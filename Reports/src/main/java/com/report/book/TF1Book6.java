package com.report.book;

import com.report.enums.TGraphType;
import org.apache.poi.ss.usermodel.Sheet;

import java.awt.*;


public interface TF1Book6 {

    void save();

    /*sheet*/
    int getActiveSheet();

    void setActiveSheet(int index);

    String getSheetName(int index);

    void setSheetName(String pSheetName, int index);

    int countSheets();

    void insertSheets(int count);

    /*sheet*/

    Object getCellStyle(int row, int col);

    void setCellStyle(Object style, int row, int col);

    /*Graph*/
    void ObjGetPos(Integer aId, Float nX1, Float nY1, Float nX2, Float nY2);

    TGraphType ObjGetType(Integer aId);

    void ObjSetSelection(Integer aId);

    void GetLineStyle(short nLineStyle, int nLineColor, short nLineWeight);

    void GetPattern(short nPattern, int nPatFG, int nPatBG);

    void EditCopy();

    void EditPaste();

    int ObjSelection(int i);

    void ObjSetPos(int nNewId, float f, float g, float h, float i);

    void ObjNew(TGraphType nType, float f, float g, float h, float i,
                int nNewId);

    void SetLineStyle(short nLineStyle, Color nLineColor, short nLineWeight);

    void SetPattern(short nPattern, Color nPatFG, Color nPatBG);

    int ObjFirstID();

    int ObjNextID(int nId);
    /*Graph*/

    int typeRC(int row, int col);

    String textRC(int row, int col);

    double numberRC(int row, int col);

    void setTextRC(String val, int row, int col);

    void setNumberRC(double val, int row, int col);


    void SetAlignment(int row, int col, Integer pHorizontal, Integer pWordWrap,
                      Integer pVertical, Integer pOrientation);

    void GetAlignment(int row, int col, Integer pHorizontal, Integer pWordWrap, Integer pVertical,
                      Integer pOrientation);

    void getBorder(int row, int col, Integer left, Integer right, Integer top, Integer bottom, Integer c0, Integer c1, Integer c2, Integer c3);

    void setBorder(int row, int col, Integer left, Integer right, Integer top, Integer bottom, Integer c0, Integer c1, Integer c2, Integer c3);

    int getRowHeight(int row);

    void setRowHeight(int rowHeight, int nDstRow);

    void setRowHeightAuto(int from, int to);

    void AddRowPageBreak(int i);

    int SelStartRow();

    int SelStartCol();

    int SelEndRow();

    int SelEndCol();

    String FormulaRC(int row, int col);

    void setFormulaRC(String formulaRC, int row, int col);

    boolean getRowHidden(int row);

    void setRowHidden(boolean isHidden, int row);

    int LastColForRow(int rowS);

    int LastCol();

    int LastRow();

    void SetColWidth(int i, int templateFormulasCols, int j, boolean b);

    int getColWidth(int i);

    void setColWidth(int colWidth, int i);

    Object GetPageSetup();

    void SetPageSetup(Object getPageSetup);

    void setAllowInCellEditing(boolean b);

    void setShowGridLines(boolean b);

    void setShowZeroValues(boolean bShowZeroValues);

    void setPrintTitles(String pPrintTitles);

    void setColWidthAuto(int firstCol, int currRow);

    void setCellDataFormat(String format, int row, int col);

    String getCellDataFormat(int row, int col);

    int getNumMergedRegions();

    void copyRow(int startRow, int endRow, int cnt);

    void setSelection(int row0, int col0, int row1, int col1);

    void setActiveCell(int pTopSRow, int j);

    boolean getShowHeading();

    void setShowHeading(boolean b);

    void deleteLine(int startIndex, int count, boolean isColumn);
}
