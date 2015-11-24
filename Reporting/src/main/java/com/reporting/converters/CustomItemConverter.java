package com.reporting.converters;

import com.reporting.model.CustomItem;
import org.primefaces.util.ComponentUtils;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@FacesConverter("customItemConverter")
public class CustomItemConverter implements Converter {

    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        try {
            List<CustomItem> list  = getItemsList(uic);
            int n = list.indexOf(new CustomItem(new BigDecimal(value), null));
            return n != -1 ? list.get(n) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        return object != null ? ((CustomItem)object).getId().toString() : null;
    }

    public static List<CustomItem> getItemsList(UIComponent component){
        List<UIComponent> children = component.getChildren();
        for(UIComponent child: children){
            if(child instanceof UISelectItems){
                Object selectItems = ((UISelectItems) child).getValue();
                if(selectItems instanceof List) {
                    return (List<CustomItem>) selectItems;
                }
            }
        }
        return new ArrayList<>();
    }
}