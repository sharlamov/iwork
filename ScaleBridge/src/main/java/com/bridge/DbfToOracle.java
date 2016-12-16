package com.bridge;

import com.dao.model.DataSet;
import com.dao.service.JDBCFactory;
import org.jamel.dbf.DbfReader;
import org.jamel.dbf.structure.DbfField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.function.BiFunction;

public class DbfToOracle {

    private File dbf;
    private JDBCFactory dao;
    private final BiFunction<DataSet, Object[], Boolean> checkFunc;
    private final boolean isDebug;
    private String tableName;
    private long last_order;

    public DbfToOracle(File dbf, JDBCFactory dao, BiFunction<DataSet, Object[], Boolean> checkFunc, boolean isDebug) {
        this.dbf = dbf;
        this.dao = dao;
        this.checkFunc = checkFunc;
        this.isDebug = isDebug;
        tableName = this.dbf.getName().replace('.', '_');
    }


    public void updateData() {
        long t = System.currentTimeMillis();

        try {
            DbfReader reader = new DbfReader(dbf);
            //check if table exists
            DataSet set = dao.exec("select tname from tab where tname = :tname", tableName);

            if (set.isEmpty())
                createTable(reader);

            //get last key values from Oracle
            set = dao.exec("select * from (select * from " + tableName + " order by last_order desc) where rownum = 1");

            //start check from last and put to dataset revers order
            DataSet result = checkLast(reader, set);

            //insert data
            insertData(result);

            reader.close();
        } catch (Exception e) {
            log(e.toString());
        }

        log("Executed time: " + (System.currentTimeMillis() - t));
    }

    private Object[] addTimeOrder(Object[] src) {
        Object[] dest = new Object[src.length + 1];
        System.arraycopy(src, 0, dest, 0, src.length);
        dest[src.length] = ++last_order;
        return dest;
    }

    private DataSet checkLast(DbfReader reader, DataSet set) {
        DataSet result = new DataSet(set.getCachedNames());
        last_order = set.getDecimal("last_order").longValue();

        if (!set.isEmpty()) {
            int count = reader.getRecordCount();

            while (count > 0) {
                reader.seekToRecord(--count);
                Object[] dataRow = reader.nextRecord();

                if (!checkFunc.apply(set, dataRow))
                    break;

                result.add(0, addTimeOrder(dataRow));
            }

            int n = result.getColCount() - 1;
            int cnt = result.size();
            for (int i = 0; i < cnt; i++)
                result.get(i)[n] = last_order - cnt + 1 + i;

        } else {
            Object[] dataRow;
            while ((dataRow = reader.nextRecord()) != null)
                result.add(addTimeOrder(dataRow));
        }


        return result;
    }

    private void createTable(DbfReader reader) throws Exception {
        StringBuilder builder = new StringBuilder("create table ");
        builder.append(tableName).append('(');
        int count = reader.getHeader().getFieldsCount();
        for (int i = 0; i < count; i++) {
            DbfField fld = reader.getHeader().getField(i);
            builder.append('"').append(fld.getName()).append('"').append(' ');
            switch (fld.getDataType()) {
                case DATE:
                    builder.append("DATE");
                    break;
                case CHAR:
                    builder.append("VARCHAR2(255)");
                    break;
                case LOGICAL:
                    builder.append("BOOLEAN");
                    break;
                default:
                    builder.append("NUMBER");
            }
            builder.append(',');
        }
        builder.append("LAST_ORDER NUMBER NOT NULL UNIQUE)");

        log(builder);
        dao.exec(builder.toString());
        log("Table executed: " + tableName);
    }

    private void insertData(DataSet result) throws Exception {
        if (result.isEmpty())
            return;

        StringBuilder builder2 = new StringBuilder("insert into ").append(tableName).append(" values(");
        for (String s : result.getCachedNames().keySet())
            builder2.append(":").append(s).append(",");

        builder2.setLength(builder2.length() - 1);
        builder2.append(")");

        log(builder2);
        dao.executeBatch(builder2.toString(), result);
        dao.commit();
        log("Rows added: " + result.size());
    }

    private void log(Object msg) {
        try {
            String text = tableName + " - " + new Date() + ": " + msg.toString() + "\r\n";
            if (isDebug) {
                Path path = Paths.get(tableName + ".log");
                if (Files.notExists(path))
                    Files.createFile(path);

                Files.write(path, text.getBytes(), StandardOpenOption.APPEND);
            } else {
                System.out.print(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
