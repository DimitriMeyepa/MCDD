package com.example.mccidanceclub.controller;

import com.example.mccidanceclub.dao.AdminDAO;
import com.example.mccidanceclub.entities.Admin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController {

    // --- LOGIN ---
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;

    private AdminDAO adminDAO;

    // --- CRUD Admins ---
    @FXML private TableView<Admin> tableAdmins;
    @FXML private TableColumn<Admin, Integer> colId;
    @FXML private TableColumn<Admin, String> colUsername;
    @FXML private TableColumn<Admin, String> colPassword;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;

    private ObservableList<Admin> adminList;
    private boolean isUpdating = false;

    @FXML
    public void initialize() {
        adminDAO = new AdminDAO();

        // Si CRUD visible, on configure la table
        if (tableAdmins != null) {
            setupTable();
            loadAdmins();
            setupSelectionListener();
            updateButtonStates();
        }
    }

    // --- LOGIN ---
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez remplir tous les champs", "error");
            return;
        }

        if (login(username, password)) {
            showMessage("✅ Connexion réussie !", "success");
            // Ici, vous pouvez changer de scène vers AdminApplication
            // ex: App.showAdminDashboard();
        } else {
            showMessage("❌ Nom d'utilisateur ou mot de passe incorrect !", "error");
        }
    }

    // Méthode publique pour le LoginApplication
    public boolean login(String username, String password) {
        Admin admin = adminDAO.login(username, password);
        return admin != null;
    }

    // --- CRUD ---
    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idAdmin"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Masquer le mot de passe dans la table (afficher seulement des ***)
        colPassword.setCellFactory(column -> new TableCell<Admin, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("••••••••");
                }
            }
        });
    }

    private void loadAdmins() {
        adminList = FXCollections.observableArrayList(adminDAO.getAllAdmins());
        tableAdmins.setItems(adminList);
    }

    private void setupSelectionListener() {
        tableAdmins.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                isUpdating = true;
                txtUsername.setText(newSel.getUsername());
                // Ne pas afficher le mot de passe hashé dans le champ password
                txtPassword.clear();
                updateButtonStates();
            }
        });
    }

    private void updateButtonStates() {
        boolean hasSelection = tableAdmins.getSelectionModel().getSelectedItem() != null;
        btnUpdate.setDisable(!hasSelection);
        btnDelete.setDisable(!hasSelection);
        btnAdd.setDisable(hasSelection && !isUpdating);
    }

    @FXML
    private void addAdmin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (!validateInputs(username, password)) {
            return;
        }

        if (adminDAO.usernameExists(username)) {
            showAlert("Erreur", "Ce nom d'utilisateur existe déjà !");
            return;
        }

        Admin admin = new Admin(username, password);
        if (adminDAO.addAdmin(admin)) {
            showMessage("Admin ajouté avec succès !", "success");
            loadAdmins();
            clearFields();
        } else {
            showAlert("Erreur", "Erreur lors de l'ajout de l'admin");
        }
    }

    @FXML
    private void updateAdmin() {
        Admin selected = tableAdmins.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un admin à modifier");
            return;
        }

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty()) {
            showAlert("Erreur", "Le nom d'utilisateur est obligatoire");
            return;
        }

        // Vérifier si le username existe déjà pour un autre admin
        if (adminDAO.usernameExistsForOtherAdmin(username, selected.getIdAdmin())) {
            showAlert("Erreur", "Ce nom d'utilisateur est déjà utilisé par un autre admin");
            return;
        }

        // Mettre à jour l'admin
        selected.setUsername(username);

        // Si un nouveau mot de passe est saisi, on le met à jour
        if (!password.isEmpty()) {
            if (!isValidPassword(password)) {
                showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères");
                return;
            }
            selected.setPassword(password); // Le DAO le hash automatiquement
        } else {
            // Si aucun nouveau mot de passe n'est saisi, on garde l'ancien
            selected.setPassword(null);
        }

        if (adminDAO.updateAdmin(selected)) {
            showMessage("Admin modifié avec succès !", "success");
            loadAdmins();
            clearFields();
        } else {
            showAlert("Erreur", "Erreur lors de la modification de l'admin");
        }
    }

    @FXML
    private void deleteAdmin() {
        Admin selected = tableAdmins.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un admin à supprimer");
            return;
        }

        // Empêcher la suppression du dernier admin
        if (adminDAO.countAdmins() <= 1) {
            showAlert("Erreur", "Impossible de supprimer le dernier administrateur !");
            return;
        }

        // Confirmation de suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'administrateur");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer l'admin : " + selected.getUsername() + " ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (adminDAO.deleteAdmin(selected.getIdAdmin())) {
                showMessage("Admin supprimé avec succès !", "success");
                loadAdmins();
                clearFields();
            } else {
                showAlert("Erreur", "Erreur lors de la suppression de l'admin");
            }
        }
    }

    @FXML
    private void clearFields() {
        txtUsername.clear();
        txtPassword.clear();
        tableAdmins.getSelectionModel().clearSelection();
        isUpdating = false;
        updateButtonStates();
    }

    // --- Validation methods ---
    private boolean validateInputs(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return false;
        }

        if (username.length() < 3) {
            showAlert("Erreur", "Le nom d'utilisateur doit contenir au moins 3 caractères");
            return false;
        }

        if (!isValidPassword(password)) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères");
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    // --- Utility methods ---
    private void showMessage(String message, String type) {
        if (lblMessage != null) {
            lblMessage.setText(message);
            if ("success".equals(type)) {
                lblMessage.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                lblMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour initialiser un admin par défaut (à apputer une fois)
    public void initializeDefaultAdmin() {
        if (adminDAO.getAllAdmins().isEmpty()) {
            Admin defaultAdmin = new Admin("admin", "admin123");
            if (adminDAO.addAdmin(defaultAdmin)) {
                System.out.println("Admin par défaut créé : admin / admin123");
            }
        }
    }
}