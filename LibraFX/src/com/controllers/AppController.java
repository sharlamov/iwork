package com.controllers;

import com.factories.CellFactory;
import com.model.*;
import com.model.settings.DataGridSetting;
import com.model.settings.GridField;
import com.service.JsonService;
import com.util.Libra;
import com.util.Msg;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


public class AppController extends AbstractController {

    public ChoiceBox<Doc> chBox;
    public ComboBox<CustomItem> elevators;
    public ComboBox<CustomItem> divs;
    public DatePicker date1;
    public DatePicker date2;
    public TableView<RowSet> dataGrid;
    public ImageView halfBtn;

    private Collection<Doc> docList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        docList = JsonService.fromJsonList(Libra.designs.get("DOC.LIST"), Doc.class);
        chBox.setItems(FXCollections.observableArrayList(docList));
        chBox.getSelectionModel().selectFirst();

        //  for (Doc doc : docList) {
        //DataGridSetting lSetting = JsonService.fromJson(Libra.designs.get(doc.getId() == 1 ? "DATAGRID.IN" : "DATAGRID.OUT"), DataGridSetting.class);
        //    chBox.getItems().add(translate(doc.getName()));
        //tabbedPane.addTab(, Pictures.middleIcon, new LibraPanel(doc, lSetting));
        //}


        elevators.setItems(FXCollections.observableArrayList(Libra.filials.keySet()));
        elevators.getSelectionModel().selectFirst();
        if (Libra.filials.size() > 1) {
            elevators.getItems().set(0, new CustomItem(null, translate("all")));
        } else
            elevators.setVisible(false);

        Libra.initFilial(elevators, divs, true);
        initDates();
        initGrid();

    }

    private void initGrid() {
        DataGridSetting params = JsonService.fromJson(Libra.designs.get(chBox.getValue().getId() == 1 ? "DATAGRID.IN" : "DATAGRID.OUT"), DataGridSetting.class);
        String columnsString = "";
        for (int i = 0; i < params.getNames().length; i++) {
            GridField gridField = params.getNames()[i];

            columnsString += ", " + gridField.getName();
            TableColumn<RowSet, String> firstNameCol = new TableColumn<>(translate(gridField.getName()));
            firstNameCol.setPrefWidth(gridField.getSize());
            firstNameCol.setCellValueFactory(new CellFactory(i));
            dataGrid.getColumns().add(firstNameCol);
        }

        DataSet filter = new DataSet(new ArrayList<>(Arrays.asList("d1", "d2", "elevator", "silos", "div", "empty", "in_out", "type")));

        CustomItem item = elevators.getValue();
        filter.setValueByName("d1", 0, Date.from(date1.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        filter.setValueByName("d2", 0, Date.from(date2.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        filter.setValueByName("elevator", 0, item.getId() == null ? Libra.filials.keySet() : item);
        //filter.setValueByName("silos", 0, divs.getValue());
        filter.setValueByName("div", 0, divs.getValue());
        filter.setValueByName("empty", 0, 0);
        filter.setValueByName("in_out", 0, chBox.getValue().getId());
        filter.setValueByName("type", 0, chBox.getValue().getType());

        try {
            String str = params.getQuery().replace("*", columnsString.substring(1));
            DataSet2 set = Libra.libraService.executeQuery2(str, filter);

            /*ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();
            for (Object[] objects : set) {
                ObservableList<Object> row = FXCollections.observableArrayList();
                Collections.addAll(row, objects);
                data.add(row);
            }
*/
            //dataGrid.setRowFactory(new RowFactory(set.findField("bgcolor")));

            dataGrid.setItems(FXCollections.observableArrayList(set));
        } catch (Exception e) {
            e.printStackTrace();
            Msg.eMsg(e.getMessage());
        }
    }
    private void initDates() {
        Locale.setDefault(Libra.SETTINGS.getLang());

        LocalDate l = LocalDate.now();
        date1.setValue(l);
        date2.setValue(l);

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
}
