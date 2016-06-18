package com.util;

import com.service.LangService;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Msg {

    public static void eMsg(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }


   /*  public static String fMsg(String name, String query, String text, Component parent) {
        return (String) JOptionPane.showInputDialog(
                parent, LangService.trans(query), LangService.trans(name),
                JOptionPane.PLAIN_MESSAGE, Pictures.filterIcon, null, text);
    }*/

    public static boolean qMsg(String name, String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(LangService.trans(name));
        alert.setHeaderText(null);
        alert.setContentText(LangService.trans(question));

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
}
