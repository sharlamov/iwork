package com.controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AbstractController implements Initializable {

    private ResourceBundle resource;
    private Stage mainStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resource = resources;
    }

    public String translate(String key) {
        try{
            return resource.getString(key.trim().toLowerCase());
        }catch (Exception e){
            System.out.println("Error: Key = " + key + " not found!");
            return key;
        }
    }

    public Parent loadXML(String path) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(path));
        fxmlLoader.setResources(getResource());
        Parent root = fxmlLoader.load();

        AbstractController controller = fxmlLoader.getController();
        if (controller != null)
            controller.setMainStage(mainStage);

        return root;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public ResourceBundle getResource() {
        return resource;
    }

    public void setResource(ResourceBundle resource) {
        this.resource = resource;
    }
}
