package md.sh.model.struct;

import md.sh.bin.ReportGear;
import md.sh.model.CustomItem;
import md.sh.model.IDataSet;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TRange extends ArrayList<Row> {

    private final Sheet sheet;
    private Map<CellStyle, CellStyle> styles;

    public TRange(Sheet sheet) {
        super();
        this.sheet = sheet;

        styles = new HashMap<>();
    }

    public void paint(IDataSet set, boolean useFullSet) {
        int count = useFullSet ? set.size() : 1;
        for (int i = 0; i < count; i++) {
            for (Row row : this) {
                createNewRow(row, set, i);
            }
        }
    }

    public Object getValue(String name, IDataSet set, int nr) {
        if (name.startsWith("_")) {
            return set.sum(name.substring(1));
        } else {
            Integer col = set.findField(name);
            return col > -1 ? set.getObject(nr, col) : null;
        }
    }

    protected Row addNewRow() {
        return sheet.createRow(sheet.getPhysicalNumberOfRows());
    }

    protected void createNewRow(Row rowOld, IDataSet set, int nr) {
        Row rowNew = addNewRow();
        for (int j = ReportGear.INDENT; j < rowOld.getLastCellNum(); j++) {
            Cell cellOld = rowOld.getCell(j);
            if (cellOld != null) {
                Cell cellNew = rowNew.createCell(cellOld.getColumnIndex() - ReportGear.INDENT);
                initCell(cellOld, cellNew, set, nr);
            }
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
                cNew.setCellValue(cOld.getStringCellValue());
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
                    cNew.setCellValue(Double.valueOf(value.toString()));
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
