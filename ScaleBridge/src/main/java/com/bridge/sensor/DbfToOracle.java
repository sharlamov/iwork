package com.bridge.sensor;

import com.bridge.bin.Util;
import com.bridge.enums.IFireListener;
import com.dao.model.DataSet;
import com.dao.service.JDBCFactory;
import org.jamel.dbf.DbfReader;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DbfToOracle implements IFireListener {

    private DateFormat dateFormat1;
    private DateFormat dateFormat2;
    private File dbf;
    private Map<Double, Integer> unaMap;
    private DataSet listValues;

    public DbfToOracle(String fName, Map<Double, Integer> unaMap) {
        this.dbf = new File(fName);
        this.unaMap = unaMap;

        dateFormat1 = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat2 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        listValues = DataSet.init("unaId", unaMap.values());
    }

    private DataSet prepare(DbfReader reader, DataSet set) throws ParseException {
        DataSet result = new DataSet("fid", "fdate", "fcant");
        Date oldDate = set.getObject("lastDate") == null ? new Date(0) : (Date) set.getObject("lastDate");

        int count = reader.getRecordCount();
        while (count > 0) {
            reader.seekToRecord(--count);
            Object[] dataRow = reader.nextRecord();

            /*date*/
            String time1 = new String((byte[]) dataRow[5]);
            if (time1.trim().isEmpty())
                continue;

            String val2 = dateFormat1.format(dataRow[0]) + " " + time1;
            Date newDate = dateFormat2.parse(val2);

            if (newDate.after(oldDate)) {
                Double d = dataRow[7] == null ? 1d : (Double) dataRow[7];
                result.add(0, new Object[]{unaMap.get(d), new Timestamp(newDate.getTime()), dataRow[8], null});
            }
        }

        return result;
    }

    @Override
    public void fire(JDBCFactory factory, String sql) {
        try (DbfReader reader = new DbfReader(dbf)) {
            //get last key values from Oracle
            DataSet set = factory.exec("select max(fDate) lastDate from tmdb_prodscales where fid in (:unaId)", listValues);
            //start check from last and put to dataset revers order
            DataSet result = prepare(reader, set);

            factory.executeBatch(sql, result);
            factory.commit();
            Util.log("Rows added: " + result.size());
        } catch (Exception e) {
            Util.log(e.toString());
        }
    }
}
