package com.report.bin;

import com.dao.model.DataSet;
import com.report.model.TBand;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ReportGear {

    //join fields
    public static int INDENT = 3;
    public static FormulaParsingWorkbook parsingBook = null;
    public static FormulaRenderingWorkbook renderingBook = null;
    private TBand title;
    private TBand detail;
    private TBand summary;
    private DataSet header;
    private DataSet master;
    private int columnCount;

    public void make(File source, DataSet header, DataSet master) throws Exception {
        this.header = header;
        this.master = master;

        if (header == null || master == null)
            throw new Exception("Dataset is empty");

        long t = System.currentTimeMillis();

        InputStream inp = new FileInputStream(source);
        Workbook oldBook = WorkbookFactory.create(inp);
        Sheet oldSheet = oldBook.getSheetAt(0);

        Workbook newBook = new HSSFWorkbook();
        Sheet newSheet = newBook.createSheet(oldSheet.getSheetName());

        init(newBook);
        process(oldSheet, newSheet);

        File target = File.createTempFile("libra", ".xls");
        target.deleteOnExit();
        FileOutputStream fileOut = new FileOutputStream(target);

        newBook.write(fileOut);
        fileOut.close();
        oldBook.close();
        inp.close();

        Desktop.getDesktop().open(target);

        System.out.println(System.currentTimeMillis() - t);
    }

    private void process(Sheet oldSheet, Sheet newSheet) {
        printSetup(oldSheet, newSheet);

        title = new TBand(newSheet);
        detail = new TBand(newSheet);
        summary = new TBand(newSheet);

        oldSheet.rowIterator().forEachRemaining(this::processRow);

        widthSetup(oldSheet, newSheet);
        title.addMerged(oldSheet);
        detail.addMerged(oldSheet);
        summary.addMerged(oldSheet);

        title.paint(header, false);
        detail.paint(master, true);
        master.concat(header);
        summary.paint(master, false);
    }

    private void processRow(Row row) {
        int fc = row.getFirstCellNum();
        if (fc == 0) {
            Cell cell = row.getCell(0);
            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                columnCount = columnCount < row.getLastCellNum() ? row.getLastCellNum() : columnCount;

                String rowType = cell.getStringCellValue().toUpperCase();
                switch (rowType) {
                    case "TITLE": {
                        title.add(row);
                    }
                    break;
                    case "GROUPH": {
                        String fName = getGroupParam(row);
                        if (!fName.isEmpty()) {
                            detail.addGroup(true, row, fName);
                        }
                    }
                    break;
                    case "DETAIL1": {
                        detail.add(row);
                    }
                    break;
                    case "GROUPF": {
                        String fName = getGroupParam(row);
                        if (!fName.isEmpty()) {
                            detail.addGroup(false, row, fName);
                        }
                    }
                    break;
                    case "SUMMARY": {
                        summary.add(row);
                    }
                    break;
                }
            }
        }
    }

    private String getGroupParam(Row row) {
        Cell cell = row.getCell(1);
        return cell != null && cell.getCellType() == 1 ? cell.getStringCellValue().replace("_", "").toUpperCase() : "";
    }

    private void printSetup(Sheet oldSheet, Sheet newSheet) {
        //print setup
        PrintSetup oldPS = oldSheet.getPrintSetup();
        PrintSetup newPS = newSheet.getPrintSetup();

        newPS.setPaperSize(oldPS.getPaperSize());
        newPS.setScale(oldPS.getScale());
        newPS.setPageStart(oldPS.getPageStart());
        newPS.setFitWidth(oldPS.getFitWidth());
        newPS.setFitHeight(oldPS.getFitHeight());
        newPS.setLeftToRight(oldPS.getLeftToRight());
        newPS.setLandscape(oldPS.getLandscape());
        newPS.setValidSettings(oldPS.getValidSettings());
        newPS.setNoColor(oldPS.getNoColor());
        newPS.setDraft(oldPS.getDraft());
        newPS.setNotes(oldPS.getNotes());
        newPS.setNoOrientation(oldPS.getNoOrientation());
        newPS.setUsePage(oldPS.getUsePage());
        newPS.setHResolution(oldPS.getHResolution());
        newPS.setVResolution(oldPS.getVResolution());
        newPS.setHeaderMargin(oldPS.getHeaderMargin());
        newPS.setFooterMargin(oldPS.getFooterMargin());
        newPS.setCopies(oldPS.getCopies());

        newSheet.setRightToLeft(oldSheet.isRightToLeft());
        newSheet.setHorizontallyCenter(oldSheet.getHorizontallyCenter());
        newSheet.setVerticallyCenter(oldSheet.getVerticallyCenter());
        newSheet.setAutobreaks(oldSheet.getAutobreaks());
        newSheet.setDisplayZeros(oldSheet.isDisplayZeros());
        newSheet.setFitToPage(oldSheet.getFitToPage());
        newSheet.setAutobreaks(oldSheet.getAutobreaks());
        newSheet.setPrintGridlines(oldSheet.isPrintGridlines());

        newSheet.getHeader().setCenter(oldSheet.getHeader().getCenter());
        newSheet.getHeader().setLeft(oldSheet.getHeader().getLeft());
        newSheet.getHeader().setRight(oldSheet.getHeader().getRight());

        newSheet.getFooter().setCenter(oldSheet.getFooter().getCenter());
        newSheet.getFooter().setLeft(oldSheet.getFooter().getLeft());
        newSheet.getFooter().setRight(oldSheet.getFooter().getRight());

        newSheet.setMargin(Sheet.LeftMargin, oldSheet.getMargin(Sheet.LeftMargin));
        newSheet.setMargin(Sheet.RightMargin, oldSheet.getMargin(Sheet.RightMargin));
        newSheet.setMargin(Sheet.HeaderMargin, oldSheet.getMargin(Sheet.HeaderMargin));
        newSheet.setMargin(Sheet.FooterMargin, oldSheet.getMargin(Sheet.FooterMargin));
        newSheet.setMargin(Sheet.BottomMargin, oldSheet.getMargin(Sheet.BottomMargin));
        newSheet.setMargin(Sheet.TopMargin, oldSheet.getMargin(Sheet.TopMargin));
    }

    private void widthSetup(Sheet oldSheet, Sheet newSheet) {
        for (int i = 0; i < columnCount; i++)
            newSheet.setColumnWidth(i, oldSheet.getColumnWidth(i + INDENT));
    }

    private void init(Workbook book) {
        if (book instanceof HSSFWorkbook) {
            parsingBook = HSSFEvaluationWorkbook.create((HSSFWorkbook) book);
            renderingBook = HSSFEvaluationWorkbook.create((HSSFWorkbook) book);
        } else if (book instanceof XSSFWorkbook) {
            parsingBook = XSSFEvaluationWorkbook.create((XSSFWorkbook) book);
            renderingBook = XSSFEvaluationWorkbook.create((XSSFWorkbook) book);
        }
    }
}
