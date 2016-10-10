package com.dao.model;

import java.math.BigDecimal;
import java.util.List;

public interface IDataSet extends List<Object[]> {

    Object getObject(int row, int col);

    Object getObject(String name);

    int size();

    int getColCount();

    int findField(String name);

    Object[] get(int i);

    BigDecimal sum(String substring);

    List<String> getNames();

    List<IDataSet> groupBy(String fName);
}
