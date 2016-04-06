package com.bin;

import com.docs.DocRo;
import com.docs.LibraEdit;
import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.model.Doc;
import com.service.LangService;
import com.service.LibraService;
import com.util.Fonts;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.db.editors.ChangeEditListener;
import com.view.component.db.editors.ComboDbEdit;
import com.view.component.db.editors.DateDbEdit;
import com.view.component.grid.DataGrid;
import com.view.component.grid.DataGridSetting;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class LibraPanel extends JPanel implements ActionListener, ListSelectionListener, ItemListener, ChangeEditListener {

    private DateDbEdit date1;
    private DateDbEdit date2;
    private JButton addBtn = new JButton(Pictures.addIcon);
    private JButton refreshBtn = new JButton(Pictures.reloadIcon);
    private JToggleButton halfBtn = new JToggleButton(Pictures.halfIcon);
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
                int row = dataGrid.getSelectedRow();
                if (me.getClickCount() == 2 && row != -1) {
                    openDocument(dataGrid.getDataSetByRow(row));
                }
            }
        });
        dataGrid.setColumnFont("masa_brutto", Fonts.bold12);
        dataGrid.setColumnFont("masa_tara", Fonts.bold12);
        dataGrid.setColumnFont("masa_netto", Fonts.bold12);

        filter = new DataSet(Arrays.asList("d1", "d2", "elevator", "silos", "div", "empty"));

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
                    openDocument(dataGrid.getDataSetByRow(row));
            }
        });

        table.getIMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "Insert");
        table.getAMap().put("Insert", new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                openDocument(dataGrid.getDataSetByRow(-1));
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
        date1 = new DateDbEdit("d1", filter);
        date2 = new DateDbEdit("d2", filter);

        date1.setDate(cDate);
        date1.setMaximumSize(dateSize);
        date1.setPreferredSize(dateSize);
        date1.addChangeEditListener(this);
        date1.setMaxSelectableDate(date2.getDate());

        date2.setDate(cDate);
        date2.setMaximumSize(dateSize);
        date2.setPreferredSize(dateSize);
        date2.addChangeEditListener(this);
        date2.setMinSelectableDate(date1.getDate());

        elevators = new ComboDbEdit<CustomItem>("silos", LibraService.user.getElevators(), filter);
        elevators.setMaximumSize(new Dimension(200, 27));
        toolBar.add(elevators);
        if (LibraService.user.getElevators().size() > 1) {
            elevators.insertItemAt(new CustomItem(null, LangService.trans("all")), 0);
            elevators.setSelectedIndex(0);
            elevators.addChangeEditListener(this);
        } else
            elevators.setVisible(false);

        toolBar.addSeparator();

        divs = new ComboDbEdit<CustomItem>("div", new ArrayList<CustomItem>(), filter);
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

    public void lostCarsInit() {
        try {
            DataSet lostDS = Libra.libraService.executeQuery(doc.getId() == 1 ? SearchType.LOSTCARIN.getSql() : SearchType.LOSTCAROUT.getSql(), filter);
            if (lostDS != null && !lostDS.isEmpty()) {
                lostCarLabel.setText(LangService.trans("lostcar") + " " + lostDS.getStringValue("dd", 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public void refreshMaster() {
        try {
            CustomItem item = (CustomItem) filter.getValueByName("silos", 0);
            filter.setValueByName("elevator", 0, item.getId() == null ? LibraService.user.getElevators() : item);
            filter.setValueByName("empty", 0, halfBtn.isSelected() ? null : BigDecimal.ZERO);
            dataGrid.select(filter);

            //refreshDetail();
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public void refreshDetail() {
        if (dataGrid.getRowCount() > 0) {
            int row = dataGrid.getSelectedRow();
            Object obj = dataGrid.getValueByFieldName("ID", row == -1 ? 0 : row);
            if (obj != null && obj instanceof BigDecimal)
                detail.refreshData((BigDecimal) obj);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(addBtn)) {
            openDocument(dataGrid.getDataSetByRow(-1));
        } else if (e.getSource().equals(refreshBtn)) {
            refreshMaster();

        }
    }

    public void openDocument(DataSet dataSet) {
        if (LibraService.user.getProfile().equalsIgnoreCase("mdauto")) {
            new LibraEdit(pan, dataSet, doc);
        } else {
            new DocRo(pan, dataSet, doc);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            refreshDetail();
        }
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(halfBtn)) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                refreshMaster();
                lostCarsInit();
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
        } else if (source.equals(date1) || source.equals(date2)) {
            refreshMaster();
            date1.setMaxSelectableDate(date2.getDate());
            date2.setMinSelectableDate(date1.getDate());
        }
    }
}