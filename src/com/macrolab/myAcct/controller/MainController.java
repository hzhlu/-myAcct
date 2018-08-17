package com.macrolab.myAcct.controller;

import com.macrolab.myAcct.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class MainController {
    @FXML
    Button btnNew;

    private Main application;

    /**
     * 与主程序关联
     * @param application
     */
    public void setApp(Main application){
        this.application = application;
    }


    /**
     * 创建资料账户记录
     */
    public void actionCreateAccount() {
        System.out.println("create account");
    }

    @FXML
    ListView<String> listAcct;

    public  void listOnEditStart(){
        System.out.println(listAcct.getEditingIndex());

    }
}
