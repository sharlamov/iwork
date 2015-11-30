package com.reporting.util;

import com.reporting.model.CustomItem;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class WebUtil {

    public static DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");

    public static <T> T parse(Object obj, Class<T> type) {
        if (obj == null)
            return null;
        else {
            if (obj instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal) obj;
                if (type == Long.class) {
                    return type.cast(bd.longValue());
                } else if (type == Integer.class) {
                    return type.cast(bd.intValue());
                } else if (type == Double.class) {
                    return type.cast(bd.doubleValue());
                } else {
                    return type.cast(bd.floatValue());
                }
            } else {
                return type.cast(obj);
            }
        }
    }

    public static List<CustomItem> toCustomItemList(List<Object> objects) {
        List<CustomItem> items = new ArrayList<>(objects.size() + 1);
        for (Object obj : objects) {
            Object[] array = (Object[]) obj;
            CustomItem ci = new CustomItem(array[0], null, array[1]);
            items.add(ci);
        }
        return items;
    }

    public static <T> T copy(T obj) {
        T clone = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream ous = new ObjectOutputStream(baos);
            ous.writeObject(obj);
            ous.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            clone = (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clone;
    }


    public static Color getRandomColor() {
        Random rand = new Random();

        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();

        return new Color(r, g, b);
    }

    public static String getColorString(List<Color> colors) {
        String str = "";
        for (Color clr : colors) {
            String hex = Integer.toHexString(clr.getRGB()).substring(2);
            str += str.isEmpty() ? hex : "," + hex;
        }
        return str;
    }

    public static Date strInDate(String str) {
        Date date = null;
        try {
            date = dateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static int currentSeason(Date date) {
        Calendar calendar = Calendar.getInstance();

        if (date != null)
            calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        try {
            Date splitDate = dateFormat.parse("01.07." + year);
            if (calendar.before(splitDate))
                year--;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return year;
    }

    public static String toText(String lang, String enText, String roText, String ruText){
        return lang.equals("en") ? enText : lang.equals("ro") ? roText : ruText;
    }

    public static Double round(Double d){
        return Math.round(d * 100) / 100d;
    }

}
