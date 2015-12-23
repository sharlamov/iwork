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
        return dataSet.names.length;
    }

    public String getColumnName(int column) {
        return dataSet.names[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return dataSet.types[columnIndex];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return dataSet.list.get(rowIndex)[columnIndex];
    }
}
