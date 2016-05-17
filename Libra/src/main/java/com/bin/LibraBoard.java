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
