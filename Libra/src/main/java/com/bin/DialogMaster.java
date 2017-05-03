package com.bin;

import com.dao.model.DataSet;
import com.util.Fonts;
import com.util.Libra;
import com.util.Validators;
import com.view.component.db.editors.NumberDbEdit;
import com.view.component.db.editors.SearchDbEdit;
import com.view.component.panel.DbPanel;

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


    public static boolean createFixDialog(String title, DataSet dataSet) {
        int dWidth = 350;
        int dHeight = 150;
        res = false;

        final JDialog dialog = new JDialog((Frame) null, title);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        String sql = "select * from (" +
                "select cod, cod||', '||denumirea clccodt from vms_univers u where tip = 'M' and gr1='2161' and isarhiv is null\n" +
                "and exists (select * FROM tMS_UMS un where u.cod = un.cod and coef > 0)\n" +
                "union all\n" +
                "select cod, cod||', '||denumirea from vms_univers u where tip = 'P' and gr1 = 'TARA' and isarhiv is null\n" +
                "and exists (select * FROM tMS_umLinks un where u.cod = un.cod and cant_k > 0)" +
                ") where lower(clccodt) like :findQuery and rownum < 31 order by 2";
        DbPanel dbPanel = new DbPanel(dWidth, dHeight);
        JPanel pan = dbPanel.createPanel(4, null);

        SearchDbEdit sc = new SearchDbEdit("clcSct", dataSet, Libra.libraService, sql);
        sc.addValidator(Validators.NULL);
        dbPanel.addToPanel(8, 8, 200, pan, sc);

        NumberDbEdit cant = new NumberDbEdit("cant", dataSet);
        cant.addValidator(Validators.NULL);
        dbPanel.addToPanel(8, 35, 125, pan, cant);

        dialog.add(dbPanel);

        JButton btnYes = new JButton(Libra.lng("yes"));
        btnYes.setPreferredSize(Libra.buttonSize);
        btnYes.addActionListener(e -> {
            if (dbPanel.verify()) {
                res = true;
                dialog.dispose();
            }
        });
        JButton btnNo = new JButton(Libra.lng("no"));
        btnNo.setPreferredSize(Libra.buttonSize);
        btnNo.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        btnPanel.add(btnYes);
        btnPanel.add(btnNo);
        dialog.add(btnPanel, BorderLayout.SOUTH);


        dialog.setSize(dWidth, dHeight);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        return res;
    }
}
