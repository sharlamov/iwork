package md.bin;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class JpegStream implements Runnable {

    private String path;
    private List<VisionEventListener> listeners;
    private Thread thread;
    private URL url;

    public JpegStream(String path) {
        this.path = path;
        listeners = new ArrayList<VisionEventListener>();

        thread = new Thread(this);
    }

    public void start() {
        try {
            url = new URL(path);
            thread.start();
            //thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (thread.isAlive()){
            thread.interrupt();
        }
    }

    public void addEventListener(VisionEventListener listener) {
        listeners.add(listener);
    }

    private void fireScaleEvent(BufferedImage img) {
        for (VisionEventListener listener : listeners) {
            listener.frameObtained(img);
        }
    }

    public void run() {
        while (true) {
            try {
               long start3=System.currentTimeMillis();
                URLConnection urlConn = url.openConnection();
                urlConn.setConnectTimeout(100);
                urlConn.setReadTimeout(100);
                InputStream is = urlConn.getInputStream();
                BufferedImage img = ImageIO.read(is);
                fireScaleEvent(img);
               System.out.println("Load image time = " + (System.currentTimeMillis() - start3));
                Thread.sleep(40);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void info() {
        System.out.println(thread.getName());
        System.out.println(thread.isAlive());
        System.out.println(thread.isInterrupted());
        System.out.println(thread.isDaemon());
        System.out.println(thread.getPriority());
        System.out.println(thread.getState());
        System.out.println(thread.toString());
    }
}
