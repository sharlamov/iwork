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

    private static BigDecimal defVal(BigDecimal v_result){
        String text = v_result.toString();
        int v_length = text.length();
        BigDecimal v_defval = BigDecimal.ZERO;
        for (int i = 0; i < v_length; i++) {
            v_defval = v_defval.add(new BigDecimal((int)(text.charAt(i))));
        }
        return v_defval;
    }

    public static String encode(String pass){
        BigDecimal TEN = new BigDecimal(10);
        BigDecimal v_result = new BigDecimal(0);
        for (int i = 0; i < 30; i++) {
            BigDecimal ascii = pass.length() > i ? new BigDecimal((int) pass.charAt(i)) : new BigDecimal((i+1)*7).add(defVal(v_result));
            v_result = v_result.multiply(TEN).add(ascii);
        }
        return v_result.toString();
    }
}
