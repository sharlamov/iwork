package com.test;

import com.test.comps.TButton;
import com.test.comps.TLabel;
import com.test.comps.TPanel;
import com.test.table.PropTable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//apply properties
//add prop opaque
//add dataset

public class FormEditor extends JFrame {

    private JPanel desktop;
    private Map<String, Class<?>> compsMap;
    private Class<?>[] classes = {TPanel.class, TLabel.class, TButton.class};
    //private TPropPanel propPanel;
    private PropTable pt;
    private ComponentTransferHandler cth = new ComponentTransferHandler();
    private ResizableBorder rb = new ResizableBorder(this);
    public Component selected;

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
    }

    private void save() {
        long t = System.currentTimeMillis();
        // TProp prop = pan.save();

        System.out.println(System.currentTimeMillis() - t);
        //JsonService.saveFile(prop, "designs.json");
    }

    private void load() {
        desktop.remove(desktop);
        repaint();
        desktop = null;


        try {
            //TProp designs = JsonService.loadFile(TProp.class, "designs.json");
            long t = System.currentTimeMillis();

            ///pan = new TPanel(designs);
            //getContentPane().add(pan);
            //repaint();
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

        desktop = new JPanel(null);
        desktop.setBackground(Color.lightGray);
        desktop.setTransferHandler(new ComponentTransferHandler());

        pt = new PropTable();
        pt.setPreferredSize(new Dimension(200, 25));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, desktop, pt);
        splitPane.setResizeWeight(.99d);
        add(splitPane, BorderLayout.CENTER);

    }

    public void selectObject(Component cmp) {
        selected = cmp;
        pt.publish(cmp);
        clear(desktop);
    }

    public void clear(Component component) {
        //System.out.println(component.getClass());
        if (!component.equals(selected)) {
            component.repaint();
        }

        for (Component c : ((Container) component).getComponents()) {
            clear(c);
        }
    }

    public void createComp(String key, Point point, Container parent) {
        Class<?> clazz = compsMap.get(key);
        try {
            JComponent comp = (JComponent) clazz.newInstance();
            comp.setBounds((int) point.getX(), (int) point.getY(), 100, 50);

            MouseResizable mr = new MouseResizable(this);
            comp.addMouseListener(mr);
            comp.addMouseMotionListener(mr);
            //comp.setFocusable(true);
            //comp.setVisible(true);
            comp.setTransferHandler(cth);
            comp.setBorder(rb);

            parent.add(comp);

            selectObject(comp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCompList() throws ClassNotFoundException {
        compsMap = Stream.of(classes).collect(Collectors.toMap(Class::getSimpleName, i -> i));
    }

    private class ComponentTransferHandler extends TransferHandler {

        final DataFlavor SUPPORTED_DATE_FLAVOR = DataFlavor.stringFlavor;

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(SUPPORTED_DATE_FLAVOR);
        }

        @Override
        public boolean importData(TransferSupport support) {
            boolean accept = false;
            if (canImport(support)) {
                try {
                    Transferable t = support.getTransferable();
                    Object value = t.getTransferData(SUPPORTED_DATE_FLAVOR);
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


