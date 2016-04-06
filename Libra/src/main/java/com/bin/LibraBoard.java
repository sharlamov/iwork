package com.bin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.model.Doc;
import com.service.LangService;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.grid.DataGridSetting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Type;
import java.util.List;

public class LibraBoard extends JPanel {


    public LibraBoard() {
        super(new BorderLayout());
        add(new ScaleOnlinePanel(), BorderLayout.NORTH);
/*
        List<Doc> docList = new ArrayList<Doc>(2);
        Doc incomeDoc = new Doc(1, "tabName0", new ArrayList<Act>(),
                Arrays.asList(
                        new Report("rep0", new File("templates/bon.xls"), SearchType.PRINTTTN.getSql()),
                        new Report("rep1", new File("templates/act1.xls"), SearchType.PRINTTTN.getSql()),
                        new Report("rep2", new File("templates/act2.xls"), SearchType.PRINTTTN.getSql())
                ));
        Doc consumeDoc = new Doc(2, "tabName1", Arrays.asList(new Act("out.act1", SearchType.ACTOUT0.getSql()), new Act("out.act2", SearchType.ACTOUT1.getSql())),
                Arrays.asList(
                        new Report("rep3", new File("templates/TTN_horiz_graf.xls"), SearchType.PRINTTTN.getSql()),
                        new Report("rep4", new File("templates/TTN_vertic_graf.xls"), SearchType.PRINTTTN.getSql()),
                        new Report("rep5", new File("templates/TTN_horiz_negraf.xls"), SearchType.PRINTTTN.getSql()),
                        new Report("rep6", new File("templates/TTN_horiz_negraf_sum.xls"), SearchType.PRINTTTN.getSql()),
                        new Report("rep7", new File("templates/NN_vertic_graf.xls"), SearchType.PRINTTTN.getSql()),
                        new Report("rep8", new File("templates/NN_horiz_graf.xls"), SearchType.PRINTTTN.getSql())
                ));

        incomeDoc.setUsePrintInfo(true);
        docList.add(incomeDoc);
        consumeDoc.setUsePrintInfo(true);
        docList.add(consumeDoc);
              gson.toJson(docList, System.out);

*/
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<Doc>>() {
        }.getType();
        List<Doc> docList = gson.fromJson(Libra.designs.get("DOC.LIST"), type);


        JTabbedPane tabbedPane = new JTabbedPane();
        for (Doc doc : docList) {
            DataGridSetting lSetting = gson.fromJson(Libra.designs.get(doc.getId() == 1 ? "DATAGRID.IN" : "DATAGRID.OUT"), DataGridSetting.class);
            tabbedPane.addTab(getHtmlTitle(doc.getName()), Pictures.middleIcon, new LibraPanel(doc, lSetting));
            tabbedPane.setMnemonicAt(0, KeyEvent.VK_F1);
        }
        add(tabbedPane, BorderLayout.CENTER);
    }

    public String getHtmlTitle(String title) {
        return "<html><body width='150'>" + LangService.trans(title) + "</body></html>";
    }
}
