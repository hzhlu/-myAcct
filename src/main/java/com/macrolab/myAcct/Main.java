package com.macrolab.myAcct;

import com.macrolab.myAcct.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public String title = "我的资料库(2018)";


    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("工作目录：" + workpath);
        // 初始化界面
        stage = primaryStage;
        setTitle(title);
        gotoMain();  // 跳转到主页面
        stage.show();
    }

    // 设置应用的 标题栏
    public void setTitle(String title) {
        stage.setTitle(title);
    }

    /**
     * 跳转主页面
     */
    private void gotoMain() {
        try {
            MainController mainController = (MainController) replaceSceneContent("myAcct.fxml");
            mainController.setApp(this);
            mainController.loadDBFile(); // 加在工作目录下的数据文件
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
        Parent rootNode = loader.load(getClass().getResourceAsStream("/fxml/" + fxml));
        Scene scene = new Scene(rootNode, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
