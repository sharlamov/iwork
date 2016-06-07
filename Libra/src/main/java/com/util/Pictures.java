package com.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class Pictures {

    public static ImageIcon printerIcon = createImageIcon("images/printer.png", 24, 24);
    public static ImageIcon actionIcon = createImageIcon("images/run.png", 24, 24);
    public static ImageIcon saveIcon = createImageIcon("images/save.png", 20, 20);
    public static ImageIcon addIcon = createImageIcon("images/add.png");
    public static ImageIcon reloadIcon = createImageIcon("images/reload.png");
    public static ImageIcon halfIcon = createImageIcon("images/half.png", 100, 30);
    public static ImageIcon filterIcon = createImageIcon("images/filter.png");
    public static ImageIcon loadedIcon = createImageIcon("images/loaded.png", 100, 80);
    public static ImageIcon unloadedIcon = createImageIcon("images/unloaded.png", 100, 80);
    public static ImageIcon middleIcon = createImageIcon("images/middle.gif");
    public static ImageIcon findIcon = createImageIcon("images/find.png");
    public static ImageIcon downloadedIcon = createImageIcon("images/download.png", 20, 20);
    public static Image scaleIcon = getImage("images/scale.png");


    public static Image getImage(String path) {
        try {
            java.net.URL imgURL = Libra.class.getClassLoader().getResource(path);
            if (imgURL != null) {
                return ImageIO.read(imgURL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image getImage(String path, int x, int y) {
        try {
            java.net.URL imgURL = Libra.class.getClassLoader().getResource(path);
            if (imgURL != null) {
                return ImageIO.read(imgURL).getScaledInstance(x, y, Image.SCALE_FAST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Libra.class.getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static ImageIcon createImageIcon(String path, int x, int y) {
        Image img = getImage(path, x, y);
        return img != null ? new ImageIcon(img) : null;
    }


}
