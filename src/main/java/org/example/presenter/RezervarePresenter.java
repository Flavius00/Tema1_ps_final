package org.example.presenter;

import org.example.model.dao.RezervareDAO;
import org.example.model.entities.Rezervare;
import org.example.view.MainView;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.event.ListSelectionEvent;

public class RezervarePresenter {
    private  RezervareGUI view;
    private RezervareDAO rezervareDAO;
    private MainView mainView;
    // Format de dată modificat - fără oră
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int currentCameraId = -1;
    private String currentCameraInfo = "";
    private int currentRezervareId = -1;

    public RezervarePresenter(RezervareGUI view, RezervareDAO rezervareDAO, MainView mainView) {
        this.view = view;
        this.rezervareDAO = rezervareDAO;
        this.mainView = mainView;

        // Set up listeners
        view.addAddButtonListener(this::addRezervare);
        view.addEditButtonListener(this::editRezervare);
        view.addDeleteButtonListener(this::deleteRezervare);
        view.addBackButtonListener(this::backToCameras);

        // Add table selection listener
        view.addTableSelectionListener(this::handleTableSelection);

        // Set initial button state
        updateButtonState(false);
    }

    private void handleTableSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = view.getSelectedRow();
            if (selectedRow != -1) {
                // Get rezervare ID and populate form
                currentRezervareId = (int) view.getValueAt(selectedRow, 0);

                // Enable edit and delete buttons
                updateButtonState(true);

                // Populate form fields
                Rezervare rezervare = rezervareDAO.findById(currentRezervareId);
                if (rezervare != null) {
                    // Convertim LocalDateTime la LocalDate
                    LocalDate startDate = rezervare.getStartDate().toLocalDate();
                    LocalDate endDate = rezervare.getEndDate().toLocalDate();

                    view.setStartDate(startDate.format(dateFormatter));
                    view.setEndDate(endDate.format(dateFormatter));
                    view.setNumeClient(rezervare.getNumeClient());
                    view.setPrenumeClient(rezervare.getPrenumeClient());
                    view.setTelefonClient(rezervare.getTelefonClient());
                    view.setEmailClient(rezervare.getEmailClient());
                }
            } else {
                updateButtonState(false);
                currentRezervareId = -1;
            }
        }
    }

    private void updateButtonState(boolean rowSelected) {
        view.setButtonsEnabled(true, rowSelected, rowSelected);
    }

    public void setCurrentCamera(int cameraId, String cameraInfo) {
        this.currentCameraId = cameraId;
        this.currentCameraInfo = cameraInfo;
        view.setCameraInfo(cameraInfo);
        loadRezervari();

        // Reset selection
        currentRezervareId = -1;
        view.clearForm();
        updateButtonState(false);

        // Populăm formul cu valori implicite
        view.setStartDate(LocalDate.now().format(dateFormatter));
        view.setEndDate(LocalDate.now().plusDays(1).format(dateFormatter));
    }

    private void loadRezervari() {
        if (currentCameraId > 0) {
            Object[][] rezervari = rezervareDAO.getRezervariAsObjects(currentCameraId);
            view.populateRezervareTable(rezervari);
        }
    }

    private void addRezervare(ActionEvent e) {
        // Clear form for new reservation
        if (validateAndSaveRezervare()) {
            loadRezervari();
        }
        view.clearForm();
        currentRezervareId = -1;

        // Setăm valori implicite pentru datele de început și sfârșit
        view.setStartDate(LocalDate.now().format(dateFormatter));
        view.setEndDate(LocalDate.now().plusDays(1).format(dateFormatter));

    }

    private void editRezervare(ActionEvent e) {
        int selectedRow = view.getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Selectați o rezervare pentru a o modifica!");
            return;
        }

        // Form is already populated by selection handler
        if (validateAndSaveRezervare()) {
            loadRezervari();
        }
    }

    private boolean validateAndSaveRezervare() {
        // Get all form data
        String startDateStr = view.getStartDate();
        String endDateStr = view.getEndDate();
        String numeClient = view.getNumeClient();
        String prenumeClient = view.getPrenumeClient();
        String telefonClient = view.getTelefonClient();
        String emailClient = view.getEmailClient();

        // Validate required fields
        if (startDateStr == null || startDateStr.trim().isEmpty()) {
            view.showMessage("Data de început nu poate fi goală!");
            return false;
        }

        if (endDateStr == null || endDateStr.trim().isEmpty()) {
            view.showMessage("Data de sfârșit nu poate fi goală!");
            return false;
        }

        if (numeClient == null || numeClient.trim().isEmpty()) {
            view.showMessage("Numele clientului nu poate fi gol!");
            return false;
        }

        if (telefonClient == null || telefonClient.trim().isEmpty()) {
            view.showMessage("Telefonul clientului nu poate fi gol!");
            return false;
        }

        // Ne asigurăm că prenumeClient și emailClient nu sunt null
        prenumeClient = (prenumeClient != null) ? prenumeClient : "";
        emailClient = (emailClient != null) ? emailClient : "";

        // Parse and validate dates
        LocalDateTime startDate, endDate;
        try {
            // Convertim din LocalDate în LocalDateTime pentru a folosi DAO-urile existente
            LocalDate startLocalDate = LocalDate.parse(startDateStr, dateFormatter);
            LocalDate endLocalDate = LocalDate.parse(endDateStr, dateFormatter);

            // Transformăm în LocalDateTime (începutul zilei)
            startDate = startLocalDate.atStartOfDay();
            endDate = endLocalDate.atStartOfDay();

            if (endDate.isBefore(startDate)) {
                view.showMessage("Data de sfârșit nu poate fi înainte de data de început!");
                return false;
            }

            if (startDate.isBefore(LocalDateTime.now())) {
                view.showMessage("Data de început nu poate fi în trecut!");
                return false;
            }
        } catch (DateTimeParseException ex) {
            view.showMessage("Format dată invalid! Folosiți formatul yyyy-MM-dd.");
            return false;
        }

        // Check for availability (only when adding or changing dates)
        boolean needToCheckAvailability = currentRezervareId == -1;
        if (currentRezervareId != -1) {
            // For existing reservation, get original dates
            Rezervare existingRezervare = rezervareDAO.findById(currentRezervareId);
            if (existingRezervare != null) {
                // Only check availability if dates changed
                if (!startDate.toLocalDate().equals(existingRezervare.getStartDate().toLocalDate()) ||
                        !endDate.toLocalDate().equals(existingRezervare.getEndDate().toLocalDate())) {
                    needToCheckAvailability = true;
                }
            }
        }

        if (needToCheckAvailability) {
            // Check if room is available for these dates
            boolean isAvailable = rezervareDAO.isCameraDisponibila(currentCameraId, startDate, endDate);
            if (!isAvailable) {
                view.showMessage("Camera nu este disponibilă în perioada selectată!");
                return false;
            }
        }

        // Save or update reservation
        boolean success;
        if (currentRezervareId == -1) {
            // Add new reservation
            success = rezervareDAO.save(startDateStr, endDateStr, currentCameraId,
                    numeClient, prenumeClient, telefonClient, emailClient);
            if (success) {
                view.showMessage("Rezervare adăugată cu succes!");
                view.clearForm();
                // Resetăm datele implicite
                view.setStartDate(LocalDate.now().format(dateFormatter));
                view.setEndDate(LocalDate.now().plusDays(1).format(dateFormatter));
            } else {
                view.showMessage("Eroare la adăugarea rezervării!");
            }
        } else {
            // Update existing reservation
            success = rezervareDAO.update(currentRezervareId, startDateStr, endDateStr, currentCameraId,
                    numeClient, prenumeClient, telefonClient, emailClient);
            if (success) {
                view.showMessage("Rezervare actualizată cu succes!");
                view.clearForm();
                currentRezervareId = -1;
                // Resetăm datele implicite
                view.setStartDate(LocalDate.now().format(dateFormatter));
                view.setEndDate(LocalDate.now().plusDays(1).format(dateFormatter));
            } else {
                view.showMessage("Eroare la actualizarea rezervării!");
            }
        }

        return success;
    }

    private void deleteRezervare(ActionEvent e) {
        int selectedRow = view.getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Selectați o rezervare pentru a o șterge!");
            return;
        }

        int rezervareId = (int) view.getValueAt(selectedRow, 0);

        int confirmResult = view.showConfirmDialog("Sigur doriți să ștergeți această rezervare?");
        if (confirmResult == 0) { // Yes option
            boolean success = rezervareDAO.delete(rezervareId);
            if (success) {
                view.showMessage("Rezervare ștearsă cu succes!");
                view.clearForm();
                loadRezervari();
                currentRezervareId = -1;
                updateButtonState(false);
                // Resetăm datele implicite
                view.setStartDate(LocalDate.now().format(dateFormatter));
                view.setEndDate(LocalDate.now().plusDays(1).format(dateFormatter));
            } else {
                view.showMessage("Eroare la ștergerea rezervării!");
            }
        }
    }

    private void backToCameras(ActionEvent e) {
        mainView.showPanel("camera");
        currentRezervareId = -1;
    }
}