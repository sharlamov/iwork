package com.report.work;

import com.report.book.TF1Book6;

public class TMergedCellsInfo {
    TF1Book6 FBook;
    int FStartRow, FStartCol, FEndRow, FEndCol;
    Object FCellFormat;
    String FText;

    public TMergedCellsInfo(TF1Book6 ABook) {
        FBook = ABook;
        FStartRow = FBook.SelStartRow();
        FStartCol = FBook.SelStartCol();
        FEndRow = FBook.SelEndRow();
        FEndCol = FBook.SelEndCol();
        FText = FBook.textRC(FStartRow, FStartCol);
        FCellFormat = FBook.getCellStyle(FStartRow, FStartCol);

        FBook.setCellStyle(FCellFormat, FStartRow, FStartCol);
    }

    void DeleteColumn(int ACol) {
        if (ACol > FEndCol)
            return;
        FEndCol--;
        if (ACol >= FStartCol)
            return;
        FStartCol--;
    }

    boolean IsCellInRange(int ARow, int ACol) {
        return (ARow >= FStartRow) && (ARow <= FEndRow) && (ACol >= FStartCol)
                && (ACol <= FEndCol);
    }
}
