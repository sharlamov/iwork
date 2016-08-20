package com.bin;

import com.dao.JdbcDAO;
import com.factories.CellFactory2;
import com.factories.LibraColorRowFactory;
import com.model.DataSet;
import com.model.settings.DataGridSetting;
import com.model.settings.GridField;
import com.model.settings.Settings;
import com.service.JsonService;
import com.util.Libra;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Narayan
 */

public class DynamicTable extends Application {

    //TABLE VIEW AND DATA

    private TableView<Object[]> tableview;

    //MAIN EXECUTOR
    public static void main(String[] args) {
        launch(args);
    }

    //CONNECTION DATABASE
    public void buildData() throws Exception {

        long before = Runtime.getRuntime().freeMemory();

        Libra.SETTINGS = JsonService.loadFile(Settings.class, "settings.json");
        JdbcDAO dao = new JdbcDAO();

        dao.getConnection();
        //load design
        DataSet designs = dao.select("select lsection, ldata from libra_designs_tbl where lprofile = ?", new Object[]{"MDAUTO"});
        for (Object[] row : designs)
            Libra.designs.put(row[0].toString(), row[1].toString());
        DataGridSetting params = JsonService.fromJson(Libra.designs.get("DATAGRID.IN"), DataGridSetting.class);

        //ResultSet
        DataSet set = dao.select("SELECT * from vmdb_scales where in_out = 1 and rownum < 10000", null);
        long l = System.currentTimeMillis();


        int count = set.getColCount();
        int im = set.findField("MASA_NETTO");
        int ii = set.findField("INV_CANT");
        List<TableColumn<Object[], ?>> columns = new ArrayList<>(count);

        for (int i = 0; i < params.getNames().length; i++) {
            GridField gf = params.getNames()[i];
            int pos = set.findField(gf.getName());
            if (pos > -1) {
                TableColumn<Object[], String> col = new TableColumn<>(gf.getName());
                col.setId(gf.getName());
                col.setCellValueFactory(new CellFactory2(pos));
                columns.add(col);
            }
        }
        tableview.getProperties().put("limit", -20);
        tableview.getColumns().addAll(columns);

        /********************************
         * Data added to ObservableList *
         ********************************/

        tableview.setRowFactory(new LibraColorRowFactory(im, ii));
        tableview.getItems().addAll(set);
        System.out.println(System.currentTimeMillis() - l);

        long after = Runtime.getRuntime().freeMemory();
        double d = (double) (before - after) / (double) (1024 * 1024);
        System.out.println("Memory used:" + d + " Mb");

    }


    @Override
    public void start(Stage stage) throws Exception {
        //TableView
        tableview = new TableView<>();
        buildData();

        //Main Scene
        Scene scene = new Scene(tableview);
        scene.getStylesheets().add("/resource/styles/app.css");
        stage.setScene(scene);
        stage.show();
    }
}




