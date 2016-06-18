package com.bin;

import com.dao.JdbcDAO;
import com.factories.CellFactory;
import com.factories.RowFactory;
import com.model.DataSet2;
import com.model.RowSet;
import com.model.settings.Settings;
import com.service.JsonService;
import com.util.Libra;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * @author Narayan
 */

public class DynamicTable extends Application {

    //TABLE VIEW AND DATA

    private TableView<RowSet> tableview;

    //MAIN EXECUTOR
    public static void main(String[] args) {
        launch(args);
    }

    //CONNECTION DATABASE
    public void buildData() {

        long before = Runtime.getRuntime().freeMemory();

        Libra.SETTINGS = JsonService.loadFile(Settings.class, "settings.json");
        JdbcDAO dao = new JdbcDAO();


        try {
            dao.getConnection();
            long l = System.currentTimeMillis();
            //ResultSet
            DataSet2 set = dao.select2("SELECT * from vmdb_scales where in_out = 0 and rownum < 10000", null);

            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
            int count = set.getColumnCount();
            for (int i = 0; i < count; i++) {
                TableColumn<RowSet, String> col = new TableColumn<>(set.getColumnName(i));
                col.setCellValueFactory(new CellFactory(i));

                tableview.getColumns().add(col);
            }


            /********************************
             * Data added to ObservableList *
             ********************************/

            tableview.setRowFactory(new RowFactory(set.findField("bgcolor")));
            tableview.setItems(FXCollections.observableList(set));
            System.out.println(System.currentTimeMillis() - l);

            long after = Runtime.getRuntime().freeMemory();
            double d = (double) (before - after) / (double) (1024 * 1024);
            System.out.println("Memory used:" + d + " Mb");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
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




