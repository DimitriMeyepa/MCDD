package com.example.mccidanceclub.controller;

import com.example.mccidanceclub.dao.EventsDAO;
import com.example.mccidanceclub.entities.Events;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class EventsController {

    @FXML private TableView<Events> tableEvents;
    @FXML private TableColumn<Events, Integer> colId;
    @FXML private TableColumn<Events, String> colName;
    @FXML private TableColumn<Events, LocalDate> colDate;

    @FXML private TextField txtName;
    @FXML private DatePicker dpDate;

    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private EventsDAO eventsDAO;
    private ObservableList<Events> eventsList;

    @FXML
    public void initialize() {
        eventsDAO = new EventsDAO();
        setupTable();
        loadEvents();
        setupSelectionListener();
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getEventId()).asObject());
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEventName()));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getEventDate()));
    }

    private void loadEvents() {
        eventsList = FXCollections.observableArrayList(eventsDAO.getAllEvents());
        tableEvents.setItems(eventsList);
    }

    private void setupSelectionListener() {
        tableEvents.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtName.setText(newSelection.getEventName());
                dpDate.setValue(newSelection.getEventDate());
            }
        });
    }

    @FXML
    private void addEvent() {
        Events event = new Events(txtName.getText(), dpDate.getValue());
        if (eventsDAO.addEvent(event)) {
            loadEvents();
            clearFields();
        }
    }

    @FXML
    private void updateEvent() {
        Events selected = tableEvents.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setEventName(txtName.getText());
            selected.setEventDate(dpDate.getValue());
            if (eventsDAO.updateEvent(selected)) {
                loadEvents();
                clearFields();
            }
        }
    }

    @FXML
    private void deleteEvent() {
        Events selected = tableEvents.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (eventsDAO.deleteEvent(selected.getEventId())) {
                loadEvents();
                clearFields();
            }
        }
    }

    private void clearFields() {
        txtName.clear();
        dpDate.setValue(null);
        tableEvents.getSelectionModel().clearSelection();
    }
}
