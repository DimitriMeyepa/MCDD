package com.example.mccidanceclub.controller;

import com.example.mccidanceclub.dao.AdminDAO;
import com.example.mccidanceclub.entities.Admin;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AdminController {

    // --- LOGIN ---
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;
    @FXML private Button btnLogin;

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

        // Configurer les effets pour le login
        if (btnLogin != null) {
            setupLoginEffects();
            setupTooltips();
        }

        // Si CRUD visible, on configure la table
        if (tableAdmins != null) {
            setupTable();
            loadAdmins();
            setupSelectionListener();
            updateButtonStates();
        }
    }

    // --- Configuration des effets pour le login ---
    private void setupLoginEffects() {
        // Effet de survol pour le bouton de connexion
        btnLogin.setOnMouseEntered(e -> {
            btnLogin.setStyle("-fx-background-color: linear-gradient(to right, #5a6fdb, #6a4790); -fx-background-radius: 15; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-cursor: hand;");
            btnLogin.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(90, 111, 219, 0.6), 15, 0, 0, 5));
        });

        btnLogin.setOnMouseExited(e -> {
            btnLogin.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-background-radius: 15; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-cursor: hand;");
            btnLogin.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(102, 126, 234, 0.4), 10, 0, 0, 0));
        });

        // Effet de clic pour le bouton
        btnLogin.setOnMousePressed(e -> {
            btnLogin.setStyle("-fx-background-color: linear-gradient(to right, #4a5fcf, #5a3a7a); -fx-background-radius: 15; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-cursor: hand;");
        });

        btnLogin.setOnMouseReleased(e -> {
            btnLogin.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-background-radius: 15; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-cursor: hand;");
        });

        // Effets pour les champs de texte
        setupFieldEffects(txtUsername);
        setupFieldEffects(txtPassword);
    }

    private void setupFieldEffects(TextField field) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #667eea; -fx-border-radius: 10; -fx-border-width: 2; -fx-padding: 12; -fx-font-size: 14;");
            } else {
                field.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-padding: 12; -fx-font-size: 14;");
            }
        });
    }

    private void setupTooltips() {
        Tooltip usernameTooltip = new Tooltip("Entrez votre nom d'utilisateur administrateur");
        usernameTooltip.setStyle("-fx-background-color: #2d3748; -fx-text-fill: white; -fx-font-size: 12;");
        txtUsername.setTooltip(usernameTooltip);

        Tooltip passwordTooltip = new Tooltip("Entrez votre mot de passe administrateur");
        passwordTooltip.setStyle("-fx-background-color: #2d3748; -fx-text-fill: white; -fx-font-size: 12;");
        txtPassword.setTooltip(passwordTooltip);
    }

    // --- LOGIN ---
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez remplir tous les champs", "error");
            shakeAnimation(txtUsername);
            shakeAnimation(txtPassword);
            return;
        }

        // Animation de chargement
        btnLogin.setText("Connexion...");
        btnLogin.setDisable(true);

        // Simuler un délai de connexion (à retirer en production)
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            if (login(username, password)) {
                showMessage("✅ Connexion réussie ! Redirection...", "success");
                animateLogin(true);
            } else {
                showMessage("❌ Identifiants incorrects", "error");
                btnLogin.setText("Se connecter");
                btnLogin.setDisable(false);
                shakeAnimation(btnLogin);
            }
        });
        pause.play();
    }

    // Animation de connexion réussie
    private void animateLogin(boolean success) {
        if (success) {
            // Animation de succès
            btnLogin.setText("✓ Connexion réussie");
            btnLogin.setStyle("-fx-background-color: #48bb78; -fx-background-radius: 15; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");

            // Transition vers la prochaine vue après un délai
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> {
                // Charger la vue admin principale
                // App.showAdminDashboard();
                System.out.println("Redirection vers le dashboard admin...");
            });
            pause.play();
        }
    }

    // Animation de secousse pour les erreurs
    private void shakeAnimation(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
    }

    // Méthode publique pour le LoginApplication
    public boolean login(String username, String password) {
        Admin admin = adminDAO.login(username, password);
        return admin != null;
    }

    // Gestion des effets pour le lien mot de passe oublié
    @FXML
    private void handleForgotPasswordHover() {
        // Cet effet sera géré par le CSS inline dans le FXML
    }

    @FXML
    private void handleForgotPasswordExit() {
        // Cet effet sera géré par le CSS inline dans le FXML
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

        // Style moderne pour la table
        tableAdmins.setStyle("-fx-font-size: 14px; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
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

        // Style moderne pour l'alerte
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 14px; -fx-border-color: #e2e8f0;");

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
                lblMessage.setStyle("-fx-text-fill: #48bb78; -fx-font-weight: bold; -fx-font-size: 14;");
            } else {
                lblMessage.setStyle("-fx-text-fill: #e53e3e; -fx-font-weight: bold; -fx-font-size: 14;");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style moderne pour les alertes
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 14px; -fx-border-color: #e2e8f0; -fx-background-color: white;");

        alert.showAndWait();
    }

    // Méthode pour initialiser un admin par défaut (à appeler une fois)
    public void initializeDefaultAdmin() {
        if (adminDAO.getAllAdmins().isEmpty()) {
            Admin defaultAdmin = new Admin("admin", "admin123");
            if (adminDAO.addAdmin(defaultAdmin)) {
                System.out.println("Admin par défaut créé : admin / admin123");
            }
        }
    }
}