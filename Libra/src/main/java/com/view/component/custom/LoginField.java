package com.view.component.custom;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class LoginField extends JPasswordField {

    private char fecho = (char) 0;
    private char techo = (char) 8226;
    private boolean hide;
    private String placeholder;
    private Color dcolor = new Color(160, 160, 160);

    public LoginField(final String pText) {
        super(pText);
        setEchoChar(hide ? techo : fecho);
    }

    public LoginField(final String pText, boolean hide) {
        super(pText);
        this.hide = hide;
        setEchoChar(hide ? techo : fecho);
    }

    public void setPlaceholder(final String s) {
        placeholder = s;
        repaint();
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (placeholder.length() == 0 || getPassword().length > 0) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(dcolor);
        g.drawString(placeholder, getInsets().left, pG.getFontMetrics()
                .getMaxAscent() + getInsets().top);
    }

    public String getString() {
        return new String(getPassword());
    }
}