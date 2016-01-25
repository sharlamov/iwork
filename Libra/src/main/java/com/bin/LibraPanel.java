package com.bin;

import com.dao.DataSetCellRenderer;
import com.dao.DataSetTableModel;
import com.enums.ArmType;
import com.service.LibraService;
import com.toedter.calendar.JDateChooser;
import com.util.Libra;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Date;

public class LibraPanel extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener {

    private JDateChooser date1;
    private JDateChooser date2;
    private JButton addBtn = new JButton(Libra.createImageIcon("images/add.png"));
    private JButton refreshBtn = new JButton(Libra.createImageIcon("images/reload.png"));
    private JToggleButton halfBtn = new JToggleButton(Libra.createImageIcon("images/half.png"));
    public JTable tbl;
    private DataSetTableModel dtm;
    private Dimension dateSize = new Dimension(100, 27);
    private Dimension btnSize = new Dimension(90, 25);
    private HistoryPanel detail;
    private ArmType armType;
    private String[] fieldNamesIn = new String[]{"sofer", "auto", "nr_remorca", "vin", "clcdep_postavt", "clcppogruz_s_12t", "clcsc_mpt"
            , "sezon_yyyy", "ttn_n", "ttn_data", "masa_ttn", "nr_analiz", "masa_brutto", "masa_tara", "masa_netto", "clcdep_gruzootpravitt", "clcdep_transpt"
            , "clcdep_hozt", "time_in", "time_out", "contract_nr", "contract_nrmanual", "contract_data", "nr_act_nedostaci"
            , "masa_return", "nr_act_nedovygruzki", "clcelevatort"};
    private String[] fieldNamesOut = new String[]{"clcsofer_s_14t", "nr_vagon", "nr_remorca", "vin", "clcdep_perevoz", "clcdep_destinatt", "clcprazgruz_s_12t", "clcpunctto_s_12t", "clcsct"
            , "sezon_yyyy", "ttn_n", "ttn_data", "ttn_nn_perem", "nr_analiz", "masa_brutto", "masa_tara", "masa_netto", "prikaz_id", "prikaz_masa"
            , "print_chk", "nrdoc_out", "clcsklad_pogruzkit", "time_in", "time_out", "clcelevatort", "prparc_seria_nr", "prparc_data"};

    public LibraPanel(final ArmType armType) {
        this.armType = armType;
        this.detail = new HistoryPanel();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        dtm = new DataSetTableModel(armType == ArmType.IN ? fieldNamesIn : fieldNamesOut);
        tbl = new JTable(dtm);
        tbl.setRowSelectionAllowed(true);
        tbl.getSelectionModel().addListSelectionListener(this);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setDefaultRenderer(Object.class, new DataSetCellRenderer(armType == ArmType.IN));
        tbl.setFillsViewportHeight(true);
        tbl.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if (me.getClickCount() == 2) {
                    new LibraEdit(dtm.getDataSetByRow(row), armType);
                }
            }
        });

        tableKeyBindings(tbl);

        JScrollPane scrollPane = new JScrollPane(tbl);
        JPanel master = new JPanel(new BorderLayout());
        master.add(initToolBar(), BorderLayout.NORTH);
        master.add(scrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(master);
        splitPane.setRightComponent(detail);
        splitPane.setResizeWeight(0.8d);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        refreshMaster();

        tbl.requestFocus();
    }

    private void tableKeyBindings(JTable table) {
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table.getActionMap().put("Enter", new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                int row = tbl.getSelectedRow();
                if (row != -1)
                    new LibraEdit(dtm.getDataSetByRow(row), armType);
            }
        });

        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "Insert");
        table.getActionMap().put("Insert", new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                new LibraEdit(null, armType);
            }
        });
    }

    public JToolBar initToolBar() {
        addBtn.addActionListener(this);
        refreshBtn.addActionListener(this);

        JToolBar toolBar = new JToolBar(SwingConstants.HORIZONTAL);
        toolBar.setFloatable(false);

        toolBar.add(addBtn);
        toolBar.add(refreshBtn);

        halfBtn.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ev) {
                if (ev.getStateChange() == ItemEvent.SELECTED) {
                    filterMaster();
                    System.out.println("button is selected");
                } else if (ev.getStateChange() == ItemEvent.DESELECTED) {
                    refreshMaster();
                    System.out.println("button is not selected");
                }
            }
        });
        toolBar.addSeparator();
        halfBtn.setPreferredSize(btnSize);
        toolBar.add(halfBtn);


        toolBar.add(Box.createHorizontalGlue());
        date1 = new JDateChooser(new Date());
        date1.setMaximumSize(dateSize);
        date1.setPreferredSize(dateSize);
        date1.getDateEditor().addPropertyChangeListener(this);

        toolBar.add(date1);
        toolBar.add(new JLabel(" - "));

        date2 = new JDateChooser(new Date());
        date2.setMaximumSize(dateSize);
        date2.setPreferredSize(dateSize);
        date2.getDateEditor().addPropertyChangeListener(this);

        toolBar.add(date2);

        date1.setMaxSelectableDate(date2.getDate());
        date2.setMinSelectableDate(date1.getDate());
        return toolBar;
    }

    public void filterMaster() {
        try {
            dtm.setData(
                    armType == ArmType.IN ?
                            Libra.libraService.getScaleIn(true, date1.getDate(), date2.getDate(), LibraService.user) :
                            Libra.libraService.getScaleOut(true, date1.getDate(), date2.getDate(), LibraService.user)
            );
            refreshDetail(0);
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public void refreshMaster() {
        if (halfBtn.isSelected())
            halfBtn.setSelected(false);

        try {
            dtm.setData(
                    armType == ArmType.IN ?
                            Libra.libraService.getScaleIn(false, date1.getDate(), date2.getDate(), LibraService.user) :
                            Libra.libraService.getScaleOut(false, date1.getDate(), date2.getDate(), LibraService.user)
            );
            refreshDetail(0);
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public void refreshDetail(int row) {
        Object obj = dtm.getValueByFieldName("ID", row);
        if (obj != null && obj instanceof BigDecimal)
            detail.refreshData((BigDecimal) obj);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(addBtn)) {
            new LibraEdit(null, armType);
        } else if (e.getSource().equals(refreshBtn)) {
            refreshMaster();
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int n = tbl.getSelectedRow();
            if (n != -1)
                refreshDetail(n);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("date".equals(evt.getPropertyName())) {
            refreshMaster();
            date1.setMaxSelectableDate(date2.getDate());
            date2.setMinSelectableDate(date1.getDate());
        }
    }
}
