package com.dao;

import java.sql.ResultSetMetaData;
import java.util.List;

/**
 * Created by sharlamov on 22.12.2015.
 */
public class DataSet {

    String[] names;
    Class[] types;
    List<Object[]> list;

    public DataSet(String[] names, Class[] types, List<Object[]> list) {
        this.names = names;
        this.types = types;
        this.list = list;
    }
}
