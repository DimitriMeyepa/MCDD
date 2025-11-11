module com.example.mccidanceclub {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.mccidanceclub to javafx.fxml;
    exports com.example.mccidanceclub.entities;
    opens com.example.mccidanceclub.entities to javafx.fxml;

    requires java.sql;
    requires org.postgresql.jdbc;
    requires jakarta.mail;
    requires jakarta.activation;
    requires jbcrypt;

    // Ouvrir le package des controllers pour FXML
    opens com.example.mccidanceclub.controller to javafx.fxml;

    // Ouvrir le package application pour le FXMLLoader
    opens com.example.mccidanceclub.application to javafx.fxml;


    exports com.example.mccidanceclub.controller;
    exports com.example.mccidanceclub.application;

}