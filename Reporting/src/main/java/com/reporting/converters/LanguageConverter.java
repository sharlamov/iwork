package com.reporting.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.Locale;

@FacesConverter("languageConverter")
public class LanguageConverter implements Converter {

    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        try {
            return new Locale(value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null) {
            return object.toString();
        } else {
            return null;
        }
    }
}