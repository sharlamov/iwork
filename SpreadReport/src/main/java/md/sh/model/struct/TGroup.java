package md.sh.model.struct;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Collections;

public class TGroup {

    private String fieldName;
    private TRange header;
    private TRange footer;

    public TGroup(Sheet sheet, String fieldName, Row row, boolean isHeader) {
        setFieldName(fieldName);
        header = new TRange(sheet);
        footer = new TRange(sheet);
        addRow(isHeader, row);
    }

    public void addRow(boolean isHeader, Row... row) {
        Collections.addAll(isHeader ? header : footer, row);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public TRange getHeader() {
        return header;
    }

    public void setHeader(TRange header) {
        this.header = header;
    }

    public TRange getFooter() {
        return footer;
    }

    public void setFooter(TRange footer) {
        this.footer = footer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TGroup tGrp = (TGroup) o;

        return fieldName.equals(tGrp.fieldName);
    }

    @Override
    public int hashCode() {
        return fieldName.hashCode();
    }
}
