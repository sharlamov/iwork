package com.view.component.widget;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Display extends JPanel {

    private BufferedImage picture;

    public Display(int x, int y) {
        setBackground(Color.black);
        setOpaque(true);

        Dimension d = new Dimension(x, y);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);

        picture = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .createCompatibleImage(x, y);
    }

    public Display() {
        setBackground(Color.black);
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(picture, 0, 0, getWidth(), getHeight(), this);
        g2.dispose();
    }

    public void frameObtained(BufferedImage image) {
        picture = image;
        repaint();
    }

    public BufferedImage getPicture() {
        return picture;
    }
}
