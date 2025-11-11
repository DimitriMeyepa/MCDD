package com.example.mccidanceclub.controller;

import com.example.mccidanceclub.dao.MembersDAO;
import com.example.mccidanceclub.entities.Members;
import com.example.mccidanceclub.utils.EmailSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;

public class MembersController {

    @FXML private TableView<Members> tableMembers;
    @FXML private TableColumn<Members, Integer> colId;
    @FXML private TableColumn<Members, String> colFirstName;
    @FXML private TableColumn<Members, String> colLastName;
    @FXML private TableColumn<Members, String> colEmail;
    @FXML private TableColumn<Members, String> colPhone;
    @FXML private TableColumn<Members, String> colClass;

    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtClass;

    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private MembersDAO membersDAO;
    private ObservableList<Members> membersList;

    @FXML
    public void initialize() {
        membersDAO = new MembersDAO();
        setupTable();
        loadMembers();
        setupSelectionListener();
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getMemberId()).asObject());
        colFirstName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        colClass.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMemberClass()));
    }

    private void loadMembers() {
        membersList = FXCollections.observableArrayList(membersDAO.getAllMembers());
        tableMembers.setItems(membersList);
    }

    private void setupSelectionListener() {
        tableMembers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtFirstName.setText(newSelection.getFirstName());
                txtLastName.setText(newSelection.getLastName());
                txtEmail.setText(newSelection.getEmail());
                txtPhone.setText(newSelection.getPhone());
                txtClass.setText(newSelection.getMemberClass());
            }
        });
    }

    @FXML
    private void addMember() {
        Members member = new Members(
                txtFirstName.getText(),
                txtLastName.getText(),
                txtEmail.getText(),
                txtPhone.getText(),
                txtClass.getText()
        );

        if (membersDAO.addMember(member)) {
            loadMembers();
            clearFields();

            // --- ENVOI EMAIL AVEC PDF ---
            try {
                // Chemin vers le PDF dans resources
                File pdfFile = new File(getClass().getResource("/com/example/mccidanceclub/pdf/charte_club.pdf").toURI());
                EmailSender.sendRegistrationEmail(member.getEmail(), member.getFirstName(), pdfFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateMember() {
        Members selected = tableMembers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setFirstName(txtFirstName.getText());
            selected.setLastName(txtLastName.getText());
            selected.setEmail(txtEmail.getText());
            selected.setPhone(txtPhone.getText());
            selected.setMemberClass(txtClass.getText());

            if (membersDAO.updateMember(selected)) {
                loadMembers();
                clearFields();
            }
        }
    }

    @FXML
    private void deleteMember() {
        Members selected = tableMembers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (membersDAO.deleteMember(selected.getMemberId())) {
                loadMembers();
                clearFields();
            }
        }
    }

    private void clearFields() {
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtClass.clear();
        tableMembers.getSelectionModel().clearSelection();
    }
}
