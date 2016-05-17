package com.docs;

import com.bin.LibraPanel;
import com.driver.ScalesDriver;
import com.model.*;
import com.service.LangService;
import com.service.LibraService;
import com.util.FocusPolicy;
import com.util.Fonts;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.db.editors.*;
import com.view.component.panel.DbPanel;
import com.view.component.weightboard.WeightBoard;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public abstract class ScaleDoc extends JDialog implements ActionListener, ChangeEditListener {

    protected final Doc doc;
    protected Integer scaleId;
    protected LibraPanel libraPanel;
    protected DataSet newDataSet;
    protected DataSet oldDataSet;
    protected DataSet newInfoSet;
    protected DataSet oldInfoSet;
    protected JButton bAction = new JButton(LangService.trans("action"), Pictures.actionIcon);
    protected JButton bPrint = new JButton(LangService.trans("print"), Pictures.printerIcon);
    protected JButton bSave = new JButton(LangService.trans("save"));
    protected JButton bCancel = new JButton(LangService.trans("cancel"));
    protected DbPanel fieldsPanel;
    protected DbPanel infoPanel;
    protected FocusPolicy policy;
    protected DataSet historySet = new DataSet("tip,id,nr,dt,br,userid,sc,masa,scaleId");
    protected SearchDbEdit sc;
    private NumberDbEdit net;
    private NumberDbEdit brutto;
    private NumberDbEdit tara;
    private DateDbEdit time_in;
    private DateDbEdit time_out;
    private JPanel board = new JPanel();
    private ComboDbEdit<CustomItem> clcelevatort;
    private ComboDbEdit<CustomItem> clcdivt;

    public ScaleDoc(LibraPanel libraPanel, final DataSet dataSet, Doc doc, Dimension size) {
        super((JFrame) null, LangService.trans(doc.getName()), true);
        this.libraPanel = libraPanel;
        this.newDataSet = dataSet.copy();
        this.newInfoSet = new DataSet();
        this.doc = doc;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exitDialog();
            }
        });
        setSize(size);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initFieldsPanel();
        initWeightBoard();
        initStatusPanel();

        setVisible(true);
    }

    public abstract boolean save();

    public abstract void inForm() throws Exception;

    public abstract void outForm() throws Exception;

    public abstract void initMain();

    public abstract void initTab(boolean isOpened);

    public abstract DbPanel createInfoPanel();

    public void updateDataSet() {
        Date d1 = newDataSet.getDateValue("time_in", 0);
        Date d2 = newDataSet.getDateValue("time_out", 0);
        d2 = d2.before(d1) ? d1 : d2;

        newDataSet.setValueByName("time_in", 0, new Timestamp(d1.getTime()));
        newDataSet.setValueByName("time_out", 0, new Timestamp(d2.getTime()));

        newDataSet.setValueByName("clctype_vehiclet", 0, new CustomItem(doc.getType(), ""));
        newDataSet.setValueByName("in_out", 0, doc.getId());

        newDataSet.setValueByName("userid", 0, LibraService.user.getId());

        if(scaleId != null && newDataSet.getValueByName("masa_netto", 0) != null)
            newDataSet.setValueByName("scaleid", 0, scaleId);
    }

    private void initWeightBoard() {
        board.setPreferredSize(new Dimension(220, 70));
        for (Object[] data : Libra.scaleDrivers) {
            ScalesDriver sd = (ScalesDriver) data[0];
            final WeightBoard wb = new WeightBoard(sd, false, data[1]);
            wb.setWeight(sd.getWeight());
            if (!net.isEmpty()) {
                wb.setBlock(true);
            }
            wb.btnAdd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (doc.getId() == 1)
                        fixWeight(wb, brutto, tara);
                    else
                        fixWeight(wb, tara, brutto);
                }
            });
            board.add(wb);
        }
        add(board, BorderLayout.EAST);
    }

    private void initFieldsPanel() {
        fieldsPanel = new DbPanel(720, 550);
        policy = new FocusPolicy();

        initMain();
        try {
            if (doc.getId() == 1)
                inForm();
            else
                outForm();
        } catch (Exception e) {
            Libra.eMsg(e.getMessage());
        }

        if (net.isEmpty())
            setFocusTraversalPolicy(policy);
        else {
            fieldsPanel.blockPanel();
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(LangService.trans("enterData"), fieldsPanel);
        if (doc.isUsePrintInfo()) {
            infoPanel = createInfoPanel();
            initTab(false);
            tabbedPane.addTab(LangService.trans("printData"), infoPanel);
            ChangeListener changeListener = new ChangeListener() {
                public void stateChanged(ChangeEvent changeEvent) {
                    JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                    int index = sourceTabbedPane.getSelectedIndex();
                    if (index == 0) {
                        setFocusTraversalPolicy(policy);
                    } else {
                        setFocusTraversalPolicy(null);
                        infoPanel.requestFocus();
                        infoPanel.setFocusCycleRoot(true);
                        initTab(true);
                    }
                }
            };
            tabbedPane.addChangeListener(changeListener);
        }
        oldDataSet = newDataSet.copy();
        oldInfoSet = newInfoSet.copy();
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void initStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        if (!doc.getActions().isEmpty()) {
            bAction.setMargin(new Insets(0, 0, 0, 0));
            bAction.addActionListener(this);
            bAction.setPreferredSize(Libra.buttonSize);
            statusPanel.add(bAction);
        }

        if (!doc.getReports().isEmpty()) {
            bPrint.setMargin(new Insets(0, 0, 0, 0));
            bPrint.addActionListener(this);
            bPrint.setPreferredSize(Libra.buttonSize);
            statusPanel.add(bPrint);
        }

        bSave.addActionListener(this);
        bSave.setPreferredSize(Libra.buttonSize);
        statusPanel.add(bSave);

        bCancel.addActionListener(this);
        bCancel.setPreferredSize(Libra.buttonSize);
        statusPanel.add(bCancel);

        add(statusPanel, BorderLayout.SOUTH);
    }

    public void exitDialog() {
        if (!isModified()
                || 0 == JOptionPane.showConfirmDialog(null, LangService.trans("cancelConfirmDialog1"), LangService.trans("cancelConfirmDialog0"), JOptionPane.YES_NO_OPTION))
            dispose();
        libraPanel.refreshMaster();
    }

    public boolean isModified() {
        return !newDataSet.isEqual(oldDataSet) || !newInfoSet.isEqual(oldInfoSet);
    }

    public void saveDocument() {
        if (save()) {
            libraPanel.refreshMaster();
            libraPanel.setRowPosition(newDataSet.getNumberValue("id", 0));
            dispose();
        }
    }

    public void makePrint() {
        if (save()) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            ButtonGroup bg = new ButtonGroup();
            for (Report report : doc.getReports()) {
                JRadioButton r0 = new JRadioButton(LangService.trans(report.getName()));
                bg.add(r0);
                p.add(r0);
            }

            if (JOptionPane.showOptionDialog(null, p, LangService.trans("rep.choose"), JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null) == 0) {

                for (int i = 0; i < p.getComponentCount(); i++) {
                    JRadioButton rb = (JRadioButton) p.getComponent(i);
                    if (rb.isSelected()) {
                        try {
                            Libra.reportService.buildReport(doc.getReports().get(i), newDataSet);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Libra.eMsg(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public void makeAction() {
        if (save()) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            ButtonGroup bg = new ButtonGroup();
            for (Act act : doc.getActions()) {
                JRadioButton r0 = new JRadioButton(LangService.trans(act.getName()));
                bg.add(r0);
                p.add(r0);
            }

            if (JOptionPane.showOptionDialog(null, p, LangService.trans("choose"), JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null) == 0) {

                for (int i = 0; i < p.getComponentCount(); i++) {
                    JRadioButton rb = (JRadioButton) p.getComponent(i);
                    if (rb.isSelected()) {
                        try {
                            Libra.libraService.execute(doc.getActions().get(i).getSql(), newDataSet);
                            Libra.libraService.commit();
                            JOptionPane.showMessageDialog(null, LangService.trans("doc.saved"), "Ok", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Libra.eMsg(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void fixWeight(WeightBoard weightBoard, IEdit firstField, IEdit secondField) {
        Integer weight = weightBoard.getWeight();
        scaleId = weightBoard.getDriverId();
        boolean isEmptyCar;

        if (weight != null && weight != 0) {
            int n = JOptionPane.showConfirmDialog(this, LangService.trans("scale.fixedweight") + " (" + weight + ")", LangService.trans("scale.take"), JOptionPane.YES_NO_OPTION);
            if (n == 0) {
                Date cTime = new Date();
                if (firstField.isEmpty()) {
                    firstField.setValue(weight);
                    time_in.setValue(cTime);
                    isEmptyCar = firstField.equals(tara);
                } else {
                    secondField.setValue(weight);
                    time_out.setValue(cTime);
                    isEmptyCar = secondField.equals(tara);
                }

                firstField.setChangeable(false);
                secondField.setChangeable(false);

                historySet.add(new Object[]{doc.getId(), null, null, new Timestamp(cTime.getTime()), isEmptyCar ? 0 : 1, LibraService.user.getId(), sc.getValue(), weight, scaleId});
                blockWeightBoards();
            }
        } else {
            Libra.eMsg(LangService.trans("error.zeroweight"));
        }
    }

    private void blockWeightBoards() {
        for (int i = 0; i < board.getComponentCount(); i++) {
            Component comp = board.getComponent(i);
            if (comp instanceof WeightBoard) {
                ((WeightBoard) comp).setBlock(true);
            }
        }
    }

    public void createHeadPanel() throws Exception {
        JPanel headPanel = fieldsPanel.createPanel(1, null);

        clcelevatort = new ComboDbEdit<CustomItem>("clcelevatort", Libra.filials.keySet(), newDataSet);
        fieldsPanel.addToPanel(8, 8, 200, headPanel, clcelevatort);

        clcdivt = new ComboDbEdit<CustomItem>("clcdivt", new ArrayList<CustomItem>(), newDataSet);
        fieldsPanel.addToPanel(370, 8, 200, headPanel, clcdivt);

        Object idVal = newDataSet.getValueByName("id", 0);
        if (idVal == null) {
            clcelevatort.addChangeEditListener(this);
            Libra.initFilial(clcelevatort, clcdivt, false);

            if (clcelevatort.getItemCount() > 1)
                policy.add(clcelevatort);
            else {
                clcelevatort.setChangeable(false);
            }

            if (clcdivt.getItemCount() > 1)
                policy.add(clcdivt);

        } else {
            clcelevatort.setChangeable(false);
            clcdivt.setChangeable(false);
        }
    }

    public void createCalculationPanel() {
        int editHeight = 23;
        int stepDown = 27;

        JPanel sumaPanel = fieldsPanel.createPanel(3, null);
        JLabel bruttoLabel = new JLabel(LangService.trans("masa_brutto"), SwingConstants.CENTER);

        bruttoLabel.setBounds(120, 4, 120, editHeight);
        sumaPanel.add(bruttoLabel);
        JLabel taraLabel = new JLabel(LangService.trans("masa_tara"), SwingConstants.CENTER);
        taraLabel.setBounds(260, 4, 120, editHeight);
        sumaPanel.add(taraLabel);
        JLabel nettoLabel = new JLabel(LangService.trans("masa_netto"), SwingConstants.CENTER);
        nettoLabel.setBounds(400, 4, 120, editHeight);
        sumaPanel.add(nettoLabel);

        JLabel weightLabel = new JLabel(LangService.trans("weight"));
        weightLabel.setBounds(8, 8 + stepDown, 120, editHeight);
        sumaPanel.add(weightLabel);
        JLabel timeLabel = new JLabel(LangService.trans("time"));
        timeLabel.setBounds(8, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(timeLabel);

        brutto = new NumberDbEdit("masa_brutto", newDataSet);
        brutto.setBounds(120, 4 + stepDown, 120, editHeight);
        brutto.setFont(Fonts.bold18);
        brutto.addChangeEditListener(this);
        sumaPanel.add(brutto);
        checkWeightField(brutto);

        tara = new NumberDbEdit("masa_tara", newDataSet);
        tara.setBounds(260, 4 + stepDown, 120, editHeight);
        tara.setFont(Fonts.bold18);
        tara.addChangeEditListener(this);
        sumaPanel.add(tara);
        checkWeightField(tara);

        net = new NumberDbEdit("masa_netto", newDataSet);
        net.addChangeEditListener(this);
        net.setChangeable(false);
        net.setBounds(400, 4 + stepDown, 120, editHeight);
        net.setFont(Fonts.bold18);
        sumaPanel.add(net);

        time_in = new DateDbEdit("time_in", Libra.dateTimeFormat, newDataSet);
        time_in.setChangeable(false);
        time_in.setBounds(doc.getId() == 1 ? 120 : 260, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(time_in);

        time_out = new DateDbEdit("time_out", Libra.dateTimeFormat, newDataSet);
        time_out.setChangeable(false);
        time_out.setBounds(doc.getId() == 1 ? 260 : 120, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(time_out);
    }

    public void checkWeightField(NumberDbEdit edit) {
        if (edit.isEmpty() && LibraService.user.getScaleType() == 4) {
            edit.setChangeable(true);
            policy.add(edit);
        } else {
            edit.setChangeable(false);
            policy.remove(edit);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object event = e.getSource();
        if (event.equals(bSave)) {
            saveDocument();
        } else if (event.equals(bCancel)) {
            exitDialog();
        } else if (event.equals(bPrint)) {
            makePrint();
        } else if (event.equals(bAction)) {
            makeAction();
        } else if (event.equals(brutto) || e.getSource().equals(tara)) {
            changeEdit(null);
        }
    }

    public void changeEdit(Object source) {
        if (source.equals(brutto) || source.equals(tara)) {
            BigDecimal b = brutto.getNumberValue();
            BigDecimal t = tara.getNumberValue();
            if (b.intValue() > 0 && t.intValue() > 0) {
                net.setValue(b.subtract(t));
            }
        } else if (source.equals(net)) {
            checkWeightField(brutto);
            checkWeightField(tara);
        } else if (source.equals(clcelevatort)) {
            Libra.initFilial(clcelevatort, clcdivt, false);
        }
    }
}