package com.dao;

import java.util.List;

public class DataSet {

    List<String> names;
    List<Class> types;
    List<Object[]> list;

    public DataSet(List<String> names, List<Class> types, List<Object[]> list) {
        this.names = names;
        this.types = types;
        this.list = list;
    }
}
