package com.factories;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class LibraColorRowFactory implements Callback<TableView<Object[]>, TableRow<Object[]>> {

    private int indNet;
    private int indInv;

    public LibraColorRowFactory(int indNet, int indInv) {
        this.indNet = indNet;
        this.indInv = indInv;
    }

    @Override
    public TableRow<Object[]> call(TableView<Object[]> param) {
        return new TableRow<Object[]>() {
            @Override
            protected void updateItem(Object[] paramT, boolean paramBoolean) {
                super.updateItem(paramT, paramBoolean);

                if (param.getProperties().containsKey("limit") && !paramBoolean) {
                    int limit = (int) param.getProperties().get("limit");
                    int mNet = getInt(paramT[indNet]);
                    int mInv = getInt(paramT[indInv]);

                    if (mNet > 0) {
                        if (mNet - mInv < limit) {
                            setStyle(style("#FF2020"));
                        } else if (mNet - mInv > limit && mNet - mInv < -1) {
                            setStyle(style("#FF9999"));
                        } else if (mNet == mInv) {
                            setStyle(style("#CCFFCC"));
                        } else if (mNet > mInv) {
                            setStyle(style("#55FF55"));
                        }
                    }
                } else {
                    setStyle(null);
                }
            }
        };
    }

    private int getInt(Object obj) {
        return obj == null ? 0 : Integer.parseInt(obj.toString());
    }

    public String style(String tColor) {
        return "-fx-control-inner-background: " + tColor + "; -fx-accent: derive(-fx-control-inner-background, -40%); -fx-cell-hover-color: derive(-fx-control-inner-background, -20%);";
    }
}
