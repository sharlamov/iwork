package com.view.dbeditors;

import com.enums.SearchType;
import com.model.DataSet;
import com.service.LibraService;
import com.view.component.autocomplete.AutocompleteItem;

public class ListDbEdit extends DbEdit {

    private AutocompleteItem fld;

    public ListDbEdit(String fieldName, DataSet dataSet, LibraService service, SearchType type) {
        super(fieldName, new AutocompleteItem(service, type), dataSet);
        fld = (AutocompleteItem) getField();
        fld.getTextArea().addFocusListener(this);
    }

    @Override
    public Object getFieldValue() {
        return ((AutocompleteItem) getField()).getSelectedItem();
    }

    @Override
    public void setFieldValue(Object object) {
        ((AutocompleteItem) getField()).setSelectedItem(object);
    }

}
