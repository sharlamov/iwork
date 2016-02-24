package com.service;

import com.model.DataSet;
import com.util.Libra;
import org.apache.poi.ss.usermodel.*;

import java.awt.*;
import java.io.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportService {

    public static void openForm(String path, DataSet dataSet) throws Exception {
        long t = System.currentTimeMillis();

        String REGEX = "_\\w+";
        Pattern p = Pattern.compile(REGEX);
        File source = new File(path);
        File target = File.createTempFile("libra", ".xls");
        target.deleteOnExit();
        copyFile(source, target);

        FileInputStream fis = new FileInputStream(target);
        Workbook wb = WorkbookFactory.create(fis);
        Sheet sheet = wb.getSheetAt(0);

        for (Row row : sheet) {
            for (Cell cell : row) {
                if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                    String val = cell.getStringCellValue();
                    Matcher m = p.matcher(val);
                    StringBuffer sb = new StringBuffer();
                    while (m.find()) {
                        String str = m.group();
                        Object obj = dataSet.getValueByName(str.substring(1), 0);
                        String dataString = "";
                        if (obj != null && obj.toString() != null) {
                            dataString = obj instanceof Date ? Libra.dateFormat.format(obj) : obj.toString();
                        }
                        m.appendReplacement(sb, dataString);
                    }
                    m.appendTail(sb);
                    cell.setCellValue(sb.toString());
                }
            }
        }

        FileOutputStream fileOut = new FileOutputStream(target);
        wb.write(fileOut);
        fileOut.close();
        wb.close();
        fis.close();


        Desktop.getDesktop().open(target);

        System.out.println(System.currentTimeMillis() - t);

    }

    private static void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
