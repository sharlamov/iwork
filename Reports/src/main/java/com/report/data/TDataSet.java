package com.report.data;

public interface TDataSet {

    String getName();

    boolean Locate(String fieldName, String keyValue, String options);

    int RecordCount();

    TField FieldByName(String name);

    boolean Eof();

    void Next();

    TRow Fields();
}
