package md.sh.model.struct;

import md.sh.bin.ReportGear;
import md.sh.model.DataSet;
import md.sh.model.IDataSet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TBand {

    protected final Sheet sheet;
    public TRange band;
    protected List<CellRangeAddress> merged;
    private List<TGroup> groups;

    public TBand(Sheet sheet) {
        this.sheet = sheet;
        band = new TRange(sheet);
        merged = new ArrayList<>();
        groups = new LinkedList<>();
    }

    public void paint(IDataSet dataSet, boolean isFull) {
        if (groups.isEmpty()) {
            band.paint(dataSet, isFull);
        } else {
            paintGroup(dataSet, isFull, 0);
        }
    }

    public void paintGroup(IDataSet dataSet, boolean isFull, int i) {
        TGroup grp = groups.get(i);
        List<DataSet> collect = dataSet.groupBy(grp.getFieldName());

        for (DataSet set : collect) {
            grp.getHeader().paint(set, false);

            if (groups.size() - 1 == i) {
                band.paint(set, isFull);
            } else
                paintGroup(set, isFull, i + 1);

            grp.getFooter().paint(set, false);
        }
    }

    public void installRanges(int yi) {
        for (CellRangeAddress ra : merged) {
            sheet.addMergedRegion(new CellRangeAddress(
                    ra.getFirstRow() + yi, //first row (0-based)
                    ra.getLastRow() + yi, //last row  (0-based)
                    ra.getFirstColumn() - ReportGear.INDENT, //first column (0-based)
                    ra.getLastColumn() - ReportGear.INDENT  //last column  (0-based)
            ));
        }
    }

    public void add(Row row) {
        band.add(row);
    }


   /* public boolean isInRange(CellRangeAddress rangeAddress) {
        for (int i = rangeAddress.getFirstRow(); i <= rangeAddress.getLastRow(); i++) {
            boolean inRange = false;
            for (Row row : rows) {
                if (row.getRowNum() == i) {
                    inRange = true;
                    break;
                }
            }
            if (!inRange)
                return false;
        }
        return true;
    }*/

    public void addRange(CellRangeAddress ra) {
        merged.add(ra);
    }


    public void addGroup(boolean isHeader, Row row, String fieldName) {
        String fName = fieldName.toUpperCase();
        //groups.stream().filter(grp -> grp.getFieldName().equals(fName))
        for (TGroup group : groups) {
            if (group.getFieldName().equals(fName)) {
                group.addRow(isHeader, row);
                return;
            }
        }

        if (isHeader)
            groups.add(new TGroup(sheet, fieldName, row, true));
        else
            groups.add(0, new TGroup(sheet, fieldName, row, false));
    }
}
