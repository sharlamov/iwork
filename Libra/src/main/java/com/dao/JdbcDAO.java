package com.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JdbcDAO {

    public Connection connection;

    public DataSet select(String query)
            throws SQLException {

        Statement stmt = null;
        ResultSetMetaData metadata;
        List<Object[]> list = new ArrayList<Object[]>();
        String[] names = new String[0];
        Class[] types = new Class[0];

        try {
            stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            metadata = rs.getMetaData();
            int numberOfColumns = metadata.getColumnCount();
            names = new String[numberOfColumns];
            types = new Class[numberOfColumns];

            for (int i = 0; i < numberOfColumns; i++) {
                names[i] = metadata.getColumnName(i + 1);
                try {
                    types[i] = Class.forName(metadata.getColumnClassName(i + 1));
                } catch (ClassNotFoundException e) {
                    types[i] = String.class;
                }
            }

            while (rs.next()) {
                Object row[] = new Object[numberOfColumns];
                for (int i = 0; i < row.length; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return new DataSet(names, types, list);
    }

    public Connection getConnection() throws SQLException {

        Properties connectionProps = new Properties();
        connectionProps.put("user", "TRANSOIL");
        connectionProps.put("password", "TRANSOIL");

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.221:1521:TRANSOIL", connectionProps);
            System.out.println("Connected to database");
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (!connection.isClosed()){
                System.out.println("Disconnected to database");
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
