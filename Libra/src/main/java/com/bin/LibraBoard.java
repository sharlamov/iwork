package com.bin;

import com.model.Doc;
import com.service.JsonService;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.grid.DataGridSetting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class LibraBoard extends JPanel {


    public LibraBoard() {
        super(new BorderLayout());
        add(new ScaleOnlinePanel(), BorderLayout.NORTH);

        List<Doc> docList = JsonService.fromJsonList(Libra.designs.get("DOC.LIST"), Doc.class);
        JTabbedPane tabbedPane = new JTabbedPane();
        for (Doc doc : docList) {
            DataGridSetting lSetting = JsonService.fromJson(Libra.designs.get(doc.getId() == 1 ? "DATAGRID.IN" : "DATAGRID.OUT"), DataGridSetting.class);
            tabbedPane.addTab(getHtmlTitle(doc.getName()), Pictures.middleIcon, new LibraPanel(doc, lSetting));
            tabbedPane.setMnemonicAt(0, KeyEvent.VK_F1);
        }
        add(tabbedPane, BorderLayout.CENTER);
    }

    public String getHtmlTitle(String title) {
        return "<html><body width='150'>" + Libra.lng(title) + "</body></html>";
    }
}
