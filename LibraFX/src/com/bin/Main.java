package com.bin;

import com.controllers.LoginController;
import com.model.settings.Settings;
import com.service.JsonService;
import com.util.Libra;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    private ResourceBundle rb;
    private URL loginUrl;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(loginUrl, rb);
        Parent root = fxmlLoader.load();
        LoginController ac = fxmlLoader.getController();
        ac.setMainStage(primaryStage);
        primaryStage.getIcons().addAll(new Image("/resource/images/scale-icon32.png"));

        if (Libra.SETTINGS.isAuto()) {
            ac.makeLogin(null);
        } else {
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.setTitle(rb.getString("libra"));
        }
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        Libra.SETTINGS = JsonService.loadFile(Settings.class, "settings.json");
        Libra.libraService.open();
        rb = ResourceBundle.getBundle("Locale", Libra.SETTINGS.getLang());
        loginUrl = getClass().getResource("/resource/designs/login.fxml");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Libra.libraService.close();
    }
}