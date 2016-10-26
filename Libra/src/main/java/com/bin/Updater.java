package com.bin;

import com.model.settings.Settings;
import com.util.Libra;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Updater {

    private File dst;
    private File update;
    private Settings settings;
    private String updNr;

    Updater(Settings settings) throws IOException {
        this.settings = settings;
    }

    boolean check() throws Exception {
        long t = System.currentTimeMillis();
        updNr = getLatestVersion();
        System.out.println("check: " + (System.currentTimeMillis() - t));
        return settings.getUpdateNr() < Long.valueOf(updNr);
    }

    void update() throws Exception {
        long t = System.currentTimeMillis();

        dst = new File(System.getProperty("user.dir"));
        update = new File(dst, "update");

        FileUtils.deleteDirectory(update);
        //download
        File zip = downloadPkg();
        //unZip
        unpack(zip, update);

        System.out.println("update: " + (System.currentTimeMillis() - t));
        //restart
        restartApp();
    }

    private void apply(ProcessBuilder pb) {
        long t = System.currentTimeMillis();
        try {
            FileUtils.copyDirectory(update, dst);
            FileUtils.deleteDirectory(update);
            pb.start();
        } catch (IOException e) {
            Libra.eMsg(e, true);
        }
        System.out.println("apply: " + (System.currentTimeMillis() - t));
    }

    private void restartApp() throws IOException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));

        if (!currentJar.getName().endsWith(".jar"))
            return;

        final ProcessBuilder pb = new ProcessBuilder(javaBin, "-jar", currentJar.getPath(), "snap", updNr);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                apply(pb);
            }
        });

        System.out.println("restart: " + currentJar);
        System.exit(0);
    }

    private File downloadPkg() throws Exception {
        URL from = new URL(settings.getUpdateUrl() + "/" + updNr + ".zip");

        update.mkdirs();

        File pkg = new File(update, updNr + ".zip");
        FileUtils.copyToFile(from.openStream(), pkg);
        return pkg;
    }

    private String getLatestVersion() throws Exception {
        String data = IOUtils.toString(new URL(settings.getUpdateUrl() + "/version.txt").openStream(), Charset.defaultCharset());
        return data.substring(data.indexOf("[version]") + 9, data.indexOf("[/version]"));
    }

    private void unpack(File zipFilePath, File destDir) throws IOException {
        if (!destDir.exists())
            destDir.mkdir();

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDir.getPath() + File.separator + entry.getName();
            File ff = new File(filePath);
            if (entry.isDirectory()) {
                ff.mkdir();
            } else
                FileUtils.copyToFile(zipIn, ff);//?copyFile(zipIn, ff);

            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        zipFilePath.delete();
    }
}