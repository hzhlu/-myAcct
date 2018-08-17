package com.macrolab.myAcct.controller;

import com.macrolab.myAcct.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button btnSearch;
    @FXML
    private TextField txtSearch;
    @FXML
    private ListView<?> listAcct;
    @FXML
    private TextField txtAcctName;
    @FXML
    private Label labelKeyVerifyCode;
    @FXML
    private ChoiceBox<?> choiceDBFile;
    @FXML
    private PasswordField txtKey;
    @FXML
    private Button btnKeyVerifyCode;
    @FXML
    private Button btnCreateAcct;
    @FXML
    private Button btnBackupAcct;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    @FXML
    private Label labelUpdateDate;
    @FXML
    private Label labelCreateDate;
    @FXML
    private Label labelMAC;
    @FXML
    private TextArea txtContent;
    @FXML
    private Label labelPath;
    @FXML
    private Label labelContentChanged;


    private Main application;

    /**
     * 与主程序关联
     *
     * @param application
     */
    public void setApp(Main application) {
        this.application = application;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }


    @FXML
    private void actionSearch(ActionEvent event) {
    }

    @FXML
    private void listOnEditStart(ListView.EditEvent<String> event) {
    }

    @FXML
    private void actionKeyVerifyCode(ActionEvent event) {
    }

    @FXML
    private void actionCreateAcct(ActionEvent event) {
    }

    @FXML
    private void actionBackupAcct(ActionEvent event) {
    }

    @FXML
    private void actionNew(ActionEvent event) {
    }

    @FXML
    private void actionSave(ActionEvent event) {
    }

    @FXML
    private void actionDelete(ActionEvent event) {
    }


}
