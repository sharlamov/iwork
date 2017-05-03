package com.orders.converters;

import com.dao.model.CustomItem;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("customItemConverter")
public class CustomItemConverter implements Converter {



    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        //CustomItem item = (CustomItem) ((UIInput) uic).getValue();
        if (!value.isEmpty()) {
            int n = value.indexOf('#');
            if (n != -1) {
                String key = value.substring(0, n);
                String label = value.substring(n + 1);
                if (!key.isEmpty())
                    return new CustomItem(key, label);
            }
        }

        ((UIInput) uic).resetValue();
        return null;
    }

    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        String res = "";
        if (object instanceof CustomItem) {
            CustomItem item = (CustomItem) object;
            return (item.getId() == null ? res : item.getId()) + "#" + item.getLabel();
        }

        return res;
    }
}