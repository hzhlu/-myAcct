package com.macrolab.myAcct;

import com.macrolab.myAcct.common.AppContext;
import com.macrolab.myAcct.controller.MainController;
import com.macrolab.myAcct.service.DBService;
import com.macrolab.myAcct.service.MyAcctService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * myAcct
 * 2015.5.1 swing版启用
 * 2018.8.16 javaFX改写
 */
public class Main extends Application {
    public static String workpath = System.getProperty("user.dir");

    MyAcctService myAcctService = new MyAcctService();

    private Stage stage;
    private final double MINIMUM_WINDOW_WIDTH = 1200;
    private final double MINIMUM_WINDOW_HEIGHT = 800;
    private String title = "myAcct 2015 -> 2018";


    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("工作目录：" + workpath);

        // 初始化数据源
        appinit();

        // 初始化界面
        stage = primaryStage;
        stage.setTitle(title);
        gotoMain();  // 跳转到主页面
        stage.show();
    }

    /**
     * 跳转主页面
     */
    private void gotoMain() {
        try {
            MainController mainController = (MainController) replaceSceneContent("myAcct.fxml");
            mainController.setApp(this);
            mainController.setMyAcctService(myAcctService);
            mainController.loadList( );              // 加载页面数据
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 页面展现
     *
     * @param fxml 待展现的fxml
     * @return
     * @throws Exception
     */
    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        File fileFXML = new File(workpath + "/resource/" + fxml);
        FileInputStream fis = new FileInputStream(fileFXML);
        InputStream in = fis;
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(fileFXML.toURI().toURL());
        AnchorPane page;
        try {
            page = loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page, 1200, 800);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }

    private void appinit() {
        // 初始化数据库
        System.out.println("初始化数据库,加载默认资料库myAcct.db");
        AppContext.dbService = new DBService();
        AppContext.dbService.setDbFile("D:/mySCM/gitRepo/myAcct/db/myAcct.db");
        AppContext.dbService.connect();

        myAcctService.setDbService(AppContext.dbService);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
