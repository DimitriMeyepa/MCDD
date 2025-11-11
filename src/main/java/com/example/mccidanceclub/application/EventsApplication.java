package com.example.mccidanceclub.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EventsApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Charger le FXML de la fenêtre Events
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mccidanceclub/events.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Gestion des Événements - MCCI Dance Club");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
