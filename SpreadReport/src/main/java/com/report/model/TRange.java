package com.report.model;

import com.dao.model.CustomItem;
import com.dao.model.IDataSet;
import com.report.bin.ReportGear;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.*;

public class TRange extends ArrayList<Row> {

    private final Sheet sheet;
    private Map<CellStyle, CellStyle> styles;
    private List<CellRangeAddress> merged;

    public TRange(Sheet sheet) {
        super();
        this.sheet = sheet;

        styles = new HashMap<>();
        merged = new ArrayList<>();
    }

    public void paint(IDataSet set, boolean useFullSet) {
        int count = useFullSet ? set.size() : 1;
        int startNr = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < count; i++) {
            for (Row row : this) {
                createNewRow(row, set, i);
            }
            setMerge(startNr);
            startNr += size();
        }
    }

    public Object getValue(String name, IDataSet set, int nr) {
        if (name.startsWith("_")) {
            return set.sum(name.substring(1));
        } else {
            Integer col = set.findField(name);
            Object obj = set.getObject(nr, col);
            return obj == null ? "" : obj;
        }
    }

    protected Row addNewRow() {
        return sheet.createRow(sheet.getPhysicalNumberOfRows());
    }

    protected void createNewRow(Row rowOld, IDataSet set, int nr) {
        Row rowNew = addNewRow();
        rowNew.setHeight(rowOld.getHeight());
        for (int j = ReportGear.INDENT; j < rowOld.getLastCellNum(); j++) {
            Cell cellOld = rowOld.getCell(j);
            if (cellOld != null) {
                Cell cellNew = rowNew.createCell(cellOld.getColumnIndex() - ReportGear.INDENT);
                initCell(cellOld, cellNew, set, nr);
            }
        }
    }

    public void addMergedRange(Sheet oldSheet) {
        OptionalInt min = stream().mapToInt(Row::getRowNum).min();
        if (min.isPresent()) {
            int firstRowNr = min.getAsInt();
            int lastRowNr = firstRowNr + size();
            oldSheet.getMergedRegions().stream().filter(ra -> ra.getFirstRow() >= firstRowNr && ra.getLastRow() <= lastRowNr).forEach(ra -> {
                ra.setFirstRow(ra.getFirstRow() - firstRowNr);
                ra.setLastRow(ra.getLastRow() - firstRowNr);
                merged.add(ra);
            });
        }
    }

    public void setMerge(int startRow) {
        for (CellRangeAddress ra : merged) {
            sheet.addMergedRegion(new CellRangeAddress(
                    ra.getFirstRow() + startRow, //first row (0-based)
                    ra.getLastRow() + startRow, //last row  (0-based)
                    ra.getFirstColumn() - ReportGear.INDENT, //first column (0-based)
                    ra.getLastColumn() - ReportGear.INDENT  //last column  (0-based)
            ));
        }
    }

    public void initCell(Cell cOld, Cell cNew, IDataSet set, int nr) {

        if (styles.containsKey(cOld.getCellStyle())) {
            cNew.setCellStyle(styles.get(cOld.getCellStyle()));
        } else {
            CellStyle newStyle = sheet.getWorkbook().createCellStyle();
            newStyle.cloneStyleFrom(cOld.getCellStyle());
            cNew.setCellStyle(newStyle);
            styles.put(cOld.getCellStyle(), newStyle);
        }

        cNew.setCellComment(cOld.getCellComment());
        cNew.setHyperlink(cOld.getHyperlink());
        cNew.setCellType(cOld.getCellType());

        switch (cOld.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cNew.setCellValue(cOld.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                cNew.setCellErrorValue(cOld.getErrorCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: {
                String value = parseContent(cOld.getCellFormula(), set, nr).toString();
                cNew.setCellFormula(copyFormula(cOld, cNew, value));
            }
            break;
            case Cell.CELL_TYPE_NUMERIC:
                cNew.setCellValue(cOld.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: {
                Object value = parseContent(cOld.getStringCellValue(), set, nr);

                if (value instanceof Date)
                    cNew.setCellValue((Date) value);
                else if (value instanceof Number)
                    cNew.setCellValue(((Number) value).doubleValue());
                else if (value instanceof String)
                    cNew.setCellValue(value.toString());
                else if (value instanceof CustomItem)
                    cNew.setCellValue(((CustomItem) value).getLabel());
                else
                    cNew.setCellValue("");
            }
            break;
            default:
                cNew.setCellValue(cOld.getStringCellValue());
        }
    }

    protected Object parseContent(String content, IDataSet set, int nr) {
        char LF = '\uFFFF';

        String val = content.trim();
        int len = val.length();

        StringBuilder resString = new StringBuilder();
        StringBuilder param = new StringBuilder();
        boolean useParam = false;


        for (int j = 0; j < len; j++) {
            char fch = (j == 0) ? LF : val.charAt(j - 1);
            char ch = val.charAt(j);
            char lch = len == j + 1 ? LF : val.charAt(j + 1);

            if (useParam) {
                if (isNeedLetter(ch))
                    param.append(ch);

                if (resString.length() == 0 && lch == LF) {
                    return getValue(param.toString(), set, nr);
                }

                if (resString.length() != 0 && lch == LF || !isNeedLetter(lch)) {
                    resString.append(getValue(param.toString(), set, nr));
                    param.setLength(0);
                    useParam = false;
                }
            } else {
                if (ch == '_' && !isNeedLetter(fch)) {
                    useParam = true;
                } else {
                    resString.append(ch);
                }
            }
        }
        return resString.toString();
    }

    protected boolean isNeedLetter(char ch) {
        return ch == '_' || Character.isLetterOrDigit(ch);
    }

    public String copyFormula(Cell oldCell, Cell newCell, String oldFormula) {

        String newFormula = "";
        Sheet sheet = oldCell.getSheet();
        Workbook workbook = sheet.getWorkbook();

        try {
            if (oldFormula != null) {
                Ptg[] ptgs = FormulaParser.parse(oldFormula, ReportGear.parsingBook, FormulaType.CELL, workbook.getSheetIndex(sheet));
                int nri = newCell.getRowIndex();
                int ori = oldCell.getRowIndex();
                int nci = newCell.getColumnIndex();
                int oci = oldCell.getColumnIndex();

                for (Ptg ptg : ptgs) {

                    if (ptg instanceof RefPtgBase) // for references such as A1, A2, B3
                    {
                        RefPtgBase refPtgBase = (RefPtgBase) ptg;

                        if (refPtgBase.isRowRelative())
                            refPtgBase.setRow(calcIndent(refPtgBase.getRow(), nri, ori));

                        if (refPtgBase.isColRelative())
                            refPtgBase.setColumn(calcIndent(refPtgBase.getColumn(), nci, oci));
                    }
                    if (ptg instanceof AreaPtgBase)  // for area of cells A1:A4
                    {
                        AreaPtgBase areaPtgBase = (AreaPtgBase) ptg;

                        // if first row is relative
                        if (areaPtgBase.isFirstRowRelative()) {
                            areaPtgBase.setFirstRow(nri - (ori - areaPtgBase.getFirstRow()));
                        }

                        // if last row is relative
                        if (areaPtgBase.isLastRowRelative()) {
                            areaPtgBase.setLastRow(nri - (ori - areaPtgBase.getLastRow()));
                        }

                        // if first column is relative
                        if (areaPtgBase.isFirstColRelative()) {
                            areaPtgBase.setFirstColumn(nci - (oci - areaPtgBase.getFirstColumn()));
                        }

                        // if last column is relative
                        if (areaPtgBase.isLastColRelative()) {
                            areaPtgBase.setLastColumn(nci - (oci - areaPtgBase.getLastColumn()));
                        }
                    }
                }
                newFormula = FormulaRenderer.toFormulaString(ReportGear.renderingBook, ptgs);
            }
            return newFormula;
        } catch (Exception e) {
            return null;
        }
    }

    private int calcIndent(int a, int b, int c) {
        return a + b - c;
    }
}
