package com.macrolab.myAcct;

import com.macrolab.myAcct.common.AppContext;
import com.macrolab.myAcct.model.TMyAcct;
import com.macrolab.myAcct.service.DBService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * myAcct
 * 2015.5.1 swing版启用
 * 2018.8.16 javaFX改写
 */
public class Main extends Application {
    public static String workpath = System.getProperty("user.dir");

    DBService dbService;

    @Override
    public void start(Stage primaryStage) throws Exception {

        appinit();

        System.out.println("工作目录：" + workpath);
        URL urlFXML = new File(workpath + "/resource/myAcct.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(urlFXML);
        primaryStage.setTitle("myAcct 2015 -> 2018");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();

        loadData();
    }

    /**
     * 加载缺省资料库内容
     */
    private void loadData() {
        List<TMyAcct> list = dbService.query("");
        for (TMyAcct myAcct : list) {
            System.out.println(myAcct);
        }
    }

    private void appinit() {
        // 初始化数据库
        System.out.println("初始化数据库,加载默认资料库myAcct.db");
        AppContext.dbService = new DBService();
        AppContext.dbService.setDbFile("D:/mySCM/gitRepo/myAcct/db/myAcct.db");
        AppContext.dbService.connect();
        dbService = AppContext.dbService;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
