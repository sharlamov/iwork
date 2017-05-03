package com.test.editor;

import com.test.comps.*;
import com.test.comps.interfaces.ISettings;
import com.test.comps.service.JsonService;
import com.test.table.PropTable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//add dataset

public class FormEditor extends JFrame {

    private TPanel desktop;
    private Map<String, Class<?>> compsMap;
    private Class<?>[] classes = {TPanel.class, TLabel.class, TextDbEdit.class, NumberDbEdit.class, CheckDbEdit.class, SearchDbEdit.class, DateDbEdit.class};
    private PropTable pt;
    private ComponentTransferHandler cth = new ComponentTransferHandler();
    private ResizableBorder rb = new ResizableBorder(this);
    private Component selected;

    /**
     * Launch the application.
     */
    public static void main(String[] args) throws Exception {
        FormEditor window = new FormEditor();
        window.setVisible(true);
    }

    /**
     * Create the application.
     */
    private FormEditor() throws Exception {
        initCompList();
        initialize();
    }

    private void deleteComp() {
        if (selected == null)
            return;

        Container cont = selected.getParent();
        cont.remove(selected);
        cont.repaint();
        selected = null;
        pt.publish(null);
    }

    private void save() {
        long t = System.currentTimeMillis();
        ISettings pan = (ISettings) desktop.getComponent(0);
        if (pan != null) {
            TProp prop = pan.save();
            JsonService.saveFile(prop, "designs.json");
        }
        System.out.println(System.currentTimeMillis() - t);
    }

    private void load() {
        desktop.removeAll();
        desktop.repaint();
        //desktop.remove(desktop);




  /*   Gson gson = new Gson();
        Font f = new Font("Courier", Font.BOLD, 18);
        String str = gson.toJson(f);
        Font ideas = gson.fromJson(str, Object.class);*/

        try {
            long t = System.currentTimeMillis();
            TProp designs = JsonService.loadFile(TProp.class, "designs.json");

            ISettings pan = new TPanel(designs, null, null);
            pan.prepareComponent(designs);
            //selectObject((Component) pan);
            desktop.add((Component) pan);
            desktop.repaint();
            desktop.revalidate();
            System.out.println("Auto load: " + (System.currentTimeMillis() - t));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() throws Exception {
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        add(bar, BorderLayout.PAGE_START);

        JButton b = new JButton("Save");
        b.addActionListener(a -> save());
        bar.add(b);

        JButton b1 = new JButton("Load");
        b1.addActionListener(a -> load());
        bar.add(b1);

        JButton b2 = new JButton("Delete");
        b2.addActionListener(a -> deleteComp());
        bar.add(b2);

        bar.addSeparator();

        JList<String> myList = new JList<>(compsMap.keySet().toArray(new String[compsMap.keySet().size()]));
        myList.setPreferredSize(new Dimension(100, 100));
        myList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        myList.setDragEnabled(true);
        add(myList, BorderLayout.WEST);

        desktop = new TPanel();
        desktop.setBackground(Color.lightGray);
        desktop.setTransferHandler(new ComponentTransferHandler());

        pt = new PropTable();
        pt.setPreferredSize(new Dimension(200, 25));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, desktop, pt);
        splitPane.setResizeWeight(.99d);
        add(splitPane, BorderLayout.CENTER);

    }

    void selectObject(Component cmp) {
        if (cmp == null)
            pt.publish(null);
        else if (!cmp.equals(selected))
            pt.publish(cmp);

        selected = cmp;
        reDraw(desktop);
    }

    private void reDraw(Component cmp) {
        if (!cmp.equals(selected))
            cmp.repaint();

        Stream.of(((Container) cmp).getComponents()).forEach(this::reDraw);
    }

    private void createComp(String key, Point point, Container parent) {
        Class<?> clazz = compsMap.get(key);
        try {
            JComponent comp = (JComponent) clazz.newInstance();
            comp.setBounds((int) point.getX(), (int) point.getY(), 100, 50);

            MouseResizable mr = new MouseResizable(this);
            comp.addMouseListener(mr);
            comp.addMouseMotionListener(mr);
            comp.setTransferHandler(cth);
            comp.setBorder(rb);
            comp.setFocusable(false);
            comp.setEnabled(false);
            parent.add(comp);

            selectObject(comp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCompList() throws ClassNotFoundException {
        compsMap = Stream.of(classes).collect(Collectors.toMap(Class::getSimpleName, i -> i));
    }

    void updateBounds(Component comp) {
        if (comp.equals(selected))
            pt.updateBounds(comp.getBounds());
    }

    Component getSelected() {
        return selected;
    }

    private class ComponentTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            boolean accept = false;
            if (canImport(support)) {
                try {
                    Transferable t = support.getTransferable();
                    Object value = t.getTransferData(DataFlavor.stringFlavor);
                    if (value instanceof String) {
                        Container component = (Container) support.getComponent();
                        createComp(value.toString(), support.getDropLocation().getDropPoint(), component);
                        accept = true;
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
            return accept;
        }
    }
}


