package com.docs;

import com.bin.DialogMaster;
import com.bin.LibraPanel;
import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import com.model.Act;
import com.model.Doc;
import com.model.Scale;
import com.report.model.Report;
import com.service.LibraService;
import com.util.FocusPolicy;
import com.util.Fonts;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.db.editors.*;
import com.view.component.panel.DbPanel;
import com.view.component.widget.ScaleWidget;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

abstract class ScaleDoc extends JDialog implements ActionListener, ChangeEditListener {

    final Doc doc;
    private Integer scaleId;
    private LibraPanel libraPanel;
    DataSet newDataSet;
    DataSet oldDataSet;
    DataSet newInfoSet;
    DataSet oldInfoSet;
    private JButton bAction = new JButton(Libra.lng("action"), Pictures.actionIcon);
    private JButton bPrint = new JButton(Libra.lng("print"), Pictures.printerIcon);
    private JButton bSave = new JButton(Libra.lng("save"));
    private JButton bCancel = new JButton(Libra.lng("cancel"));
    DbPanel fieldsPanel;
    DbPanel infoPanel;
    FocusPolicy policy;
    DataSet historySet = new DataSet("tip", "id", "nr", "dt", "br", "userid", "sc", "masa", "scaleId", "photo1", "photo2");
    SearchDbEdit sc;
    boolean useRefresh = false;
    NumberDbEdit sezon_yyyy;
    private NumberDbEdit net;
    private NumberDbEdit brutto;
    private NumberDbEdit tara;
    private DateDbEdit time_in;
    private DateDbEdit time_out;
    private JPanel board = new JPanel();
    private ComboDbEdit<CustomItem> clcelevatort;
    private ComboDbEdit<CustomItem> clcdivt;
    private JTabbedPane tabbedPane;

    ScaleDoc(LibraPanel libraPanel, final DataSet dataSet, Doc doc, Dimension size) {
        super((JFrame) null, Libra.lng(doc.getName()), true);
        this.libraPanel = libraPanel;
        this.newDataSet = dataSet.copy();
        this.newInfoSet = new DataSet();
        this.doc = doc;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
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

    public abstract void initTab(boolean isOpened);

    public abstract DbPanel createInfoPanel();

    void updateDataSet() {
        initDefSeason();
        Date d1 = newDataSet.getDate("time_in");
        Date d2 = newDataSet.getDate("time_out");
        d2 = d2.before(d1) ? d1 : d2;

        newDataSet.setObject("time_in", new Timestamp(d1.getTime()));
        newDataSet.setObject("time_out", new Timestamp(d2.getTime()));

        newDataSet.setObject("clctype_vehiclet", new CustomItem(doc.getType(), ""));
        newDataSet.setObject("in_out", doc.getId());

        newDataSet.setObject("userid", LibraService.user.getId());

        if (scaleId != null && isNetNull())
            newDataSet.setObject("scaleid", scaleId);
    }

    private void initWeightBoard() {
        if (Libra.scales == null)
            return;

        board.setPreferredSize(new Dimension(220, 70));
        for (Scale scale : Libra.scales) {
            final ScaleWidget wb = new ScaleWidget(scale.getDriver(), false, scale.getScaleId(), scale.getCams());
            wb.setWeight(scale.getDriver().getWeight());
            if (!isNetNull()) {
                wb.setBlock(true);
            }
            wb.btnAdd.addActionListener(e -> {
                if (doc.getId() == 1)
                    fixWeight(wb, brutto, tara);
                else
                    fixWeight(wb, tara, brutto);
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
            Libra.eMsg(e);
        }

        if (isNetNull())
            setFocusTraversalPolicy(policy);
        else {
            fieldsPanel.blockPanel();
        }

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab(Libra.lng("enterData"), fieldsPanel);
        if (doc.isUsePrintInfo()) {
            infoPanel = createInfoPanel();
            initTab(false);
            tabbedPane.addTab(Libra.lng("printData"), infoPanel);
            ChangeListener changeListener = changeEvent -> {
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
            };
            tabbedPane.addChangeListener(changeListener);
        }
        oldDataSet = newDataSet.copy();
        oldInfoSet = newInfoSet.copy();
        add(tabbedPane, BorderLayout.CENTER);
    }

    protected abstract void initMain();

    private void initDefSeason() {
        BigDecimal val = newDataSet.getDecimal("season");
        if (val.equals(BigDecimal.ZERO)) {
            try {
                DataSet set = Libra.libraService.executeQuery(Libra.sql("DEFAULTSEASON"), DataSet.init("clcdivt", clcdivt.getValue(), "cDate", Libra.truncDate(null)));
                val = set.getDecimal("sezon_yyyy");
            } catch (Exception e) {
                Libra.eMsg(e, true);
            }
            newDataSet.setObject("season", val);
        }
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

    private void exitDialog() {
        if (!isModified()
                || 0 == JOptionPane.showConfirmDialog(null, Libra.lng("cancelConfirmDialog1"), Libra.lng("cancelConfirmDialog0"), JOptionPane.YES_NO_OPTION))
            dispose();
        libraPanel.refreshMaster();
    }

    boolean isModified() {
        return !newDataSet.equals(oldDataSet) || !newInfoSet.equals(oldInfoSet);
    }

    private void saveDocument() {
        if (save()) {
            libraPanel.refreshMaster();
            libraPanel.setRowPosition(newDataSet.getDecimal("id"));
            dispose();
        } else {
            tabbedPane.setSelectedIndex(0);
        }
    }

    private void makePrint() {
        if (save()) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            ButtonGroup bg = new ButtonGroup();
            for (Report report : doc.getReports()) {
                JRadioButton r0 = new JRadioButton(Libra.lng(report.getName()));
                bg.add(r0);
                p.add(r0);
            }

            if (JOptionPane.showOptionDialog(null, p, Libra.lng("rep.choose"), JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null) == 0) {

                for (int i = 0; i < p.getComponentCount(); i++) {
                    JRadioButton rb = (JRadioButton) p.getComponent(i);
                    if (rb.isSelected()) {
                        try {
                            Libra.reportService.buildReport(doc.getReports().get(i), newDataSet);
                        } catch (Exception e) {
                            Libra.eMsg(e);
                        }
                    }
                }
            }
        } else {
            tabbedPane.setSelectedIndex(0);
        }
    }

    private void makeAction() {
        if (save()) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            ButtonGroup bg = new ButtonGroup();
            for (Act act : doc.getActions()) {
                JRadioButton r0 = new JRadioButton(Libra.lng(act.getName()));
                bg.add(r0);
                p.add(r0);
            }

            if (JOptionPane.showOptionDialog(null, p, Libra.lng("choose"), JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null) == 0) {

                for (int i = 0; i < p.getComponentCount(); i++) {
                    JRadioButton rb = (JRadioButton) p.getComponent(i);
                    if (rb.isSelected()) {
                        try {
                            Libra.libraService.execute(doc.getActions().get(i).getSql(), newDataSet);
                            Libra.libraService.commit();
                            JOptionPane.showMessageDialog(null, Libra.lng("doc.saved"), "Ok", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception e) {
                            Libra.eMsg(e);
                        }
                    }
                }
            }
        } else {
            tabbedPane.setSelectedIndex(0);
        }
    }

    private void fixWeight(ScaleWidget scaleWidget, final IEdit firstField, final IEdit secondField) {
        final Integer weight = scaleWidget.getWeight();
        scaleId = scaleWidget.getDriverId();

        if (Libra.SETTINGS.isDebug())
            Libra.log("--fixWeight: " + weight + " --scaleid: " + scaleId);

        boolean bConfirm;

        if (weight != null && weight != 0) {
            List<URL> cams = scaleWidget.getCams();
            List<BufferedImage> images = new ArrayList<>(cams.size());
            for (URL url : cams) {
                try {
                    images.add(ImageIO.read(url));
                } catch (IOException e) {
                    Libra.eMsg(e, true);
                }
            }

            bConfirm = images.isEmpty() ?
                    JOptionPane.showConfirmDialog(this, Libra.lng("scale.fixedweight") + " (" + weight + ")", Libra.lng("scale.take"), JOptionPane.YES_NO_OPTION) == 0 :
                    DialogMaster.createFixDialog(Libra.lng("scale.take"), weight, images);


            if (bConfirm) {
                boolean isEmptyCar;
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

                Object mat[] = new Object[2];
                switch (images.size()) {
                    case 1:
                        mat[0] = createBlob(images.get(0));
                        break;
                    case 2: {
                        mat[0] = createBlob(images.get(0));
                        mat[1] = createBlob(images.get(1));
                    }
                    break;
                    default:
                }

                historySet.addRow(doc.getId(), null, null, new Timestamp(cTime.getTime()), isEmptyCar ? 0 : 1, LibraService.user.getId(), sc.getValue(), weight, scaleId, mat[0], mat[1]);
                blockWeightBoards();
            }
        } else {
            Libra.iMsg(Libra.lng("error.zeroweight"));
        }
    }

    private InputStream createBlob(BufferedImage img) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "jpg", baos);
        } catch (IOException e) {
            Libra.eMsg(e, true);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private void blockWeightBoards() {
        for (int i = 0; i < board.getComponentCount(); i++) {
            Component comp = board.getComponent(i);
            if (comp instanceof ScaleWidget) {
                ((ScaleWidget) comp).setBlock(true);
            }
        }
    }

    void createHeadPanel() {
        JPanel headPanel = fieldsPanel.createPanel(1, null);

        clcelevatort = new ComboDbEdit<>("clcelevatort", Libra.filials.keySet(), newDataSet);
        fieldsPanel.addToPanel(8, 8, 200, headPanel, clcelevatort);

        clcdivt = new ComboDbEdit<>("clcdivt", new ArrayList<>(), newDataSet);
        fieldsPanel.addToPanel(370, 8, 200, headPanel, clcdivt);

        Object idVal = newDataSet.getObject("id");
        if (idVal == null) {
            clcelevatort.addChangeEditListener(this);
            Libra.initFilial(clcelevatort, clcdivt, false);

            if (clcelevatort.getItemCount() > 1)
                policy.add(clcelevatort);
            else {
                clcelevatort.setChangeable(false);
            }

            if (clcdivt.getItemCount() > 1) {
                clcdivt.addChangeEditListener(this);
                policy.add(clcdivt);
            }
        } else {
            clcelevatort.setChangeable(false);
            clcdivt.setChangeable(false);
        }

        initDefSeason();
    }

    void createCalculationPanel() {
        int editHeight = 23;
        int stepDown = 27;

        JPanel sumaPanel = fieldsPanel.createPanel(3, null);
        JLabel bruttoLabel = new JLabel(Libra.lng("masa_brutto"), SwingConstants.CENTER);

        bruttoLabel.setBounds(120, 4, 120, editHeight);
        sumaPanel.add(bruttoLabel);
        JLabel taraLabel = new JLabel(Libra.lng("masa_tara"), SwingConstants.CENTER);
        taraLabel.setBounds(260, 4, 120, editHeight);
        sumaPanel.add(taraLabel);
        JLabel nettoLabel = new JLabel(Libra.lng("masa_netto"), SwingConstants.CENTER);
        nettoLabel.setBounds(400, 4, 120, editHeight);
        sumaPanel.add(nettoLabel);

        JLabel weightLabel = new JLabel(Libra.lng("weight"));
        weightLabel.setBounds(8, 8 + stepDown, 120, editHeight);
        sumaPanel.add(weightLabel);
        JLabel timeLabel = new JLabel(Libra.lng("time"));
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

    private void checkWeightField(NumberDbEdit edit) {
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
        } else if (event.equals(brutto) || event.equals(tara)) {
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
            initDefSeason();
            sezon_yyyy.refresh();
            useRefresh = true;
        } else if (source.equals(clcdivt)) {
            initDefSeason();
            sezon_yyyy.refresh();
            useRefresh = true;
        }
    }

    private boolean isNetNull() {
        return newDataSet.getDecimal("masa_netto").equals(BigDecimal.ZERO);
    }
}