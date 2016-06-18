package com.controllers;

import com.service.JsonService;
import com.service.LibraService;
import com.util.Libra;
import com.util.Msg;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import oracle.net.ns.NetException;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController extends AbstractController {

    @FXML
    public PasswordField passField;
    @FXML
    public TextField loginField;
    @FXML
    public Text langBox;
    @FXML
    public Pane formId;
    @FXML
    public Text titleLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        titleLabel.setText(Libra.TITLE);
        loginField.setText(Libra.SETTINGS.getUsername());
        passField.setText(Libra.SETTINGS.getPassword());
    }

    public void changeLanguage(Event event) {

        Libra.SETTINGS.setLang(new Locale(langBox.getText().equals("Ro") ? "ru" : "ro"));

        try {
            setResource(ResourceBundle.getBundle("resource.locale", Libra.SETTINGS.getLang()));
            getMainStage().setTitle(getResource().getString("libra"));
            Pane newNode = (Pane) loadXML("/resource/designs/login.fxml");
            ((Pane) getMainStage().getScene().getRoot()).getChildren().setAll(newNode.getChildren());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void makeLogin(ActionEvent actionEvent) {
        try {
            if (Libra.libraService.login(loginField.getText(), passField.getText())) {
                JsonService.saveFile(Libra.SETTINGS, "settings.json");

                Parent root = loadXML("/resource/designs/app.fxml");
                Scene scene = getMainStage().getScene();

                boolean isAuto = scene == null;
                if (isAuto)
                    getMainStage().setScene(new Scene(root));
                else
                    scene.setRoot(root);

                getMainStage().setResizable(true);
                getMainStage().setMaximized(!isAuto);
                getMainStage().setTitle(Libra.TITLE + " - " + LibraService.user.getUsername() + ": " + Libra.filials.keySet());
            }
        } catch (Exception e1) {
            if (e1.getCause() instanceof NetException)
                Msg.eMsg(translate("error.neterror"));
            else {
                e1.printStackTrace();
                Msg.eMsg(translate(e1.getMessage()));
            }
        }
    }
}
