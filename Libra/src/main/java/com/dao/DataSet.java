package com.dao;

import java.util.ArrayList;
import java.util.List;

public class DataSet extends ArrayList<Object[]>{

    private List<String> names;

    public DataSet(List<String> names, List<Object[]> list) {
        this.names = names;
        this.addAll(list);
    }

    public String getColumnName(int col){
        return names.isEmpty() ? null : names.get(col);
    }

    public int getColumnCount(){
        return names.size();
    }

    public Object getValue(int row, int col){
        return isEmpty() ? null : get(row)[col];
    }

    public Object getValueByName(String fieldName, int row){
        int col = names.indexOf(fieldName);
        //System.out.println(fieldName + " : " + row + " : " + col);
        return col == -1 ? null : getValue(row, col);
    }

    public void setValueByName(String fieldName, int row, Object value){
        int col = names.indexOf(fieldName);
        if(col != -1){
            get(row)[col] = value;
        }
    }

    public List<String> getNames() {
        return names;
    }
}
