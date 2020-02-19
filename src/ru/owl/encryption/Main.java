package ru.owl.encryption;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Main extends Application {
    private static Stage stage;
    public static Window getMainStage(){
        return stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/Main.fxml"));
        primaryStage.setTitle("Encrypter");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        stage = primaryStage;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
