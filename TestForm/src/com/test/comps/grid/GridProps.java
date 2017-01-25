package com.test.comps.grid;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class GridProps {

    DecimalFormat stdNumberFormat;
    SimpleDateFormat stdDateFormat;
    private List<Field> columns;
    boolean useBgColor;
    boolean useSummary;
    boolean useSorting;
    String query;

    public GridProps() {
        columns = new ArrayList<>();
        columns.add(new Field("clccodt", null, 100));
        query = "";
        stdNumberFormat = new DecimalFormat("#,###.##");
        stdDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    }

    String toStdFormat(Object obj) {
        String res = "";
        if (obj instanceof Number)
            res = stdNumberFormat.format(obj);
        else if (obj instanceof Date)
            res = stdDateFormat.format(obj);
        else if (obj != null)
            res = obj.toString();

        return res;
    }

    void add(Field field) {
        columns.add(field);
    }

    void clear() {
        columns.clear();
    }

    Field create(String name, Font font, int size) {
        return new Field(name, font, size);
    }

    Field get(int col) {
        return columns.get(col);
    }

    String getColName(int col) {
        return get(col).name;
    }

    Font getColFont(int col) {
        return get(col).font;
    }

    int getColSize(int col) {
        return get(col).size;
    }

    int size() {
        return columns.size();
    }

    @Override
    public String toString() {
        return columns.toString();
    }

    public class Field {
        Field(String name, Font font, int size) {
            this.name = name;
            this.font = font;
            this.size = size;
        }

        String name;
        Font font;
        int size;

        @Override
        public String toString() {
            return name;
        }
    }
}
