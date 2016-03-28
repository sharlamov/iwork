package com.view.component.panel;

import javax.swing.*;
import java.awt.*;

public class CustomPanel extends JPanel {

    private Image backgroundImage;

    public CustomPanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, this);
    }
}
