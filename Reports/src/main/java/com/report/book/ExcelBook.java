package com.report.book;

import com.report.enums.TGraphType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelBook implements TF1Book6 {

    private Workbook wb;
    private FileInputStream fis;
    private FileOutputStream fos;
    private DataFormat df;
    private int rowId, colId;

    public ExcelBook(String path) {
        File file = new File(path);
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                this.wb = new XSSFWorkbook(fis);
            } else {
                fos = new FileOutputStream(file);
                this.wb = new XSSFWorkbook();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            if (fos != null) {
                wb.write(fos);
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*sheet operation*/
    public int countSheets() {
        return wb.getNumberOfSheets();
    }

    public void insertSheets(int count) {
        for (int i = 0; i < count; i++)
            wb.createSheet();
    }

    public void setSheetName(String pSheetName, int index) {
        wb.setSheetName(index, pSheetName);
    }

    public String getSheetName(int index) {
        return wb.getSheetName(index);
    }

    public void setActiveSheet(int index) {
        wb.setActiveSheet(index);
    }

    public int getActiveSheet() {
        return wb.getActiveSheetIndex();
    }
    /*end sheet operation*/

    /*Graph*/
    public void ObjGetPos(Integer aId, Float nX1, Float nY1, Float nX2,
                          Float nY2) {
        // TODO Auto-generated method stub

    }

    public TGraphType ObjGetType(Integer aId) {
        // TODO Auto-generated method stub
        return null;
    }

    public void ObjSetSelection(Integer aId) {
        // TODO Auto-generated method stub

    }

    public void GetLineStyle(short nLineStyle, int nLineColor, short nLineWeight) {
        // TODO Auto-generated method stub

    }

    public void GetPattern(short nPattern, int nPatFG, int nPatBG) {
        // TODO Auto-generated method stub

    }

    public void EditCopy() {
        // TODO Auto-generated method stub

    }

    public void EditPaste() {
        // TODO Auto-generated method stub

    }

    public int ObjSelection(int i) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void ObjSetPos(int nNewId, float f, float g, float h, float i) {
        // TODO Auto-generated method stub

    }

    public void ObjNew(TGraphType nType, float f, float g, float h,
                       float i, int nNewId) {
        // TODO Auto-generated method stub

    }

    public void SetLineStyle(short nLineStyle, Color nLineColor,
                             short nLineWeight) {
        // TODO Auto-generated method stub

    }

    public void SetPattern(short nPattern, Color nPatFG, Color nPatBG) {
        // TODO Auto-generated method stub

    }

    public int ObjFirstID() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int ObjNextID(int nId) {
        // TODO Auto-generated method stub
        return 0;
    }
    /*Graph*/

    /*Cell*/
    private Cell getCell(int row, int col) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        Row line = sheet.getRow(row);
        return line.getCell(col);
    }

    public int typeRC(int row, int col) {
        return getCell(row, col).getCellType();
    }

    public String textRC(int row, int col) {
        Cell c = getCell(row, col);
        return c == null ? "" : getCell(row, col).getStringCellValue();
    }

    public double numberRC(int row, int col) {
        return getCell(row, col).getNumericCellValue();
    }

    public void setTextRC(String val, int row, int col) {
        getCell(row, col).setCellValue(val);
    }

    public void setNumberRC(double val, int row, int col) {
        getCell(row, col).setCellValue(val);
    }

    public String FormulaRC(int row, int col) {
        return getCell(row, col).getCellFormula();
    }

    public void setFormulaRC(String formulaRC, int row, int col) {
        getCell(row, col).setCellFormula(formulaRC);
    }

    public Object getCellStyle(int row, int col) {
        return getCell(row, col).getCellStyle();
    }

    public void setCellStyle(Object style, int row, int col) {
        getCell(row, col).setCellStyle((CellStyle) style);
    }

    /*Cell*/

    /*selection*/
    public void SetAlignment(int row, int col, Integer pHorizontal, Integer pWordWrap,
                             Integer pVertical, Integer pOrientation) {
        CellStyle cellStyle = getCell(row, col).getCellStyle();
        cellStyle.setAlignment(pHorizontal.shortValue());
        cellStyle.setVerticalAlignment(pVertical.shortValue());
        cellStyle.setRotation(pOrientation.shortValue());
        cellStyle.setWrapText(pWordWrap != 0);
    }

    public void GetAlignment(int row, int col, Integer pHorizontal, Integer pWordWrap,
                             Integer pVertical, Integer pOrientation) {
        CellStyle cellStyle = getCell(row, col).getCellStyle();
        pHorizontal = (int) cellStyle.getAlignment();
        pVertical = (int) cellStyle.getVerticalAlignment();
        pOrientation = (int) cellStyle.getRotation();
        pWordWrap = cellStyle.getWrapText() ? 1 : 0;
    }

    /*selection*/

    public void getBorder(int row, int col, Integer left, Integer right, Integer top, Integer bottom, Integer c0, Integer c1, Integer c2, Integer c3) {
        CellStyle cellStyle = getCell(row, col).getCellStyle();

        left = (int) cellStyle.getBorderLeft();
        right = (int) cellStyle.getBorderRight();
        top = (int) cellStyle.getBorderTop();
        bottom = (int) cellStyle.getBorderBottom();
        c0 = (int) cellStyle.getLeftBorderColor();
        c1 = (int) cellStyle.getRightBorderColor();
        c2 = (int) cellStyle.getTopBorderColor();
        c3 = (int) cellStyle.getBottomBorderColor();
    }

    public void setBorder(int row, int col, Integer left, Integer right, Integer top, Integer bottom, Integer c0, Integer c1, Integer c2, Integer c3) {
        CellStyle cellStyle = getCell(row, col).getCellStyle();

        cellStyle.setBorderLeft(left.shortValue());
        cellStyle.setBorderRight(right.shortValue());
        cellStyle.setBorderTop(top.shortValue());
        cellStyle.setBorderBottom(bottom.shortValue());
        cellStyle.setLeftBorderColor(c0.shortValue());
        cellStyle.setRightBorderColor(c1.shortValue());
        cellStyle.setTopBorderColor(c2.shortValue());
        cellStyle.setBottomBorderColor(c3.shortValue());
    }

    public int getRowHeight(int row) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        return sheet.getRow(row).getHeight();
    }

    public void setRowHeight(int rowHeight, int nDstRow) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        sheet.getRow(nDstRow).setHeight((short) rowHeight);
    }

    public void setRowHeightAuto(int from, int to) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        for (int i = from; i < to + 1; i++)
            sheet.getRow(i).setHeight((short) -1);
    }

    public void AddRowPageBreak(int i) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        sheet.setRowBreak(i);
    }

    public int SelStartRow() {
        return rowId;
    }

    public int SelStartCol() {
        return colId;
    }

    public int SelEndRow() {
        return rowId;
    }

    public int SelEndCol() {
        return colId;
    }

    public boolean getRowHidden(int row) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        CellStyle cs = sheet.getRow(row).getRowStyle();
        return cs != null && sheet.getRow(row).getRowStyle().getHidden();
    }

    public void setRowHidden(boolean isHidden, int row) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        sheet.getRow(row).getRowStyle().setHidden(isHidden);
    }

    public int LastColForRow(int rowS) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        return sheet.getRow(rowS).getLastCellNum();
    }

    public int LastCol() {
        return LastColForRow(0);
    }

    public int LastRow() {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        return sheet.getLastRowNum();
    }

    public void SetColWidth(int i, int templateFormulasCols, int j, boolean b) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        for (int c = i - 1; c < templateFormulasCols; c++) {
            sheet.setColumnWidth(c, j);
        }
    }

    public int getColWidth(int i) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        return sheet.getColumnWidth(i);
    }

    public void setColWidth(int colWidth, int i) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        sheet.setColumnWidth(colWidth, i);
    }

    public Object GetPageSetup() {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        return sheet.getPrintSetup();
    }

    public void SetPageSetup(Object getPageSetup) {
        // TODO Auto-generated method stub
    }

    public boolean getShowHeading() {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        return sheet.isDisplayRowColHeadings();
    }

    public void setShowHeading(boolean b) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        sheet.setDisplayRowColHeadings(b);
    }

    public void deleteLine(int startIndex, int count, boolean isColumn) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        if (isColumn) {
            for (Row r : sheet) {
                for (int i = startIndex; i < count; i++) {
                    Cell c = r.getCell(i);
                    if(c != null)
                        r.removeCell(c);
                }
            }
        } else {
            for (int i = startIndex; i < count + 1; i++)
                sheet.removeRow(sheet.getRow(i));
        }
    }

    public void setAllowInCellEditing(boolean b) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        for (Row r : sheet)
            for (Cell c : r)
                c.getCellStyle().setLocked(b);
    }

    public void setShowGridLines(boolean b) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        sheet.setPrintGridlines(b);
    }

    public void setShowZeroValues(boolean bShowZeroValues) {
        // TODO Auto-generated method stub
    }

    public void setPrintTitles(String pPrintTitles) {
        // TODO Auto-generated method stub
    }

    public void setColWidthAuto(int firstCol, int currRow) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        for(int i = firstCol; i < currRow + 1; i++ ){
            sheet.autoSizeColumn(i);
        }
    }

    public void setCellDataFormat(String format, int row, int col) {
        getCell(row, col).getCellStyle().setDataFormat(df.getFormat(format));
    }

    public String getCellDataFormat(int row, int col) {
        return getCell(row, col).getCellStyle().getDataFormatString();
    }

    public int getNumMergedRegions() {
        return wb.getSheetAt(wb.getActiveSheetIndex()).getNumMergedRegions();
    }

    public void copyRow(int startRow, int endRow, int cnt) {
        Sheet sheet = wb.getSheetAt(getActiveSheet());
        sheet.shiftRows(startRow, endRow, cnt);
    }

    public void setSelection(int row0, int col0, int row1, int col1) {
        rowId = row0;
        colId = col0;
    }

    public void setActiveCell(int row, int col) {
        rowId = row;
        colId = col;
    }
}
