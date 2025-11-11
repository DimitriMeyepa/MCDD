package com.example.mccidanceclub.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MembersApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/mccidanceclub/members.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MCCI Dance Club - Gestion des Membres");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
