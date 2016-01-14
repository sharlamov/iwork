package com.bin;

import com.enums.SearchType;
import com.model.CustomItem;
import com.service.LibraService;
import com.view.component.autocomplete.AutoSuggestBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Test extends JFrame implements ActionListener {
    AutoSuggestBox combo;

    public Test() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(100, 100, 621, 400);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        List<CustomItem> myWords = new ArrayList<CustomItem>();

        myWords.add(new CustomItem(new BigDecimal(1), "bike"));
        myWords.add(new CustomItem(new BigDecimal(2), "car"));
        myWords.add(new CustomItem(new BigDecimal(3), "cap"));
        myWords.add(new CustomItem(new BigDecimal(4), "Cape"));
        myWords.add(new CustomItem(new BigDecimal(5), "canadian"));
        myWords.add(new CustomItem(new BigDecimal(6), "caprecious"));
        myWords.add(new CustomItem(new BigDecimal(7), "catepult"));

        LibraService service = new LibraService();
        combo = new AutoSuggestBox(service, SearchType.CROPS);
        combo.setPreferredSize(new Dimension(200, 25));
        add(combo);
        JButton b = new JButton("Check");
        b.addActionListener(this);
        add(b);
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
        System.out.println(combo.getSelectedItem());
    }
}
