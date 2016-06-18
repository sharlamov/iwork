package com.factories;

import com.model.RowSet;
import com.util.Libra;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class CellFactory implements Callback<TableColumn.CellDataFeatures<RowSet, String>, ObservableValue<String>> {
    DecimalFormat numberFormat = new DecimalFormat("#,###.##");
    private int col;

    public CellFactory(int col) {
        this.col = col;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<RowSet, String> param) {
        List<Object> v = param.getValue();
        TableColumn<RowSet, String> clmn = param.getTableColumn();
        Object value = v.get(col);

        if (value == null) {
            return null;
        } else {
            String test;
            if (value instanceof Date) {
                clmn.setStyle("-fx-alignment: CENTER;");
                test = Libra.dateFormat.format(value);
            } else if (value instanceof BigDecimal) {
                clmn.setStyle("-fx-alignment: CENTER_RIGHT;");
                test = numberFormat.format(value);
            } else {
                test = value.toString();
            }
            return new SimpleStringProperty(test);
        }
    }
}
