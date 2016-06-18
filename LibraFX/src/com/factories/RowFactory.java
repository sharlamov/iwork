package com.factories;

import com.model.RowSet;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class RowFactory implements Callback<TableView<RowSet>, TableRow<RowSet>> {

    private int colorRow;

    public RowFactory(int colorRow) {
        this.colorRow = colorRow;
    }

    @Override
    public TableRow<RowSet> call(TableView<RowSet> param) {

        return new TableRow<RowSet>() {
            @Override
            protected void updateItem(RowSet paramT, boolean paramBoolean) {
                if (!paramBoolean && paramT.get(colorRow) != null && colorRow > -1) {
                    switch (paramT.get(colorRow) != null ? paramT.get(colorRow).toString() : "") {
                        case "6711039":
                            setStyle("-fx-control-inner-background: #FF2020; -fx-accent: derive(-fx-control-inner-background, -40%); -fx-cell-hover-color: derive(-fx-control-inner-background, -20%);");
                            break;
                        case "13421823":
                            setStyle("-fx-control-inner-background: #FF9999; -fx-accent: derive(-fx-control-inner-background, -40%); -fx-cell-hover-color: derive(-fx-control-inner-background, -20%);");
                            break;
                        case "13434828":
                            setStyle("-fx-control-inner-background: #CCFFCC; -fx-accent: derive(-fx-control-inner-background, -40%); -fx-cell-hover-color: derive(-fx-control-inner-background, -20%);");
                            break;
                        case "5635925":
                            setStyle("-fx-control-inner-background: #55FF55; -fx-accent: derive(-fx-control-inner-background, -40%); -fx-cell-hover-color: derive(-fx-control-inner-background, -20%);");
                            break;
                    }
                }

                super.updateItem(paramT, paramBoolean);
            }
        };
    }
}
