package com.test.editor;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;


class MouseResizable extends MouseInputAdapter {

    private int cursor;
    private Point startPos = null;
    private FormEditor editor;
    private int minSize = 20;

    MouseResizable(FormEditor editor) {
        this.editor = editor;
    }

    private void resize(JComponent comp) {
        if (comp.getParent() != null) {
            comp.getParent().revalidate();
            editor.updateBounds(comp);
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        JComponent comp = (JComponent) me.getSource();
        if (comp.equals(editor.getSelected())) {
            ResizableBorder border = (ResizableBorder) comp.getBorder();
            comp.setCursor(Cursor.getPredefinedCursor(border.getCursor(me)));
        }
    }

    @Override
    public void mouseExited(MouseEvent me) {
        JComponent comp = (JComponent) me.getSource();
        comp.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mousePressed(MouseEvent me) {
        JComponent comp = (JComponent) me.getSource();
        ResizableBorder border = (ResizableBorder) comp.getBorder();
        cursor = border.getCursor(me);
        startPos = me.getPoint();

        //comp.requestFocus();
        editor.selectObject(comp);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        JComponent comp = (JComponent) me.getSource();
        if (startPos != null) {

            int x = comp.getX();
            int y = comp.getY();
            int w = comp.getWidth();
            int h = comp.getHeight();

            int dx = me.getX() - startPos.x;
            int dy = me.getY() - startPos.y;

            switch (cursor) {
                case Cursor.N_RESIZE_CURSOR:
                    if (!(h - dy < minSize)) {
                        comp.setBounds(x, y + dy, w, h - dy);
                        resize(comp);
                    }
                    break;

                case Cursor.S_RESIZE_CURSOR:
                    if (!(h + dy < minSize)) {
                        comp.setBounds(x, y, w, h + dy);
                        startPos = me.getPoint();
                        resize(comp);
                    }
                    break;

                case Cursor.W_RESIZE_CURSOR:
                    if (!(w - dx < minSize)) {
                        comp.setBounds(x + dx, y, w - dx, h);
                        resize(comp);
                    }
                    break;

                case Cursor.E_RESIZE_CURSOR:
                    if (!(w + dx < minSize)) {
                        comp.setBounds(x, y, w + dx, h);
                        startPos = me.getPoint();
                        resize(comp);
                    }
                    break;

                case Cursor.NW_RESIZE_CURSOR:
                    if (!(w - dx < minSize) && !(h - dy < minSize)) {
                        comp.setBounds(x + dx, y + dy, w - dx, h - dy);
                        resize(comp);
                    }
                    break;

                case Cursor.NE_RESIZE_CURSOR:
                    if (!(w + dx < minSize) && !(h - dy < minSize)) {
                        comp.setBounds(x, y + dy, w + dx, h - dy);
                        startPos = new Point(me.getX(), startPos.y);
                        resize(comp);
                    }
                    break;

                case Cursor.SW_RESIZE_CURSOR:
                    if (!(w - dx < minSize) && !(h + dy < minSize)) {
                        comp.setBounds(x + dx, y, w - dx, h + dy);
                        startPos = new Point(startPos.x, me.getY());
                        resize(comp);
                    }
                    break;

                case Cursor.SE_RESIZE_CURSOR:
                    if (!(w + dx < minSize) && !(h + dy < minSize)) {
                        comp.setBounds(x, y, w + dx, h + dy);
                        startPos = me.getPoint();
                        resize(comp);
                    }
                    break;

                case Cursor.MOVE_CURSOR:
                    Rectangle bounds = comp.getBounds();
                    bounds.translate(dx, dy);
                    comp.setBounds(bounds);
                    resize(comp);
            }

            comp.setCursor(Cursor.getPredefinedCursor(cursor));
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        startPos = null;
    }

}