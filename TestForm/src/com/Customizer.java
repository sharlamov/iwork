package com;

import javax.swing.*;
import java.awt.*;

/**
 * Created by sharlamov on 10.09.2016.
 */
public interface Customizer {

    default void load(JComponent comp){
        System.out.println("Loading");
        comp.setBackground(Color.lightGray);
        comp.setBounds(0, 0, 300, 200);

        //add comps

    }

    default void save(){
        System.out.println("Saving");
    }
}
