package com.factories;

import com.util.Libra;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

public class CellFactory2 implements Callback<TableColumn.CellDataFeatures<Object[], String>, ObservableValue<String>> {
    DecimalFormat numberFormat = new DecimalFormat("#,###.##");
    private int col;

    public CellFactory2(int col) {
        this.col = col;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Object[], String> param) {
        Object[] v = param.getValue();
        TableColumn<Object[], String> clmn = param.getTableColumn();
        Object value = v[col];

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
