package md.bin;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Vision extends Frame{

    private static final int width = 1050;

    private static final int height = 600;

    private JPanel pane;

    private JpegStream stream;

    public Vision()  {
        super("Direct Media Player");
        setBounds(100, 100, width, height);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stream.stop();
                System.exit(0);
            }
        });

        try {
            pane = new JPanel();
            initJpegPanel("http://192.168.0.57/image");
            //initJpegPanel("http://192.168.0.37/image");
        } catch (Exception e) {
            e.printStackTrace();
        }


        add(pane);
        setVisible(true);
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Vision();
            }
        });
    }

    public void initJpegPanel(String path) throws Exception {
        final Display display = new Display(500, 300);

        pane.add(display);

        JButton btn = new JButton("Snapshot");
        btn.addActionListener(new ActionListener() {
            int i;
            public void actionPerformed(ActionEvent e) {
                BufferedImage img = display.getSnapShot();
                if (img != null) {
                    try {
                        ImageIO.write(img, "jpg", new File((i++) + ".jpg"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        pane.add(btn);

        JButton btn1 = new JButton("info");
        btn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stream.info();
            }
        });
        pane.add(btn1);

        stream = new JpegStream(path);
        stream.addEventListener(display);
        stream.start();
    }

    public void initRTSPPanel() {
        final VideoSurfacePanel videoSurface = new VideoSurfacePanel("rtsp://dev:dev@192.168.1.251", 300, 200);

        pane = new JPanel();
        pane.add(videoSurface);

        JButton btn = new JButton("Snapshot");
        btn.addActionListener(new ActionListener() {
            int i;

            public void actionPerformed(ActionEvent e) {
                BufferedImage img = videoSurface.getSnapShot();
                if (img != null) {
                    try {
                        ImageIO.write(img, "jpg", new File((i++) + ".jpg"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        pane.add(btn);
    }
}

