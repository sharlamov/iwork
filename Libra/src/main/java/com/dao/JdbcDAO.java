package com.dao;

import com.model.CustomItem;
import com.model.CustomUser;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class JdbcDAO {

    public Connection connection;

    public DataSet select(String query, Object[] params)
            throws SQLException {

        long t = System.currentTimeMillis();
        PreparedStatement stmt = null;
        ResultSetMetaData metadata;
        List<Object[]> list = new ArrayList<Object[]>();
        List<String> names = new ArrayList<String>();
        List<Class> types = new ArrayList<Class>();

        try {
            stmt = getConnection().prepareStatement(query);
            for (int i = 0; i < params.length; i++) {

                if(params[i] instanceof Date){
                    stmt.setDate(i + 1, new java.sql.Date(((Date) params[i]).getTime()));
                }else if(params[i] instanceof BigDecimal){
                    stmt.setBigDecimal(i + 1, (BigDecimal) params[i]);
                }else if(params[i] instanceof Integer){
                    stmt.setInt(i + 1, (Integer) params[i]);
                }else {
                    stmt.setString(i + 1, params[i].toString());
                }
            }

            ResultSet rs = stmt.executeQuery();
            metadata = rs.getMetaData();
            int numberOfColumns = metadata.getColumnCount();

            for (int i = 0; i < numberOfColumns; i++) {
                names.add(metadata.getColumnName(i + 1));
            }

            Map<String, int[]> map = new LinkedHashMap<String, int[]>();
            for (int i = 0; i < numberOfColumns; i++) {
                if(names.get(i).startsWith("CLC") && names.get(i).endsWith("T")){
                    int codIndex = names.indexOf(names.get(i).substring(3, names.get(i).length() - 1));
                    if(codIndex == -1){
                        map.put(names.get(i), new int[]{i + 1});
                    }else{
                        map.put(names.get(i), new int[]{codIndex + 1, i + 1});
                    }
                } else if(-1 == names.indexOf("CLC" + names.get(i) + "T")) {
                    map.put(names.get(i), new int[]{i + 1});
                }
            }

            names.clear();
            names.addAll(map.keySet());

            while (rs.next()) {
                Object row[] = new Object[map.size()];
                int i = 0;
                for (Map.Entry<String, int[]> entry : map.entrySet()) {
                    if(entry.getValue().length > 1){
                        row[i++] = new CustomItem(rs.getObject(entry.getValue()[0]), rs.getObject(entry.getValue()[1]));
                    }else{
                        row[i++] = rs.getObject(entry.getValue()[0]);
                    }
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
        System.out.println(System.currentTimeMillis() - t);
        return new DataSet(names, types, list);
    }

    public CustomUser loginUser(String name, String pass) throws SQLException {
        CallableStatement cstmt = getConnection().prepareCall("{? := un$userparams.RegisterUser(?, ?)}");
        cstmt.registerOutParameter(1, Types.INTEGER);
        cstmt.setString(2, name);
        cstmt.setString(3, pass);
        cstmt.execute();
        int id = cstmt.getInt(1);

        CustomUser customUser = new CustomUser();
        customUser.setId(new BigDecimal(id));
        customUser.setUsername(name);
        customUser.setPassword(pass);
        return customUser;
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
            if (connection != null && !connection.isClosed()) {
                System.out.println("Disconnected to database");
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
