package com.test;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

public class DragListener extends MouseInputAdapter {
    private Point location;
    private MouseEvent pressed;

    public void mousePressed(MouseEvent me) {
        pressed = me;
    }

    public void mouseDragged(MouseEvent me) {
        Component component = me.getComponent();
        location = component.getLocation(location);
        int x = location.x - pressed.getX() + me.getX();
        int y = location.y - pressed.getY() + me.getY();
        component.setLocation(x, y);
    }

    public void register(Component comp) {
        comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
    }

    public void unregister(Component comp) {
        comp.removeMouseListener(this);
        comp.removeMouseMotionListener(this);
    }
}