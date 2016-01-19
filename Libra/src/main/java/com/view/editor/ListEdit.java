package com.view.editor;

import com.enums.SearchType;
import com.model.DataSet;
import com.service.LibraService;
import com.view.component.autocomplete.AutoSuggestBox;

public class ListEdit extends AbstractEdit {

    public ListEdit(String title, DataSet dataSet, LibraService service, SearchType type) {
        super(title, new AutoSuggestBox(service, type), dataSet);
    }

    @Override
    public Object getValue() {
        return ((AutoSuggestBox) getField()).getSelectedItem();
    }

    @Override
    public void setValue(Object obj) {
        ((AutoSuggestBox) getField()).setSelectedItem(obj);
    }
}
