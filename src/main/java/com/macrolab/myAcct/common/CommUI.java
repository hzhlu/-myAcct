package com.macrolab.myAcct.common;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class CommUI {

    public static void warningBox(String header, String content) {
        msgBox(Alert.AlertType.WARNING, "警告信息", header, content);
    }

    public static void infoBox(String header, String content) {
        msgBox(Alert.AlertType.INFORMATION, "通知信息", header, content);
    }

    public static void errorBox(String header, String content) {
        msgBox(Alert.AlertType.ERROR, "错误信息", header, content);
    }

    public static Optional<ButtonType> confirmBox(String header, String content) {
        return msgBox(Alert.AlertType.CONFIRMATION, "确认信息", header, content);
    }

    public static Optional<ButtonType> msgBox(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }


    public static String txtInputDlg(String title, String header, String prompt, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(prompt);

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }

    }

}
