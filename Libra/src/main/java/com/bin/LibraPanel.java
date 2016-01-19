package com.bin;

import com.dao.DataSetCellRenderer;
import com.dao.DataSetTableModel;
import com.enums.ArmType;
import com.enums.SearchType;
import com.model.DataSet;
import com.service.LibraService;
import com.toedter.calendar.JDateChooser;
import com.util.Libra;
import com.view.editor.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.Date;

public class LibraPanel extends JPanel implements ActionListener, ListSelectionListener {

    private Date dc1 = new Date();
    private Date dc2 = new Date();
    private JButton addBtn = new JButton(Libra.createImageIcon("images/add.png"));
    private JButton refreshBtn = new JButton(Libra.createImageIcon("images/reload.png"));
    private JButton filterBtn = new JButton(Libra.createImageIcon("images/filter.png"));
    private JTable tbl;
    private DataSetTableModel dtm;
    private Dimension dateSize = new Dimension(200, 25);
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
        tbl.getSelectionModel().addListSelectionListener(this);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setDefaultRenderer(Object.class, new DataSetCellRenderer());
        tbl.setFillsViewportHeight(true);
        tbl.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if (me.getClickCount() == 2) {
                    openEditDialog(dtm.getDataSetByRow(row));
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

    }

    private void tableKeyBindings(JTable table) {
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table.getActionMap().put("Enter", new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                int row = tbl.getSelectedRow();
                if (row != -1)
                    openEditDialog(dtm.getDataSetByRow(row));
            }
        });
    }

    public JToolBar initToolBar() {
        addBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        filterBtn.addActionListener(this);

        JToolBar toolBar = new JToolBar(SwingConstants.HORIZONTAL);
        toolBar.setFloatable(false);

        toolBar.add(addBtn);
        toolBar.add(refreshBtn);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(filterBtn);

        return toolBar;
    }

    public void refreshMaster() {
        try {
            dtm.setData(
                    armType == ArmType.IN ?
                            Libra.libraService.getScaleIn(dc1, dc2, LibraService.user) :
                            Libra.libraService.getScaleOut(dc1, dc2, LibraService.user)
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
            openEditDialog(null);
        } else if (e.getSource().equals(refreshBtn)) {
            refreshMaster();
        } else if (e.getSource().equals(filterBtn)) {
            openFilterDialog();
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int n = tbl.getSelectedRow();
            if (n != -1)
                refreshDetail(n);
        }
    }

    private void openFilterDialog() {
        JDateChooser date1 = new JDateChooser(dc1);
        date1.setPreferredSize(dateSize);
        JDateChooser date2 = new JDateChooser(dc2);
        date2.setPreferredSize(dateSize);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(date1);
        panel.add(date2);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Период", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (dc1.compareTo(date1.getDate()) != 0 || dc2.compareTo(date2.getDate()) != 0) {
                dc1 = date1.getDate();
                dc2 = date2.getDate();
                refreshMaster();
            }
        }
    }

    public void openEditDialog(DataSet dataSet) {

        long t = System.currentTimeMillis();

        String title = armType == ArmType.IN ? "Приход" : "Расход";

        AbstractEdit[] edits = {
                new StringEdit("sofer", dataSet),
                new StringEdit("auto", dataSet),
                new StringEdit("nr_remorca", dataSet),
                new StringEdit("vin", dataSet),
                new ListEdit("clcdep_postavt", dataSet, Libra.libraService, SearchType.CROPS),
                new ListEdit("clcppogruz_s_12t", dataSet, Libra.libraService, SearchType.CROPS),
                new ListEdit("clcsc_mpt", dataSet, Libra.libraService, SearchType.CROPS),
                new NumberEdit("sezon_yyyy", dataSet),
                new StringEdit("ttn_n", dataSet),
                new DateEdit("ttn_data", dataSet),
                new NumberEdit("masa_ttn", dataSet),
                new NumberEdit("nr_analiz", dataSet),
                new NumberEdit("masa_brutto", dataSet),
                new NumberEdit("masa_tara", dataSet),
                new NumberEdit("masa_netto", dataSet),
                new ListEdit("clcdep_gruzootpravitt", dataSet, Libra.libraService, SearchType.CROPS),
                new ListEdit("clcdep_transpt", dataSet, Libra.libraService, SearchType.CROPS),
                new ListEdit("clcdep_hozt", dataSet, Libra.libraService, SearchType.CROPS),
                new StringEdit("contract_nr", dataSet),
                new StringEdit("contract_nrmanual", dataSet),
                new DateEdit("contract_data", dataSet),
                new StringEdit("nr_act_nedostaci", dataSet),
                new NumberEdit("masa_return", dataSet),
                new StringEdit("nr_act_nedovygruzki", dataSet),
                new ListEdit("clcelevatort", dataSet, Libra.libraService, SearchType.CROPS),
        };

        new LibraEdit(title, dataSet, ArmType.IN);
        System.out.println("Dialog opened: " + (System.currentTimeMillis() - t));
    }


}
