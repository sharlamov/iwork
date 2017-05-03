package com.orders.utils;

import com.dao.model.CustomItem;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebUtil {

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
            CustomItem ci = new CustomItem(array[0], array[1]);
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

    private static BigDecimal defVal(BigDecimal v_result) {
        String text = v_result.toString();
        int v_length = text.length();
        BigDecimal v_defval = BigDecimal.ZERO;
        for (int i = 0; i < v_length; i++) {
            v_defval = v_defval.add(new BigDecimal((int) (text.charAt(i))));
        }
        return v_defval;
    }

    public static String encode(String pass) {
        BigDecimal TEN = BigDecimal.TEN;
        BigDecimal v_result = BigDecimal.ZERO;
        for (int i = 0; i < 30; i++) {
            BigDecimal ascii = pass.length() > i ? new BigDecimal((int) pass.charAt(i)) : new BigDecimal((i + 1) * 7).add(defVal(v_result));
            v_result = v_result.multiply(TEN).add(ascii);
        }
        return v_result.toString();
    }

   /* public static void log(String text) {
        try {
            FileUtils.write(new File("FlyLog.log"), new Date() + " - " + text + "\r\n", Charset.defaultCharset(), true);
        } catch (IOException e) {
            System.out.println(e);
        }
    }*/
}
