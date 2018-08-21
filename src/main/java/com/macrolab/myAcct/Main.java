package com.macrolab.myAcct;

import com.macrolab.myAcct.controller.MainController;
import com.macrolab.myAcct.service.DBService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * myAcct
 * 2015.5.1 swing版启用
 * 2018.8.16 javaFX改写
 */
public class Main extends Application {
    Logger logger = LoggerFactory.getLogger(Main.class);

    public static String workpath = System.getProperty("user.dir");

    private Stage stage;
    private final double WINDOW_WIDTH = 1245;
    private final double WINDOW_HEIGHT = 770;
    private String title = "myAcct 2015 -> 2018";


    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("工作目录：" + workpath);
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
            mainController.loadDBFile(workpath + "/db"); // 加在工作目录下的数据文件
            mainController.choiceDefaultDBFile();  // 缺省加在第一个库文件
        } catch (Exception ex) {
            logger.error("跳转主页面异常" + ex.getMessage(), ex);
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
        File fileFXML = new File(workpath + "/src/main/resources/" + fxml);
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
        Scene scene = new Scene(page, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
