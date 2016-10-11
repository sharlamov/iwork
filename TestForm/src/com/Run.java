package com;


import javax.swing.*;
import java.awt.*;

public class Run extends JFrame { //�������� �� JFrame �� �������� ��� ���������������� ����

    public Run(){
        super("My First Window"); //��������� ����

        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        CustomPanel panel = new CustomPanel();
/*
        JLabel label = new JLabel("test label");
        label.setBounds(50, 10, 100, 25);
        label.setBackground(Color.BLUE);
        label.setOpaque(true);
        panel.add(label);

        JButton btn = new JButton("test button");
        btn.setBounds(100, 100, 100, 50);
        btn.setBackground(Color.cyan);
        panel.add(btn);
*/
        add(panel);

        panel.load();
    }

    public static void main(String[] args) { //��� ������� ����� ���� � � ������ ������
        Run app = new Run(); //������� ��������� ������ ����������
        app.setVisible(true); //� ����� ������� ���������� ��������!
    }
}