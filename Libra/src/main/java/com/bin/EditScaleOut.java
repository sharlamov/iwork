package com.bin;

import javax.swing.*;
import java.awt.*;

/**
 * Created by sharlamov on 29.12.2015.
 */
public class EditScaleOut extends JDialog {

    JPanel board = new JPanel();

    public EditScaleOut() {
        super();
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        board.setLayout(new BorderLayout());

        setVisible(true);
    }
}
