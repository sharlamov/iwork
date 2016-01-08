package com.bin;

import com.dao.DataSetCellRenderer;
import com.dao.DataSetTableModel;
import com.service.LibraService;
import com.toedter.calendar.JDateChooser;
import com.util.Libra;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

public class LibraPanel extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener {

    private JDateChooser dc1;
    private JDateChooser dc2;
    private ImageIcon addIcon = Libra.createImageIcon("images/add.png");
    private ImageIcon refreshIcon = Libra.createImageIcon("images/reload.png");
    private JPanel master = new JPanel(new BorderLayout());
    private JButton addBtn = new JButton(addIcon);
    private JButton refreshBtn = new JButton(refreshIcon);
    private LibraService service;
    private JTable tbl;
    private DataSetTableModel dtm;
    private Dimension btnSize = new Dimension(30, 30);
    private HistoryPanel detail;
    private int armType;
    private String[] fieldNamesIn = new String[]{"sofer", "auto", "nr_remorca", "vin", "clcdep_postavt", "clcppogruz_s_12t", "clcsc_mpt"
            , "sezon_yyyy", "ttn_n", "ttn_data", "masa_ttn", "nr_analiz", "masa_brutto", "masa_tara", "masa_netto", "clcdep_gruzootpravitt", "clcdep_transpt"
            , "clcdep_hozt", "time_in", "time_out", "contract_nr", "contract_nrmanual", "contract_data", "nr_act_nedostaci"
            , "masa_return", "nr_act_nedovygruzki", "clcelevatort"};
    private String[] fieldNamesOut = new String[]{"clcsofer_s_14t", "nr_vagon", "nr_remorca", "vin", "clcdep_perevoz", "clcdep_destinatt", "clcprazgruz_s_12t", "clcpunctto_s_12t", "clcsct"
            , "sezon_yyyy", "ttn_n", "ttn_data", "ttn_nn_perem", "nr_analiz", "masa_brutto", "masa_tara", "masa_netto", "prikaz_id", "prikaz_masa"
            , "print_chk", "nrdoc_out", "clcsklad_pogruzkit", "time_in", "time_out", "clcelevatort", "prparc_seria_nr", "prparc_data"};

    public LibraPanel(final LibraService service, final int armType) {
        long t = System.currentTimeMillis();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.armType = armType;
        this.service = service;
        this.detail = new HistoryPanel(service);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        dtm = new DataSetTableModel(armType == 1 ? fieldNamesIn : fieldNamesOut);
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
                    new EditScaleOut(armType == 1 ? "Приход" : "Расход", getDtm().getDataSetByRow(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tbl);
        master.add(initToolBar(), BorderLayout.NORTH);
        master.add(scrollPane, BorderLayout.CENTER);


        splitPane.setLeftComponent(master);
        splitPane.setRightComponent(detail);
        splitPane.setResizeWeight(0.8d);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        refreshMaster();
        System.out.println("draw libra panel:" + (System.currentTimeMillis() - t));
    }

    public JToolBar initToolBar() {
        /*date picker*/

        dc1 = new JDateChooser(new Date());
        dc1.setPreferredSize(new Dimension(200, 30));
        dc1.addPropertyChangeListener(this);
        dc2 = new JDateChooser(new Date());
        dc2.setPreferredSize(new Dimension(200, 30));
        dc2.addPropertyChangeListener(this);
        /*-------------*/
        addBtn.setMaximumSize(btnSize);
        addBtn.addActionListener(this);
        refreshBtn.setMaximumSize(btnSize);
        refreshBtn.addActionListener(this);
        JToolBar toolBar = new JToolBar(SwingConstants.HORIZONTAL);
        toolBar.setFloatable(false);
        //toolBar.addSeparator();
        toolBar.add(dc1);
        toolBar.add(dc2);

        toolBar.add(refreshBtn);
        toolBar.add(addBtn);
        toolBar.add(Box.createHorizontalGlue());

        return toolBar;
    }

    public void refreshMaster() {
        try {
            getDtm().setData(
                    armType == 1 ?
                            getService().getScaleIn(dc1.getDate(), dc2.getDate(), AppFrame.loggedUser) :
                            getService().getScaleOut(dc1.getDate(), dc2.getDate(), AppFrame.loggedUser)
            );
            refreshDetail(0);
        } catch (SQLException e) {
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
            new EditScaleOut(armType == 1 ? "Приход" : "Расход", null);
        } else if (e.getSource().equals(refreshBtn)) {
            refreshMaster();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(dc1) || evt.getSource().equals(dc2)) {
            refreshMaster();
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        int n = tbl.getSelectedRow();
        if (n != -1)
            refreshDetail(n);
    }

    public DataSetTableModel getDtm() {
        return dtm;
    }

    public void setDtm(DataSetTableModel dtm) {
        this.dtm = dtm;
    }

    public LibraService getService() {
        return service;
    }

    public void setService(LibraService service) {
        this.service = service;
    }
}
