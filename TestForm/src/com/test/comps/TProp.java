package com.test.comps;

import java.util.HashMap;

public class TProp extends HashMap<String, Object> {


    public <T> T fetch(Object key) {
        Object obj = get(key);
        return obj == null ? null : (T) obj;
    }

    /*
    //public String name;
    public String type;
    public Color background;
    public Color foreground;
    public Font font;
    //public String text;
    public Rectangle bounds;
    public GridField[] fields;
    public LibraService service;
    public String sql;
    public String[] targetFields;
    public String format;
    public boolean isAlphaNum;
    public boolean shouldHide;
    public boolean shouldClear;
    public boolean isChangeable;
    List<TProp> children;
    */
}
