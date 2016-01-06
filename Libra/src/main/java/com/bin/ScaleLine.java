package com.bin;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Created by sharlamov on 29.12.2015.
 */
public class ScaleLine extends JPanel{

    public ScaleLine() {
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        add(new Scoreboard("Весы 1"));
        add(new Scoreboard("Весы 2"));
    }
}
