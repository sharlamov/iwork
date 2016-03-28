package com.bin;

import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.model.Doc;
import com.service.LangService;
import com.service.LibraService;
import com.toedter.calendar.JDateChooser;
import com.util.Fonts;
import com.util.Libra;
import com.view.component.db.editors.ComboDbEdit;
import com.view.component.editors.ChangeEditListener;
import com.view.component.grid.DataGrid;
import com.view.component.grid.DataGridSetting;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

public class LibraPanel extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener, ItemListener, ChangeEditListener {

    private JDateChooser date1 = new JDateChooser("dd.MM.yyyy", "##.##.####", '_');
    private JDateChooser date2 = new JDateChooser("dd.MM.yyyy", "##.##.####", '_');
    private JButton addBtn = new JButton(Libra.createImageIcon("images/add.png"));
    private JButton refreshBtn = new JButton(Libra.createImageIcon("images/reload.png"));
    private JToggleButton halfBtn = new JToggleButton(Libra.createImageIcon("images/half.png", 100, 30));
    private ComboDbEdit elevators;
    private ComboDbEdit divs;
    private DataGrid dataGrid;
    private Dimension dateSize = new Dimension(100, 27);
    private HistoryPanel detail;
    private Doc doc;
    private LibraPanel pan;
    private JLabel lostCarLabel;
    private DataSet filter;

    public LibraPanel(final Doc doc, DataGridSetting lSetting) {
        this.doc = doc;
        this.detail = new HistoryPanel();
        this.pan = this;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        dataGrid = new DataGrid(lSetting, Libra.libraService);
        dataGrid.increaseRowHeight(1.5f);
        dataGrid.setGridFont(Fonts.plain15);
        dataGrid.addListSelectionListener(this);
        dataGrid.addActs(doc);
        dataGrid.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                int row = table.rowAtPoint(me.getPoint());
                if (me.getClickCount() == 2 && row != -1) {
                    new LibraEdit(pan, dataGrid.getDataSetByRow(row), doc);
                }
            }
        });
        dataGrid.setColumnFont("masa_brutto", Fonts.bold12);
        dataGrid.setColumnFont("masa_tara", Fonts.bold12);
        dataGrid.setColumnFont("masa_netto", Fonts.bold12);

        List<String> lst = Arrays.asList("d1", "d2", "elevator", "div", "empty");
        filter = new DataSet(lst, new Object[lst.size()]);

        tableKeyBindings(dataGrid);

        dataGrid.addToolBar(initToolBar());
        dataGrid.setHeaderFont(Fonts.bold12);
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
                    new LibraEdit(pan, dataGrid.getDataSetByRow(row), doc);
            }
        });

        table.getIMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "Insert");
        table.getAMap().put("Insert", new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                new LibraEdit(pan, dataGrid.getDataSetByRow(-1), doc);
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

        toolBar.addSeparator();
        lostCarLabel = new JLabel();
        lostCarLabel.setFont(Fonts.bold15);
        lostCarLabel.setOpaque(true);
        lostCarLabel.setForeground(Color.red);
        toolBar.add(lostCarLabel);

        toolBar.add(Box.createHorizontalGlue());
        toolBar.addSeparator();

        Date cDate = Libra.truncDate(null);
        date1.setDate(cDate);
        date1.setMaximumSize(dateSize);
        date1.setPreferredSize(dateSize);
        date1.getDateEditor().addPropertyChangeListener(this);
        date1.setMaxSelectableDate(date2.getDate());

        date2.setDate(cDate);
        date2.setMaximumSize(dateSize);
        date2.setPreferredSize(dateSize);
        date2.getDateEditor().addPropertyChangeListener(this);
        date2.setMinSelectableDate(date1.getDate());

        elevators = new ComboDbEdit("elevator", LibraService.user.getElevators(), filter);
        elevators.setMaximumSize(new Dimension(200, 27));
        toolBar.add(elevators);
        if (LibraService.user.getElevators().size() > 1) {
            elevators.insertItemAt(new CustomItem(null, LangService.trans("all")), 0);
            elevators.setSelectedIndex(0);
            elevators.addChangeEditListener(this);
        } else
            elevators.setVisible(false);

        toolBar.addSeparator();

        divs = new ComboDbEdit("div", new ArrayList<CustomItem>(), filter);
        divs.setMaximumSize(new Dimension(100, 27));
        divs.addChangeEditListener(this);
        toolBar.add(divs);
        initDiv();


        toolBar.addSeparator();
        toolBar.add(date1);
        toolBar.add(new JLabel(" - "));
        toolBar.add(date2);

        return toolBar;
    }

    public void setRowPosition(BigDecimal rowId) {
        if (rowId != null) {
            int row = dataGrid.defineLocation("id", rowId);
            if (row != -1) {
                dataGrid.setSelectedRow(row);
            }
        }
    }

    public void lostCarsInit(Map<String, Object> params) {
        DataSet lostDS = null;
        try {
            lostDS = Libra.libraService.selectDataSet(doc.getId() == 1 ? SearchType.LOSTCARIN : SearchType.LOSTCAROUT, params);
            if (lostDS != null && !lostDS.isEmpty()) {
                lostCarLabel.setText(LangService.trans("lostcar") + " " + lostDS.getStringValue("dd", 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public Map<String, Object> refreshMaster() {
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            params.put(":d1", date1.getDate());
            params.put(":d2", date2.getDate());

            CustomItem item = (CustomItem) elevators.getSelectedItem();
            params.put(":elevator", item.getId() == null ? LibraService.user.getElevators() : item);
            params.put(":div", divs.getSelectedItem());
            params.put(":empty", halfBtn.isSelected() ? null : 0);

            dataGrid.select(params);

            int selectedRow = dataGrid.getSelectedRow();
            if (selectedRow == -1)
                refreshDetail(0);
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }

        return params;
    }

    public void refreshDetail(int row) {
        Object obj = dataGrid.getValueByFieldName("ID", row);
        if (obj != null && obj instanceof BigDecimal)
            detail.refreshData((BigDecimal) obj);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(addBtn)) {
            new LibraEdit(pan, dataGrid.getDataSetByRow(-1), doc);
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

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(halfBtn)) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                lostCarsInit(refreshMaster());
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                lostCarLabel.setText("");
                refreshMaster();
            }
        }
    }

    private void initDiv() {
        try {
            DataSet divSet = Libra.libraService.selectDataSet(SearchType.GETDIVBYSILOS, Collections.singletonMap(":elevator_id", elevators.getValue()));
            divs.changeData(divSet);
            divs.setSelectedItem(LibraService.user.getDefDiv());
            divs.setVisible(divSet.size() > 1);
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public void changeEdit(Object source) {
        if (source.equals(elevators)) {
            initDiv();
            refreshMaster();
        } else if (source.equals(divs)) {
            refreshMaster();
        }
    }
}