package com.report.data;

import com.report.enums.TFieldType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class TDataList extends ArrayList<TRow> implements TDataSet{

    private String name;
    private Iterator<TRow> it;
    private TRow row;

    public TDataList(String name) {
        this.name = name;
        it = iterator();
    }

    public TDataList(String name, List<String> fieldNames, List<List<Object>> data) {
        this.name = name;

        //data process
        for(List<Object> lst: data){
            TRow line = new TRow();
            Iterator dataLine = lst.iterator();
            for(String fieldName:fieldNames){
                line.add(new TField(fieldName, dataLine.next(), this));
            }
        }

        it = iterator();
        Next();
    }

    public String getName() {
        return name;
    }

    public boolean Locate(String fieldName, String keyValue, String options) {
        Iterator<TRow> iter = iterator();
        while(iter.hasNext()){
            TRow line = iter.next();
            for(TField f: line){
                if(f.getFieldName().equalsIgnoreCase(fieldName)){
                    if(f.AsString().equalsIgnoreCase(keyValue)){
                        row = line;
                        it = iter;
                        return true;
                    }else
                        break;
                }
            }
        }
        return false;
    }

    public int RecordCount() {
        return size();
    }

    public boolean Eof() {
        return it.hasNext();
    }

    public void Next() {
        if(it.hasNext())
            row = it.next();
    }

    public TRow Fields() {
        return row;
    }

    public TField FieldByName(String name) {
        if(row != null){
            for(TField f: row){
                if(f.getFieldName().equalsIgnoreCase(name))
                    return f;
            }
        }
        return null;
    }

    public void AddField(String fieldName, TFieldType fType, int i, boolean b) {
        if( size() == 1 ){
            row.add(new TField(fieldName, "", this));
        }
    }
}
