package com.serialcomm.bin;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Updater implements Runnable {

    private File src;
    private File dst;
    private File temp;

    Updater(String url) {
        src = new File(url);
        dst = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        temp = new File(dst.getPath() + "/temp");
    }

    private long filter(List<File> srcList) {
        long checkSum = 0L;

        for (int i = 0; i < srcList.size(); ) {
            File srcFile = srcList.get(i);
            File dstFile = new File(dst.getPath() + srcFile.getPath().replace(src.getPath(), ""));
            if (!dstFile.exists() || dstFile.lastModified() < srcFile.lastModified()) {
                checkSum += srcFile.length();
                i++;
            } else {
                srcList.remove(i);
            }
        }
        return checkSum;
    }

    void updating() throws IOException {
        long t = System.currentTimeMillis();

        List<File> srcList = listFiles(src, new ArrayList<>());
        System.out.println(srcList.size() + " - size before filter");
        long checkSum = filter(srcList);
        System.out.println(srcList.size() + " - size after filter");

        if (checkSum > 0) {
            deleteFile(temp);
            long tempSum = copyFiles(src, temp, srcList);
            if (checkSum == tempSum)
                restart(this);
        }

        System.out.println("Update: " + (System.currentTimeMillis() - t));
    }

    private void restart(Runnable runBeforeRestart) throws IOException {

        try {
            // java binary
            String java = System.getProperty("java.home") + "/bin/java";
            // vm arguments
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            StringBuilder vmArgsOneLine = new StringBuilder();
            // if it's the agent argument : we ignore it otherwise the
            // address of the old application and the new one will be in conflict
            vmArguments.stream().filter(arg -> !arg.contains("-agentlib")).forEach(arg -> {
                vmArgsOneLine.append(arg);
                vmArgsOneLine.append(" ");
            });
            // init the command to execute, add the vm args
            final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);

            // program main and program arguments
            String[] mainCommand = System.getProperty("sun.java.command").split(" ");
            // program main is a jar
            if (mainCommand[0].endsWith(".jar")) {
                // if it's a jar, add -jar mainJar
                cmd.append("-jar ").append(new File(mainCommand[0]).getPath());
            } else {
                // else it's a .class, add the classpath and mainClass
                cmd.append("-cp \"").append(System.getProperty("java.class.path")).append("\" ").append(mainCommand[0]);
            }
            // finally add program arguments
            for (int i = 1; i < mainCommand.length; i++) {
                cmd.append(" ");
                cmd.append(mainCommand[i]);
            }
            // execute the command in a shutdown hook, to be sure that all the
            // resources have been disposed before restarting the application
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        Runtime.getRuntime().exec(cmd.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            // execute some custom code before restarting
            if (runBeforeRestart != null)
                runBeforeRestart.run();

            System.exit(0);
        } catch (Exception e) {
            // something went wrong
            throw new IOException("Error while trying to restart the application", e);
        }
    }

    @Override
    public void run() {
        try {
            copyFiles(temp, dst, listFiles(temp, new ArrayList<>()));
            deleteFile(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteFile(File f) {
        if (!f.exists())
            return;

        if (f.isDirectory()) {
            File[] fls = f.listFiles();
            if (fls != null) {
                for (File c : fls)
                    deleteFile(c);
            }
        }
        f.delete();
    }


    private void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    private long copyFiles(File src, File dst, List<File> updList) throws IOException {
        long tempSum = 0L;
        for (File srcFile : updList) {
            File dstFile = new File(dst.getPath() + srcFile.getPath().replace(src.getPath(), ""));
            dstFile.getParentFile().mkdirs();
            copyFile(srcFile, dstFile);
            tempSum += dstFile.length();
        }
        return tempSum;
    }

    private <T extends Collection<File>> T listFiles(File file, T files) {
        File[] list = file.listFiles();
        if (list != null) {
            for (File f : list) {
                if (f.isDirectory())
                    listFiles(f, files);
                else
                    files.add(f);
            }
        }
        return files;
    }
}