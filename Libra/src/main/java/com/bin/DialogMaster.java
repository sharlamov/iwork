package com.bin;

import com.util.Fonts;
import com.util.Libra;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class DialogMaster {

    private static boolean res = false;

    public static boolean createFixDialog(String title, int weight, List<BufferedImage> images) {
        int dWidth = 600;
        int dHeight = 300;
        res = false;

        final JDialog dialog = new JDialog((Frame) null, title);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());

        if (!images.isEmpty()) {
            JPanel fotoPanel = new JPanel();
            for (BufferedImage image : images) {
                JLabel label = new JLabel(new ImageIcon(image.getScaledInstance(280, 200, Image.SCALE_FAST)));
                label.setPreferredSize(new Dimension(280, 200));
                fotoPanel.add(label);
            }

            fotoPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            dialog.add(fotoPanel, BorderLayout.CENTER);
        }


        JPanel qPanel = new JPanel(new FlowLayout());
        qPanel.setPreferredSize(new Dimension(dWidth, 50));
        qPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JLabel label = new JLabel(Libra.lng("scale.fixedweight") + " (" + weight + ")");
        label.setFont(Fonts.bold18);
        qPanel.add(label);

        JButton bSave = new JButton(Libra.lng("save"));
        bSave.setPreferredSize(Libra.buttonSize);
        qPanel.add(bSave);
        bSave.addActionListener(e -> {
            res = true;
            dialog.dispose();
        });

        JButton bCancel = new JButton(Libra.lng("cancel"));
        bCancel.setPreferredSize(Libra.buttonSize);
        qPanel.add(bCancel);
        bCancel.addActionListener(e -> dialog.dispose());

        dialog.add(qPanel, BorderLayout.SOUTH);
        dialog.setSize(dWidth, dHeight);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        return res;
    }
}
