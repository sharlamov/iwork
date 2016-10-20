package com.bin;

import com.model.settings.Settings;
import com.util.Libra;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Updater {

    private static final int BUFFER_SIZE = 1024;
    private String src;
    private String dst;
    private File update;
    private Settings settings;
    private String updNr;

    Updater(Settings settings) throws IOException {
        this.settings = settings;
    }

    boolean check() throws Exception {
        long t = System.currentTimeMillis();
        src = settings.getUpdateUrl();
        updNr = getLatestVersion();
        System.out.println("check: " + (System.currentTimeMillis() - t));
        return settings.getUpdateNr() < Long.valueOf(updNr);
    }

    void update() throws Exception {
        long t = System.currentTimeMillis();

        dst = System.getProperty("user.dir");
        update = new File(dst + "/update");

        //clean
        deleteFile(update);
        //download
        File zip = downloadPkg();
        //unZip
        unpack(zip.getPath(), update.getPath());

        System.out.println("update: " + (System.currentTimeMillis() - t));
        //restart
        restartApp();
    }

    private void apply(ProcessBuilder pb) {
        long t = System.currentTimeMillis();
        try {
            copyFiles(update, new File(dst), listFiles(update, new ArrayList<>()));
            deleteFile(update);
            pb.start();
        } catch (IOException e) {
            Libra.eMsg(e, true);
        }
        System.out.println("apply: " + (System.currentTimeMillis() - t));
    }

    private void restartApp() throws IOException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if (!currentJar.getName().endsWith(".jar"))
            return;

        final ProcessBuilder pb = new ProcessBuilder(javaBin, "-jar", currentJar.getPath(), "snap", updNr);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                apply(pb);
            }
        });

        System.out.println("exit: " + currentJar.toString());
        System.exit(0);
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
            byte[] buffer = new byte[BUFFER_SIZE];
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

    private void unpack(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        deleteFile(new File(zipFilePath));
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    private File downloadPkg() throws Exception {
        URL from = new URL(src + "/" + updNr + ".zip");
        File to = new File(update + "/" + updNr + ".zip");

        update.mkdir();
        if (!to.exists()) {
            to.createNewFile();
        }

        InputStream input = from.openStream();
        OutputStream output = new FileOutputStream(to);

        int n;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
        output.close();
        input.close();

        return to;
    }

    private String getLatestVersion() throws Exception {
        InputStream file = new URL(src + "/version.txt").openStream();

        int c = 0;
        StringBuilder buffer = new StringBuilder();

        while (c != -1) {
            c = file.read();
            buffer.append((char) c);
        }
        file.close();

        String data = buffer.toString();
        return data.substring(data.indexOf("[version]") + 9, data.indexOf("[/version]"));
    }

}//238