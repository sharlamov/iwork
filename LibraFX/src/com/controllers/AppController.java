package com.controllers;

import com.factories.CellFactory2;
import com.factories.LibraColorRowFactory;
import com.model.CustomItem;
import com.model.DataSet;
import com.model.Doc;
import com.model.settings.DataGridSetting;
import com.model.settings.GridField;
import com.service.JsonService;
import com.util.Libra;
import com.util.Msg;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class AppController extends AbstractController {


    public ComboBox<CustomItem> elevators;
    public ComboBox<CustomItem> divs;
    public DatePicker date1;
    public DatePicker date2;
    public TableView<Object[]> dataGrid;
    public ImageView halfBtn;
    public Label lbCount;
    public Label lbBrut;
    public Label lbTara;
    public Label lbNet;
    public Button btnType;
    public SplitPane split;
    public VBox historyPane;

    private List<Doc> docList;
    private Doc selectedDoc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        docList = JsonService.fromJsonList(Libra.designs.get("DOC.LIST"), Doc.class);

        if (Libra.filials.size() > 1) {
            elevators.getItems().add(new CustomItem(null, translate("all")));
            elevators.getItems().addAll(Libra.filials.keySet());
            elevators.getSelectionModel().selectFirst();
        } else {
            elevators.getItems().addAll(Libra.filials.keySet());
            elevators.getSelectionModel().selectFirst();
            elevators.setVisible(false);
        }

        dataGrid.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> initHistory(newSelection));
        dataGrid.focusedProperty().addListener((val, before, after) -> closeHistory(after));
        btnType.setOnAction(e -> initDoc());
        Libra.initFilial(elevators, divs, true);
        initDates();
        initDoc();
    }

    private void closeHistory(Boolean after) {
        if (!after) {
            split.setDividerPositions(1.0f, 0.0);
        }
    }

    private void initGrid() {

        DataGridSetting params = JsonService.fromJson(Libra.designs.get(selectedDoc.getId() == 1 ? "DATAGRID.IN" : "DATAGRID.OUT"), DataGridSetting.class);

        CustomItem item = elevators.getSelectionModel().getSelectedItem();
        DataSet filter = new DataSet(
                "d1", Date.from(date1.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "d2", Date.from(date2.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "elevator", item.getId() == null ? Libra.filials.keySet() : item,
                "silos", null,
                "div", divs.getValue(),
                "empty", 0,
                "in_out", selectedDoc.getId(),
                "type", selectedDoc.getType());

        try {

            DataSet set = Libra.libraService.executeQuery(params.getQuery(), filter);
            long t = System.currentTimeMillis();

            int cCount = params.getNames().length;
            List<TableColumn<Object[], ?>> columns = new ArrayList<>(cCount);

            dataGrid.getItems().clear();
            dataGrid.getColumns().clear();

            for (int i = 0; i < cCount; i++) {
                GridField gf = params.getNames()[i];
                int pos = set.findField(gf.getName());
                if (pos > -1) {
                    TableColumn<Object[], String> col = new TableColumn<>(translate(gf.getName()));
                    col.setCellValueFactory(new CellFactory2(pos));
                    col.setPrefWidth(gf.getSize());
                    columns.add(col);
                }
            }
            dataGrid.getColumns().addAll(columns);
            if (selectedDoc.getId() == 1) {
                dataGrid.getProperties().put("limit", -20);
            } else {
                dataGrid.getProperties().remove("limit");
            }

            dataGrid.setRowFactory(new LibraColorRowFactory(set.findField("MASA_NETTO"), set.findField("INV_CANT")));
            dataGrid.getItems().addAll(set);

            lbCount.setText(translate("summary.count") + " " + Libra.decimalFormat.format(set.size()));
            lbBrut.setText(translate("summary.brutto") + " " + Libra.decimalFormat.format(set.sum("masa_brutto")));
            lbTara.setText(translate("summary.tara") + " " + Libra.decimalFormat.format(set.sum("masa_tara")));
            lbNet.setText(translate("summary.netto") + " " + Libra.decimalFormat.format(set.sum("masa_netto")));

            System.out.println(System.currentTimeMillis() - t + " - rrr");

        } catch (Exception e) {
            e.printStackTrace();
            Msg.eMsg(e.getMessage());
        }

    }

    private void initDates() {
        Locale.setDefault(Libra.SETTINGS.getLang());

        LocalDate l = LocalDate.now();
        date1.setValue(l);
        date1.setOnAction(event -> initGrid());

        date2.setValue(l);
        date2.setOnAction(event -> initGrid());

        final Callback<DatePicker, DateCell> dayCellFactory1 =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                setDisable(item.isAfter(date2.getValue()));
                            }
                        };
                    }
                };

        final Callback<DatePicker, DateCell> dayCellFactory2 =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                setDisable(item.isBefore(date1.getValue()));
                            }
                        };
                    }
                };
        date1.setDayCellFactory(dayCellFactory1);
        date2.setDayCellFactory(dayCellFactory2);
    }

    public void initDoc() {
        long t = System.currentTimeMillis();
        if (selectedDoc != null) {
            for (int i = 0; i < docList.size(); i++) {
                if (docList.get(i).getId() == selectedDoc.getId()) {
                    int k = i + 1 == docList.size() ? 0 : i + 1;
                    selectedDoc = docList.get(k);
                    break;
                }
            }
        } else {
            selectedDoc = docList.get(0);
        }
        btnType.setText(translate(selectedDoc.getName()));
        System.out.println(System.currentTimeMillis() - t + " - next");
        initGrid();
    }

    public void initHistory(Object[] row) {
        if (row == null)
            return;
        //System.out.println(row);
        try {
          /*  DataSet dataSet = Libra.libraService.executeQuery(SearchType.HISTORY.getSql(), new DataSet("id", row.get(0)));
            if (dataSet.size() > 0) {
                split.setDividerPositions(0.8f, 0.2f);
                for (int i = 0; i < dataSet.size(); i++) {
                    Label l = new Label(dataSet.getValueByName("masa", i).toString());
                    historyPane.getChildren().add(l);
                }
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
