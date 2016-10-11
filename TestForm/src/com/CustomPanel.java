package com;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class CustomPanel extends JPanel{

    public CustomPanel() {
        setLayout(null);
    }

    public void load(){
        try(Reader reader = new FileReader("Output.json")){
            Gson gson = new GsonBuilder().create();
            TProps p = gson.fromJson(reader, TProps.class);

            setOpaque(true);
            setBackground(p.color);
            setName(p.name);
            setBounds(p.x, p.y, p.width, p.height);

            for (TProps c : p.comps) {
                Class<?> clazz = Class.forName(c.className);
                Component obj = (Component) clazz.newInstance();
                obj.setBackground(c.color);
                obj.setName(c.name);
                obj.setBounds(c.x, c.y, c.width, c.height);
                add(obj);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void save(){
        try (Writer writer = new FileWriter("Output.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();


            TProps props = new TProps();
            props.color = getBackground();
            props.name = getName();
            props.className = getClass().getName();

            props.x = getX();
            props.y = getY();
            props.width = getWidth();
            props.height = getHeight();
            props.comps = new ArrayList<>(getComponentCount());

            for (int i = 0; i < getComponentCount(); i++){
                Component c = getComponent(i);

                TProps props1 = new TProps();
                props1.color = c.getBackground();
                props1.name = c.getName();
                props1.className = c.getClass().getName();

                props1.x = c.getX();
                props1.y = c.getY();
                props1.width = c.getWidth();
                props1.height = c.getHeight();
                props.comps.add(props1);
            }

            gson.toJson(props, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
