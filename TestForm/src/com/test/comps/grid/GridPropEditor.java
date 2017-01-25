package com.test.comps.grid;

import com.test.editor.JFontChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class GridPropEditor extends JDialog implements FocusListener {

    private DefaultListModel<GridProps.Field> listModel;
    private JList<GridProps.Field> list;
    private GridProps props;
    private JCheckBox useBgColor;
    private JCheckBox useSorting;
    private JCheckBox useSummary;
    private JTextArea query;

    private void swapElements(/*int pos1, int pos2*/int step) {
        int fStep = list.getSelectedIndex();
        if (fStep > -1) {
            int lStep = fStep + step;
            if ((step > 0 && lStep < listModel.size()) || (step < 0 && fStep != 0)){
                GridProps.Field tmp = listModel.get(lStep);
                listModel.set(lStep, listModel.get(fStep));
                listModel.set(fStep, tmp);
//                list.revalidate();
//                list.repaint();
                list.setSelectedIndex(lStep);
            }
        }
    }

    public GridPropEditor(GridProps props) {
        super((Frame) null, "Grid properties");
        this.props = props;

        listModel = new DefaultListModel<>();
        for (int i = 0; i < props.size(); i++)
            listModel.addElement(props.get(i));

        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);


        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(200, 50));
        getContentPane().add(listScroller, BorderLayout.WEST);

        JPanel info = new JPanel(null);
        useBgColor = new JCheckBox("UseBgColor");
        useBgColor.setBounds(90, 20, 120, 20);
        useBgColor.setSelected(props.useBgColor);
        info.add(useBgColor);

        useSorting = new JCheckBox("UseSorting");
        useSorting.setBounds(90, 60, 120, 20);
        useSorting.setSelected(props.useSorting);
        info.add(useSorting);

        useSummary = new JCheckBox("UseSummary");
        useSummary.setBounds(90, 100, 120, 20);
        useSummary.setSelected(props.useSummary);
        info.add(useSummary);

        JButton addFld = new JButton("+");
        addFld.setBounds(5, 20, 45, 30);
        addFld.addActionListener(e -> {
            listModel.addElement(props.create("Field", null, 50));
            list.setSelectedIndex(listModel.size() - 1);
        });
        info.add(addFld);

        JButton delFld = new JButton("-");
        delFld.addActionListener(e -> {
            int n = list.getSelectedIndex();
            if (n != -1) {
                listModel.remove(n);
            }
        });
        delFld.setBounds(5, 60, 45, 30);
        info.add(delFld);

        JButton up = new JButton("↑");
        up.addActionListener(e -> swapElements(-1));
        up.setBounds(5, 100, 45, 30);
        info.add(up);

        JButton down = new JButton("↓");
        down.addActionListener(e -> swapElements(1));
        down.setBounds(5, 140, 45, 30);
        info.add(down);

        JTextField tf1 = new JTextField(props.stdNumberFormat.toPattern());
        tf1.setBounds(220, 20, 100, 25);
        info.add(tf1);

        JTextField tf2 = new JTextField(props.stdDateFormat.toPattern());
        tf2.setBounds(220, 55, 100, 25);
        info.add(tf2);

        query = new JTextArea(5, 20);
        query.setText(props.query);
        JScrollPane scrollPane = new JScrollPane(query);
        scrollPane.setBounds(350, 20, 220, 150);
        info.add(scrollPane);

        JLabel label0 = new JLabel("Name");
        label0.setBounds(20, 200, 100, 25);
        info.add(label0);
        JLabel label1 = new JLabel("Font");
        label1.setBounds(20, 230, 100, 25);
        info.add(label1);
        JLabel label2 = new JLabel("Size");
        label2.setBounds(20, 260, 100, 25);
        info.add(label2);


        JTextField tfName = new JTextField("");
        tfName.setBounds(150, 200, 100, 25);
        tfName.addFocusListener(this);
        info.add(tfName);

        JButton btnFont = new JButton();
        btnFont.setBounds(150, 230, 250, 25);
        btnFont.addActionListener(a -> {
            int n = list.getSelectedIndex();
            if (n != -1) {
                JFontChooser c = new JFontChooser(listModel.getElementAt(n).font);
                Font f = c.showDialog(null, "Choose font");
                listModel.getElementAt(n).font = f;
                btnFont.setText(fontToString(f));
            } else {
                JOptionPane.showInternalMessageDialog(this, "Choose field");
            }
        });
        info.add(btnFont);

        SpinnerModel spinnerModel = new SpinnerNumberModel(10, 0, 1000, 10);
        JSpinner spinner = new JSpinner(spinnerModel);
        spinner.addChangeListener(e -> {
            int n = list.getSelectedIndex();
            if (n != -1) {
                listModel.getElementAt(n).size = (Integer) spinner.getValue();
            } else {
                JOptionPane.showInternalMessageDialog(this, "Choose field");
            }
        });
        spinner.setBounds(150, 260, 100, 25);
        info.add(spinner);

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                GridProps.Field f = list.getSelectedValue();
                tfName.setText(f.name);
                btnFont.setText(fontToString(f.font));
                spinner.setValue(f.size);
            }
        });

        if (!listModel.isEmpty())
            list.setSelectedIndex(0);

        JButton save = new JButton("SAVE");
        save.addActionListener(e -> {
            props.useBgColor = useBgColor.isSelected();
            props.useSorting = useSorting.isSelected();
            props.useSummary = useSummary.isSelected();
            props.query = query.getText();

            props.stdNumberFormat = new DecimalFormat(tf1.getText());
            props.stdDateFormat = new SimpleDateFormat(tf2.getText());

            props.clear();
            for (int i = 0; i < listModel.size(); i++)
                props.add(listModel.elementAt(i));

            System.out.println("Saved!!!");
            dispose();
        });
        save.setBounds(470, 250, 100, 30);
        info.add(save);


        getContentPane().add(info, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private String fontToString(Font f) {
        return f != null ? (f.getName() + ", " + (f.isBold() ? "bold" : "") + (f.isItalic() ? "italic" : "plain") + ", " + f.getSize()) : "";
    }

    public GridProps showDialog() {
        setModal(true);
        setSize(800, 330);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        return props;
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        JTextField comp = (JTextField) e.getSource();
        int n = list.getSelectedIndex();
        if (n != -1) {
            listModel.getElementAt(n).name = comp.getText();
            list.revalidate();
            list.repaint();
            //listModel.setElementAt(listModel.getElementAt(n), n);
        } else {
            JOptionPane.showInternalMessageDialog(this, "Choose field");
        }
    }
}
