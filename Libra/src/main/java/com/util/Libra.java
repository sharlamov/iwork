package com.util;

import javax.swing.*;
import java.text.SimpleDateFormat;

/**
 * Created by sharlamov on 27.12.2015.
 */
public class Libra {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static void eMsg(String str){
        JOptionPane.showMessageDialog(null, str, "Error", JOptionPane.ERROR_MESSAGE);
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
}
