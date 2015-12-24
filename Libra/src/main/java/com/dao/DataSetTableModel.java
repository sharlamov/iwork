package com.dao;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;

/**
 * Created by sharlamov on 22.12.2015.
 */
public class DataSetTableModel extends AbstractTableModel {

    DataSet dataSet;

    public DataSetTableModel(String query, JdbcDAO dao) {
        try {
            dataSet = dao.select(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int getRowCount() {
        return dataSet.list.size();
    }

    public int getColumnCount() {
        return dataSet.names.size();
    }

    public String getColumnName(int column) {
        return dataSet.names.get(column);
    }

    public Object getValueAt(int row, int column) {
        return dataSet.list.get(row)[column];
    }
}
