package com.macrolab.myAcct;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        File mainFXML = new File("D:\\mySCM\\gitRepo\\myAcct\\myAcct.fxml");
//        Parent root = FXMLLoader.load(getClass().getResource(mainFXML.getAbsolutePath()));
        Parent root = FXMLLoader.load(getClass().getResource("myAcct.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
