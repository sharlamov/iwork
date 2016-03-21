package com.dao;

import com.model.DataSet;
import com.util.Libra;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PoiExcelDAO {

    private Pattern p = Pattern.compile("_\\w+");

    public void makeReport(String path, DataSet header, DataSet master) throws Exception {
        long t = System.currentTimeMillis();
        File source = new File(path);
        File target = File.createTempFile("libra", ".xls");
        target.deleteOnExit();
        copyFile(source, target);

        FileInputStream fis = new FileInputStream(target);
        Workbook wb = WorkbookFactory.create(fis);
        Sheet sheet = wb.getSheetAt(0);

        for (int i = 0; i < wb.getNumberOfNames(); i++) {
            Name rangeName = wb.getNameAt(i);
            if (!sheet.getSheetName().equals(rangeName.getSheetName())) continue;

            AreaReference area = new AreaReference(rangeName.getRefersToFormula());
            CellReference crList[] = area.getAllReferencedCells();

            if (rangeName.getNameName().equalsIgnoreCase("TITLE") && header != null) {
                processHeader(sheet, crList, header);
            } else if (rangeName.getNameName().equalsIgnoreCase("DETAIL") && master != null) {
                processMaster(wb, sheet, crList, master);
            } else if (rangeName.getNameName().equalsIgnoreCase("SUMMARY") && master != null) {
                processSummary(sheet, crList, master);
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

    public void processHeader(Sheet sheet, CellReference[] crList, DataSet dataSet) {
        for (CellReference cr : crList) {
            Row row = sheet.getRow(cr.getRow());
            if (row == null) {
                continue;
            }
            Cell cell = row.getCell(cr.getCol());

            if (cell != null && Cell.CELL_TYPE_STRING == cell.getCellType()) {
                String val = cell.getStringCellValue();
                Matcher m = p.matcher(val);
                StringBuffer sb = new StringBuffer();//change to builder
                while (m.find()) {
                    String str = m.group();
                    Object obj = dataSet.getValueByName(str.substring(1), 0);
                    String dataString = "";
                    if (obj != null && obj.toString() != null) {
                        dataString = obj instanceof Date ? Libra.dateFormat.format(obj) : obj.toString();
                    }

                    if (dataString.contains("\\"))
                        dataString = dataString.replaceAll("\\\\", "/");
                    m.appendReplacement(sb, dataString);
                }
                m.appendTail(sb);
                cell.setCellValue(sb.toString());
            }
        }
    }

    public void processMaster(Workbook workbook, Sheet sheet, CellReference[] crList, DataSet dataSet) {
        int count = dataSet.getColumnCount() == 0 ? 0 : dataSet.size() - 1;

        Row row = null;
        java.util.List<Integer> cellCol = new ArrayList<Integer>();
        java.util.List<Integer> dataSetCol = new ArrayList<Integer>();

        for (CellReference cr : crList) {
            row = sheet.getRow(cr.getRow());
            Cell cell = row.getCell(cr.getCol());

            if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                String val = cell.getStringCellValue();
                Matcher m = p.matcher(val);
                while (m.find()) {
                    String str = m.group();
                    cellCol.add(cell.getColumnIndex());
                    dataSetCol.add(dataSet.findField(str.substring(1)));
                    cell.setCellValue("");
                }
            }
        }

        if (row != null) {
            int n = row.getRowNum();


            for (int i = 0; i < count; i++) {
                copyRow(sheet, n + i, n + i + 1);
            }

            for (int i = 0; i < count + 1; i++, n++) {
                Row r = sheet.getRow(n);
                for (int j = 0; j < cellCol.size(); j++) {
                    Cell c = r.getCell(cellCol.get(j));
                    if (dataSetCol.get(j) != -1) {
                        Object obj = dataSet.get(i)[dataSetCol.get(j)];
                        if (obj != null && obj.toString() != null) {
                            String dataString = obj instanceof Date ? Libra.dateFormat.format(obj) : obj.toString();
                            c.setCellValue(dataString);
                        }
                    }
                }
            }
        }
    }

    public void processSummary(Sheet sheet, CellReference[] crList, DataSet dataSet) {
        for (CellReference cr : crList) {
            Row row = sheet.getRow(cr.getRow());
            Cell cell = row.getCell(cr.getCol());

            if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                String val = cell.getStringCellValue();
                Matcher m = p.matcher(val);
                StringBuffer sb = new StringBuffer();//change to builder
                while (m.find()) {
                    String str = m.group();
                    Object obj = dataSet.getSumByColumn(str.substring(1));
                    m.appendReplacement(sb, obj.toString());
                }
                m.appendTail(sb);
                cell.setCellValue(sb.toString());
            }
        }
    }

    private void copyFile(File source, File dest) throws IOException {
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

    private void copyRow(Sheet worksheet, int sourceRowNum, int destinationRowNum) {
        // Get the source / new row
        Row newRow = worksheet.getRow(destinationRowNum);
        Row sourceRow = worksheet.getRow(sourceRowNum);

        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null) {
            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
        } else {
            newRow = worksheet.createRow(destinationRowNum);
        }

        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = newRow.createCell(i);

            // If the old cell is null jump to next cell
            if (oldCell == null)
                continue;


            // Copy style from old cell and apply to new cell
            /*
            CellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);*/

            newCell.setCellStyle(oldCell.getCellStyle());

            // If there is a cell comment, copy
            if (newCell.getCellComment() != null) newCell.setCellComment(oldCell.getCellComment());

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) newCell.setHyperlink(oldCell.getHyperlink());

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());

            // Set the cell data value
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
                default:
                    newCell.setCellValue(oldCell.getStringCellValue());
            }
        }
    }
}