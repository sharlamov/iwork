package com;


import javax.swing.*;
import java.awt.*;

public class Run extends JFrame { //Наследуя от JFrame мы получаем всю функциональность окна

    public Run(){
        super("My First Window"); //Заголовок окна

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

    public static void main(String[] args) { //эта функция может быть и в другом классе
        Run app = new Run(); //Создаем экземпляр нашего приложения
        app.setVisible(true); //С этого момента приложение запущено!
    }
}