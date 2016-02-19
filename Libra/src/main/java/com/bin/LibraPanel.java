package com.bin;

import com.enums.ArmType;
import com.enums.SearchType;
import com.service.LibraService;
import com.toedter.calendar.JDateChooser;
import com.util.Libra;
import com.view.component.grid.DataGrid;
import com.view.component.grid.GridField;

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
import java.util.HashMap;
import java.util.Map;

public class LibraPanel extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener, ItemListener {

    private final Font headerFont = new Font("Courier", Font.BOLD, 12);
    private JDateChooser date1;
    private JDateChooser date2;
    private JButton addBtn = new JButton(Libra.createImageIcon("images/add.png"));
    private JButton refreshBtn = new JButton(Libra.createImageIcon("images/reload.png"));
    private JToggleButton halfBtn = new JToggleButton(Libra.createImageIcon("images/half.png", 100, 30));
    private DataGrid dataGrid;
    private Dimension dateSize = new Dimension(100, 27);
    private HistoryPanel detail;
    private ArmType armType;
    private LibraPanel pan;

    public LibraPanel(final ArmType armType) {
        this.armType = armType;
        this.detail = new HistoryPanel();
        this.pan = this;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        dataGrid = new DataGrid(Libra.libraService, armType == ArmType.IN ? SearchType.SCALEIN : SearchType.SCALEOUT, getFieldNames(armType), armType == ArmType.IN, true);
        dataGrid.addListSelectionListener(this);
        dataGrid.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                int row = table.rowAtPoint(me.getPoint());
                if (me.getClickCount() == 2 && row != -1) {
                    new LibraEdit(pan, dataGrid.getDataSetByRow(row), armType);
                }
            }
        });
        dataGrid.setColumnFont("masa_brutto", headerFont);
        dataGrid.setColumnFont("masa_tara", headerFont);
        dataGrid.setColumnFont("masa_netto", headerFont);

        tableKeyBindings(dataGrid);

        dataGrid.addToolBar(initToolBar());
        dataGrid.setHeaderFont(headerFont);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(dataGrid);
        splitPane.setRightComponent(detail);
        splitPane.setResizeWeight(0.8d);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        refreshMaster();
    }

    private void tableKeyBindings(final DataGrid table) {
        table.getIMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table.getAMap().put("Enter", new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                int row = table.getSelectedRow();
                if (row != -1)
                    new LibraEdit(pan, dataGrid.getDataSetByRow(row), armType);
            }
        });

        table.getIMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "Insert");
        table.getAMap().put("Insert", new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                new LibraEdit(pan, dataGrid.getDataSetByRow(-1), armType);
            }
        });

        table.getIMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK), "Find");
        table.getAMap().put("Find", new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    table.filter();
                } catch (Exception e) {
                    e.printStackTrace();
                    Libra.eMsg(e.getMessage());
                }
            }
        });

        table.getIMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "Refresh");
        table.getAMap().put("Refresh", new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                refreshMaster();
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


        toolBar.addSeparator();
        halfBtn.addItemListener(this);
        toolBar.add(halfBtn);

        Date cDate = Libra.truncDate(null);

        toolBar.add(Box.createHorizontalGlue());
        date1 = new JDateChooser("dd.MM.yyyy", "##.##.####", '_');
        date1.setDate(cDate);
        date1.setMaximumSize(dateSize);
        date1.setPreferredSize(dateSize);
        date1.getDateEditor().addPropertyChangeListener(this);

        toolBar.add(date1);
        toolBar.add(new JLabel(" - "));

        date2 = new JDateChooser("dd.MM.yyyy", "##.##.####", '_');
        date2.setDate(cDate);
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
            dataGrid.filter("(nvl(masa_brutto, 0) = 0 and nvl(masa_tara,0) != 0) or (nvl(masa_brutto, 0) != 0 and nvl(masa_tara,0) = 0)");
            refreshDetail(0);
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public void refreshMaster() {
        if (halfBtn.isSelected()) {
            halfBtn.setSelected(false);
        }

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(":date1", date1.getDate());
            params.put(":date2", date2.getDate());
            params.put(":userid", LibraService.user.getId());
            dataGrid.select(params);
            refreshDetail(0);
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public void refreshDetail(int row) {
        Object obj = dataGrid.getValueByFieldName("ID", row);
        if (obj != null && obj instanceof BigDecimal)
            detail.refreshData((BigDecimal) obj);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(addBtn)) {
            new LibraEdit(pan, dataGrid.getDataSetByRow(-1), armType);
        } else if (e.getSource().equals(refreshBtn)) {
            refreshMaster();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("date".equals(evt.getPropertyName())) {
            if (evt.getNewValue() != evt.getOldValue()) {
                refreshMaster();
                date1.setMaxSelectableDate(date2.getDate());
                date2.setMinSelectableDate(date1.getDate());
            }
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int n = dataGrid.getSelectedRow();
            if (n != -1)
                refreshDetail(n);
        }
    }

    private GridField[] getFieldNames(ArmType type) {
        if (type == ArmType.IN) {
            return new GridField[]{
                    new GridField("nr_analiz", 50),
                    new GridField("sofer", 90),
                    new GridField("auto", 100),
                    new GridField("nr_remorca", 65),
                    new GridField("clcdep_postavt", 150),
                    new GridField("clcppogruz_s_12t", 100),
                    new GridField("clcsc_mpt", 100),
                    new GridField("sezon_yyyy", 40),
                    new GridField("ttn_n", 85),
                    new GridField("ttn_data", 80),
                    new GridField("masa_ttn", 50),
                    new GridField("masa_brutto", 50),
                    new GridField("masa_tara", 50),
                    new GridField("masa_netto", 50),
                    new GridField("clcdep_gruzootpravitt", 150),
                    new GridField("clcdep_transpt", 150),
                    new GridField("clcdep_hozt", 150),
                    new GridField("time_in", 50),
                    new GridField("time_out", 50),
                    new GridField("contract_nr", 50),
                    new GridField("contract_nrmanual", 50),
                    new GridField("contract_data", 50),
                    new GridField("nr_act_nedostaci", 50),
                    new GridField("masa_return", 50),
                    new GridField("nr_act_nedovygruzki", 50),
                    new GridField("clcdivt", 50),
                    new GridField("clcelevatort", 150)};
        } else {
            return new GridField[]{
                    new GridField("nr_analiz", 50),
                    new GridField("prikaz_id", 50),
                    new GridField("clcsofer_s_14t", 90),
                    new GridField("nr_vagon", 100),
                    new GridField("nr_remorca", 65),
                    new GridField("clcdep_perevozt", 150),
                    new GridField("clcdep_destinatt", 150),
                    new GridField("clcprazgruz_s_12t", 100),
                    new GridField("clcpunctto_s_12t", 100),
                    new GridField("clcsct", 140),
                    new GridField("sezon_yyyy", 40),
                    new GridField("ttn_n", 85),
                    new GridField("ttn_data", 80),
                    new GridField("ttn_nn_perem", 85),
                    new GridField("masa_brutto", 50),
                    new GridField("masa_tara", 50),
                    new GridField("masa_netto", 50),
                    new GridField("prikaz_masa", 50),
                    new GridField("nrdoc_out", 50),
                    new GridField("clcsklad_pogruzkit", 150),
                    new GridField("time_in", 50),
                    new GridField("time_out", 50),
                    new GridField("clcelevatort", 150),
                    new GridField("prparc_seria_nr", 50),
                    new GridField("prparc_data", 50),
                    new GridField("clcdivt", 50)};
        }
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(halfBtn)) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                filterMaster();
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                refreshMaster();
            }
        }
    }
}
