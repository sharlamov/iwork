package com.view.editor;

import com.enums.SearchType;
import com.model.DataSet;
import com.service.LibraService;
import com.view.component.autocomplete.AutoSuggestBox;

import java.awt.*;

public class ListEdit extends AbstractEdit {

    private final String title;
    private final LibraService service;
    private final SearchType type;
    private AutoSuggestBox field;
    private DataSet dataSet;

    public ListEdit(String title, DataSet dataSet, LibraService service, SearchType type) {
        super(title);
        this.title = title;
        this.dataSet = dataSet;
        this.service = service;
        this.type = type;
        field = new AutoSuggestBox(service, type);
        setValue();
        add(field, BorderLayout.CENTER);
    }

    @Override
    public void setValue() {
        if (dataSet != null && !dataSet.isEmpty()) {
            Object obj = dataSet.getValueByName(getTitle(), 0);
            field.setSelectedItem(obj);
        }
    }

    @Override
    public Object getValue() {
        return field.getSelectedItem();
    }
}
