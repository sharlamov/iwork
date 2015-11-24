package net.scales.converters;

import net.scales.model.CustomItem;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.math.BigDecimal;

@FacesConverter("customItemConverter")
public class CustomItemConverter implements Converter {

	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		try{
			String[] data = value.split("#");
			switch (data.length) {
			case 1:
				return new CustomItem(null, null, data[0]);
			case 2:
				return new CustomItem(new BigDecimal(data[0]), null, data[1]); 
			default:
				return null;
			}
		} catch(Exception e) {
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