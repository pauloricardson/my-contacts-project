package br.capacita.contatos.util;

import javafx.scene.control.Alert;

public class Alerts {
    public static void showAlerts(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}