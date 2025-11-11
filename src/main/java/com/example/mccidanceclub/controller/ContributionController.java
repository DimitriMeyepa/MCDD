package com.example.mccidanceclub.controller;

import com.example.mccidanceclub.dao.ContributionDAO;
import com.example.mccidanceclub.dao.MembersDAO;
import com.example.mccidanceclub.entities.Contribution;
import com.example.mccidanceclub.entities.Members;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

public class ContributionController {

    @FXML private ComboBox<Members> cmbMembers;
    @FXML private TextField txtAmount;
    @FXML private TableView<Contribution> tableContributions;
    @FXML private TableColumn<Contribution, String> colMemberName;
    @FXML private TableColumn<Contribution, Double> colAmount;
    @FXML private TableColumn<Contribution, Double> colBalance;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    // Nouveaux labels pour le solde total
    @FXML private Label lblTotalAmount;
    @FXML private Label lblTotalBalance;

    private MembersDAO membersDAO;
    private ContributionDAO contributionDAO;
    private ObservableList<Members> membersList;
    private ObservableList<Contribution> contributionList;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur des contributions...");
        try {
            membersDAO = new MembersDAO();
            contributionDAO = new ContributionDAO();

            loadMembers();
            setupTable();
            loadContributions();
            setupSelectionListener();
            updateTotalBalance(); // Mettre à jour le solde total au démarrage

            System.out.println("Initialisation terminée avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void loadMembers() {
        try {
            membersList = FXCollections.observableArrayList(membersDAO.getAllMembers());
            cmbMembers.setItems(membersList);

            // Configuration de l'affichage dans la liste déroulante
            cmbMembers.setCellFactory(param -> new ListCell<Members>() {
                @Override
                protected void updateItem(Members member, boolean empty) {
                    super.updateItem(member, empty);
                    if (empty || member == null) {
                        setText(null);
                    } else {
                        setText(member.getFirstName() + " " + member.getLastName());
                    }
                }
            });

            // Configuration de l'affichage dans le bouton de la ComboBox
            cmbMembers.setConverter(new StringConverter<Members>() {
                @Override
                public String toString(Members member) {
                    if (member != null) {
                        return member.getFirstName() + " " + member.getLastName();
                    } else {
                        return "";
                    }
                }

                @Override
                public Members fromString(String string) {
                    return membersList.stream()
                            .filter(member -> (member.getFirstName() + " " + member.getLastName()).equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });

            System.out.println("Chargement des membres terminé: " + membersList.size() + " membres trouvés");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des membres: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des membres: " + e.getMessage());
        }
    }

    private void setupTable() {
        try {
            // Configuration de la colonne nom du membre
            colMemberName.setCellValueFactory(data -> {
                if (data.getValue() != null && data.getValue().getMember() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                            data.getValue().getMember().getFirstName() + " " +
                                    data.getValue().getMember().getLastName()
                    );
                } else {
                    return new javafx.beans.property.SimpleStringProperty("Membre inconnu");
                }
            });

            // Configuration de la colonne montant
            colAmount.setCellValueFactory(data -> {
                if (data.getValue() != null) {
                    return new javafx.beans.property.SimpleDoubleProperty(data.getValue().getAmount()).asObject();
                } else {
                    return new javafx.beans.property.SimpleDoubleProperty(0.0).asObject();
                }
            });

            // Configuration de la colonne solde
            colBalance.setCellValueFactory(data -> {
                if (data.getValue() != null) {
                    return new javafx.beans.property.SimpleDoubleProperty(data.getValue().getBalance()).asObject();
                } else {
                    return new javafx.beans.property.SimpleDoubleProperty(0.0).asObject();
                }
            });

            System.out.println("Configuration du tableau terminée");
        } catch (Exception e) {
            System.err.println("Erreur lors de la configuration du tableau: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la configuration du tableau: " + e.getMessage());
        }
    }

    private void loadContributions() {
        try {
            contributionList = FXCollections.observableArrayList(contributionDAO.getAllContributions());
            tableContributions.setItems(contributionList);
            updateTotalBalance(); // Mettre à jour le solde total après le chargement
            System.out.println("Chargement des contributions terminé: " + contributionList.size() + " contributions trouvées");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des contributions: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des contributions: " + e.getMessage());
        }
    }

    private void setupSelectionListener() {
        tableContributions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    System.out.println("Contribution sélectionnée: " + newSelection.getContributionId());

                    // Trouver le membre correspondant dans la liste
                    Members correspondingMember = membersList.stream()
                            .filter(m -> m.getMemberId() == newSelection.getMember().getMemberId())
                            .findFirst()
                            .orElse(null);

                    if (correspondingMember != null) {
                        cmbMembers.setValue(correspondingMember);
                        txtAmount.setText(String.valueOf(newSelection.getAmount()));

                        // Activer les boutons de modification et suppression
                        btnUpdate.setDisable(false);
                        btnDelete.setDisable(false);
                    } else {
                        System.err.println("Membre non trouvé pour la contribution sélectionnée");
                        showAlert("Erreur", "Membre non trouvé pour la contribution sélectionnée");
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la sélection: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // Désactiver les boutons de modification et suppression quand aucune sélection
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
            }
        });
    }

    // Méthode pour calculer et mettre à jour le solde total
    private void updateTotalBalance() {
        try {
            double totalAmount = 0.0;
            double totalBalance = 0.0;

            if (contributionList != null && !contributionList.isEmpty()) {
                for (Contribution contribution : contributionList) {
                    totalAmount += contribution.getAmount();
                    totalBalance += contribution.getBalance();
                }
            }

            // Mettre à jour les labels avec formatage
            lblTotalAmount.setText(String.format("%,.2f €", totalAmount));
            lblTotalBalance.setText(String.format("%,.2f €", totalBalance));

            System.out.println("Solde total mis à jour - Montant total: " + totalAmount + ", Solde général: " + totalBalance);
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du solde total: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addContribution() {
        try {
            Members selectedMember = cmbMembers.getValue();
            if (selectedMember == null) {
                showAlert("Erreur", "Veuillez sélectionner un membre.");
                return;
            }

            if (txtAmount.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez entrer un montant.");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(txtAmount.getText());
                if (amount <= 0) {
                    showAlert("Erreur", "Le montant doit être positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Veuillez entrer un montant valide.");
                return;
            }

            Contribution contribution = new Contribution(selectedMember, amount);
            boolean success = contributionDAO.addContribution(contribution);

            if (success) {
                System.out.println("Contribution ajoutée avec succès");
                clearFields();
                loadContributions(); // Cette méthode appelle updateTotalBalance()
                showSuccessAlert("Succès", "Contribution ajoutée avec succès.");
            } else {
                showAlert("Erreur", "Impossible d'ajouter la contribution.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de la contribution: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout de la contribution: " + e.getMessage());
        }
    }

    @FXML
    private void updateContribution() {
        try {
            Contribution selected = tableContributions.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Erreur", "Veuillez sélectionner une contribution à modifier.");
                return;
            }

            Members selectedMember = cmbMembers.getValue();
            if (selectedMember == null) {
                showAlert("Erreur", "Veuillez sélectionner un membre.");
                return;
            }

            if (txtAmount.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez entrer un montant.");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(txtAmount.getText());
                if (amount <= 0) {
                    showAlert("Erreur", "Le montant doit être positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Veuillez entrer un montant valide.");
                return;
            }

            selected.setMember(selectedMember);
            selected.setAmount(amount);

            boolean success = contributionDAO.updateContribution(selected);
            if (success) {
                System.out.println("Contribution modifiée avec succès");
                clearFields();
                loadContributions(); // Cette méthode appelle updateTotalBalance()
                showSuccessAlert("Succès", "Contribution modifiée avec succès.");
            } else {
                showAlert("Erreur", "Impossible de modifier la contribution.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification de la contribution: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la modification de la contribution: " + e.getMessage());
        }
    }

    @FXML
    private void deleteContribution() {
        try {
            Contribution selected = tableContributions.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Erreur", "Veuillez sélectionner une contribution à supprimer.");
                return;
            }

            // Confirmation avant suppression
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer la contribution");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette contribution ?");

            ButtonType result = confirmation.showAndWait().orElse(ButtonType.CANCEL);

            if (result == ButtonType.OK) {
                boolean success = contributionDAO.deleteContribution(selected.getContributionId());
                if (success) {
                    System.out.println("Contribution supprimée avec succès");
                    clearFields();
                    loadContributions(); // Cette méthode appelle updateTotalBalance()
                    showSuccessAlert("Succès", "Contribution supprimée avec succès.");
                } else {
                    showAlert("Erreur", "Impossible de supprimer la contribution.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de la contribution: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression de la contribution: " + e.getMessage());
        }
    }

    private void clearFields() {
        cmbMembers.getSelectionModel().clearSelection();
        txtAmount.clear();
        tableContributions.getSelectionModel().clearSelection();

        // Réactiver le bouton d'ajout, désactiver modification/suppression
        btnAdd.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}