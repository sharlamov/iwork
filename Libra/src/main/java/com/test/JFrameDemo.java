package com.test;

import com.view.component.editors.CommonEdit;
import com.view.component.editors.validators.NegativeValidator;
import com.view.component.editors.validators.NullValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JFrameDemo {
    public static void main(String s[]) {
        JFrame frame = new JFrame("JFrame Source Demo");
        frame.setLayout(new FlowLayout());
        // Add a window listner for close button

        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        final CommonEdit fld = new CommonEdit("test");
        fld.setPreferredSize(new Dimension(175, 27));
        fld.addValidator(new NullValidator("Поле должно быть заполнено!"));
        fld.addValidator(new NegativeValidator("Поле должно быть отрицательным!"));
        frame.add(fld);


        final JButton btn = new JButton("pppp");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fld.verify();
            }
        });

        frame.add(btn);


        frame.setVisible(true);
    }
}
