package com.bin;

import com.enums.SearchType;
import com.util.Libra;
import com.view.component.editors.ChangeEditListener;
import com.view.component.editors.DateEdit;
import com.view.component.editors.NumberEdit;
import com.view.component.editors.SearchEdit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Test extends JFrame implements ActionListener, ChangeEditListener {

    JLabel t = new JLabel("Test");
    JButton b = new JButton("start");
    SearchEdit edit;
    NumberEdit edit1;
    DateEdit edit2;


    //insert into glosary list

    public Test() {
        Libra.dbUser = "TRANSOIL";
        Libra.dbPass = "TRANSOIL";
        Libra.dbUrl = "jdbc:oracle:thin:@192.168.1.221:1521:TRANSOIL";
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(100, 100, 621, 400);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);
        b.addActionListener(this);
        add(t);
        add(b);

        edit = new SearchEdit("clcdep_postavt", Libra.libraService, SearchType.CROPS);
        edit.setPreferredSize(new Dimension(200, 27));
        edit.addChangeEditListener(this);
        add(edit);
        edit1 = new NumberEdit("masa_brutto", Libra.decimalFormat);
        edit1.setPreferredSize(new Dimension(200, 27));
        edit1.addChangeEditListener(this);
        add(edit1);
        edit2 = new DateEdit("date_ttn", Libra.dateFormat);
        edit2.setPreferredSize(new Dimension(200, 27));
        //edit2.setValue(new Date());
        //edit2.setValue("01.01.1901");
        edit2.addChangeEditListener(this);
        add(edit2);
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Test frame = new Test();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println(edit.getValue());
        System.out.println(edit1.getValue());
        System.out.println(edit2.getValue());
    }

    public void changeEdit(Object source) {
        if (source.equals(edit)) {
            System.out.println(edit.getValue());
        } else if (source.equals(edit1)) {
            System.out.println(edit1.getValue());
        } else if (source.equals(edit2)) {
            System.out.println(edit2.getValue());
        }
    }
}

