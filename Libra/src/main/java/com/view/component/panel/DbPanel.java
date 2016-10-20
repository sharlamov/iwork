package com.view.component.panel;

import com.bin.InsertDialog;
import com.enums.InsertType;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.db.editors.IEdit;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DbPanel extends JPanel {

    private List<IEdit> edits;
    private List<Component> comps;

    public DbPanel(int pWidth, int pHeight) {
        super(null);
        setSize(pWidth, pHeight);

        edits = new ArrayList<>();
        comps = new ArrayList<>();
    }

    public void refresh() {
        edits.forEach(IEdit::refresh);
    }

    public void refresh(String name) {
        for (IEdit edit : edits) {
            if (edit.getName().equalsIgnoreCase(name)) {
                edit.refresh();
                break;
            }
        }
    }

    public boolean verify() {
        boolean result = true;
        for (IEdit edit : edits) {
            if (!edit.verify()) {
                result = false;
            }
        }
        return result;
    }

    public void blockPanel() {
        for (IEdit edit : edits) {
            edit.setChangeable(false);
        }
        for (Component comp : comps) {
            comp.setEnabled(false);
        }
    }

    public void setValue(String name, Object value) {
        edits.stream().filter(edit -> edit.getName().equalsIgnoreCase(name)).forEach(edit -> edit.setValue(value));
        comps.stream().filter(comp -> comp instanceof JLabel && value != null).forEach(comp -> {
            JLabel label = (JLabel) comp;
            if (label.getName() != null && label.getName().equalsIgnoreCase(name)) {
                label.setText(value.toString());
            }
        });
    }

    @Override
    public Component add(Component comp) {
        if (comp instanceof IEdit)
            edits.add((IEdit) comp);
        else
            comps.add(comp);

        return super.add(comp);
    }

    public void addToGroup(JPanel panelTo, Component comp) {
        if (comp instanceof IEdit)
            edits.add((IEdit) comp);
        else
            comps.add(comp);

        panelTo.add(comp);
    }

    public JPanel createPanel(int count, String title) {
        int pHeight = 0;
        JPanel p0 = new JPanel(null);

        for (int i = 0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            if (comp instanceof JPanel) {
                pHeight += comp.getHeight();
            }
        }

        if (title != null) {
            TitledBorder tb = new TitledBorder(title);
            tb.setTitleColor(Color.decode("#006600"));
            p0.setBorder(tb);
            p0.setBounds(0, pHeight, getWidth() - 15, count * 40 + 7);
        } else {
            p0.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            p0.setBounds(0, pHeight, getWidth(), count * 30 + 7);
        }

        this.add(p0);
        return p0;
    }

    public void addToPanel(int x, int y, int size, JPanel panelTo, JComponent comp) {
        String name = comp.getName();
        if (name != null) {
            String text = Libra.lng(comp.getName());
            if (text.length() > 0 && !name.equals(text)) {
                JLabel label = new JLabel(Libra.lng(comp.getName()));
                label.setBounds(x, y, 100, 23);
                panelTo.add(label);
                x += 110;
            }
        }
        comp.setBounds(x, y, size, 23);
        panelTo.add(comp);

        if (comp instanceof IEdit) {
            edits.add((IEdit) comp);
        }
    }

    public void addInsertBtn(final IEdit edit, final InsertType type) {
        JButton btn = new JButton(Pictures.saveIcon);
        //final JPanel parent = this;
        btn.addActionListener(e -> new InsertDialog("add", type, edit, this));

        addEditBtn(edit, btn);
    }

    public void addEditBtn(final IEdit edit, JButton btn) {
        final Component comp = (Component) edit;
        btn.setBounds(comp.getX() + comp.getWidth() + 2, comp.getY(), 24, 24);
        comp.getParent().add(btn);
        comps.add(btn);
    }
}
