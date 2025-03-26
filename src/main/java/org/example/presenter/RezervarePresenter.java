package org.example.presenter;

import org.example.model.dao.RezervareDAO;
import org.example.model.entities.Rezervare;
import org.example.view.MainView;
import org.example.view.RezervareGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import java.util.HashMap;
import java.util.Map;

public class RezervarePresenter {
    private RezervareGUI view;
    private RezervareDAO rezervareDAO;
    private MainView mainView;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int currentCameraId = -1;
    private String currentCameraInfo = "";
    private int currentRezervareId = -1;

    public RezervarePresenter(RezervareGUI view, RezervareDAO rezervareDAO, MainView mainView) {
        this.view = view;
        this.rezervareDAO = rezervareDAO;
        this.mainView = mainView;

        Map<String, ActionListener> listeners = new HashMap<>();
        listeners.put("add", this::addRezervare);
        listeners.put("edit", this::editRezervare);
        listeners.put("delete", this::deleteRezervare);
        listeners.put("back", this::backToCameras);

        view.registerEventHandlers(this::handleTableSelection, listeners);

        updateButtonState(false);
    }

    private int showDialog(int dialogType, String message, Object initialValue) {
        switch (dialogType) {
            case JOptionPane.QUESTION_MESSAGE:
                view.displayDialog(dialogType, message, initialValue);
                return JOptionPane.YES_OPTION;

            case JOptionPane.PLAIN_MESSAGE:
                if (initialValue != null) {
                    view.displayDialog(dialogType, message, initialValue);
                    return 0;
                }
                break;

            default:
                view.displayDialog(dialogType, message, null);
                return -1;
        }
        return -1;
    }

    private void handleTableSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Object[] rowData = view.getTableSelection();
            if (rowData != null) {
                currentRezervareId = (int) rowData[0];

                updateButtonState(true);

                Rezervare rezervare = rezervareDAO.findById(currentRezervareId);
                if (rezervare != null) {
                    LocalDate startDate = rezervare.getStartDate().toLocalDate();
                    LocalDate endDate = rezervare.getEndDate().toLocalDate();

                    view.setFormData(new Object[] {
                            startDate.format(dateFormatter),
                            endDate.format(dateFormatter),
                            rezervare.getNumeClient(),
                            rezervare.getPrenumeClient(),
                            rezervare.getTelefonClient(),
                            rezervare.getEmailClient(),
                            false
                    });
                }
            } else {
                updateButtonState(false);
                currentRezervareId = -1;
            }
        }
    }

    private void updateButtonState(boolean rowSelected) {
        boolean[] buttonStates = {
                true,
                rowSelected,
                rowSelected
        };
        view.updateComponents(null, null, buttonStates);
    }

    public void setCurrentCamera(int cameraId, String cameraInfo) {
        this.currentCameraId = cameraId;
        this.currentCameraInfo = cameraInfo;
        view.updateComponents(cameraInfo, null, null);
        loadRezervari();

        currentRezervareId = -1;

        LocalDate today = LocalDate.now();
        view.setFormData(new Object[] {
                today.format(dateFormatter),
                today.plusDays(1).format(dateFormatter),
                "",
                "",
                "",
                "",
                true
        });

        updateButtonState(false);
    }

    private void loadRezervari() {
        if (currentCameraId > 0) {
            Object[][] rezervari = rezervareDAO.getRezervariAsObjects(currentCameraId);
            view.updateComponents(null, rezervari, null);
        }
    }

    private void addRezervare(ActionEvent e) {
        if (validateAndSaveRezervare()) {
            loadRezervari();
        }

        LocalDate today = LocalDate.now();
        view.setFormData(new Object[] {
                today.format(dateFormatter),
                today.plusDays(1).format(dateFormatter),
                "",
                "",
                "",
                "",
                true
        });

        currentRezervareId = -1;
    }

    private void editRezervare(ActionEvent e) {
        Object[] rowData = view.getTableSelection();
        if (rowData == null) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Selectați o rezervare pentru a o modifica!", null);
            return;
        }

        if (validateAndSaveRezervare()) {
            loadRezervari();
        }
    }

    private boolean validateAndSaveRezervare() {
        Object[] formValues = view.getFormData();
        if (formValues.length < 6) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Date de formular incomplete!", null);
            return false;
        }

        String startDateStr = (String) formValues[0];
        String endDateStr = (String) formValues[1];
        String numeClient = (String) formValues[2];
        String prenumeClient = (String) formValues[3];
        String telefonClient = (String) formValues[4];
        String emailClient = (String) formValues[5];

        if (startDateStr == null || startDateStr.trim().isEmpty()) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Data de început nu poate fi goală!", null);
            return false;
        }

        if (endDateStr == null || endDateStr.trim().isEmpty()) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Data de sfârșit nu poate fi goală!", null);
            return false;
        }

        if (numeClient == null || numeClient.trim().isEmpty()) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Numele clientului nu poate fi gol!", null);
            return false;
        }

        if (telefonClient == null || telefonClient.trim().isEmpty()) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Telefonul clientului nu poate fi gol!", null);
            return false;
        }

        prenumeClient = (prenumeClient != null) ? prenumeClient : "";
        emailClient = (emailClient != null) ? emailClient : "";

        LocalDateTime startDate, endDate;
        try {
            LocalDate startLocalDate = LocalDate.parse(startDateStr, dateFormatter);
            LocalDate endLocalDate = LocalDate.parse(endDateStr, dateFormatter);

            startDate = startLocalDate.atStartOfDay();
            endDate = endLocalDate.atStartOfDay();

            if (endDate.isBefore(startDate)) {
                showDialog(JOptionPane.ERROR_MESSAGE, "Data de sfârșit nu poate fi înainte de data de început!", null);
                return false;
            }

            if (startDate.isBefore(LocalDateTime.now())) {
                showDialog(JOptionPane.ERROR_MESSAGE, "Data de început nu poate fi în trecut!", null);
                return false;
            }
        } catch (DateTimeParseException ex) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Format dată invalid! Folosiți formatul yyyy-MM-dd.", null);
            return false;
        }

        boolean needToCheckAvailability = currentRezervareId == -1;
        if (currentRezervareId != -1) {
            Rezervare existingRezervare = rezervareDAO.findById(currentRezervareId);
            if (existingRezervare != null) {
                if (!startDate.toLocalDate().equals(existingRezervare.getStartDate().toLocalDate()) ||
                        !endDate.toLocalDate().equals(existingRezervare.getEndDate().toLocalDate())) {
                    needToCheckAvailability = true;
                }
            }
        }

        if (needToCheckAvailability) {
            boolean isAvailable = rezervareDAO.isCameraDisponibila(currentCameraId, startDate, endDate);
            if (!isAvailable) {
                showDialog(JOptionPane.ERROR_MESSAGE, "Camera nu este disponibilă în perioada selectată!", null);
                return false;
            }
        }

        boolean success;
        if (currentRezervareId == -1) {
            success = rezervareDAO.save(startDateStr, endDateStr, currentCameraId,
                    numeClient, prenumeClient, telefonClient, emailClient);
            if (success) {
                showDialog(JOptionPane.INFORMATION_MESSAGE, "Rezervare adăugată cu succes!", null);
            } else {
                showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la adăugarea rezervării!", null);
            }
        } else {
            success = rezervareDAO.update(currentRezervareId, startDateStr, endDateStr, currentCameraId,
                    numeClient, prenumeClient, telefonClient, emailClient);
            if (success) {
                showDialog(JOptionPane.INFORMATION_MESSAGE, "Rezervare actualizată cu succes!", null);
                currentRezervareId = -1;
            } else {
                showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la actualizarea rezervării!", null);
            }
        }

        return success;
    }

    private void deleteRezervare(ActionEvent e) {
        Object[] rowData = view.getTableSelection();
        if (rowData == null) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Selectați o rezervare pentru a o șterge!", null);
            return;
        }

        int rezervareId = (int) rowData[0];

        int confirmResult = showDialog(JOptionPane.QUESTION_MESSAGE, "Sigur doriți să ștergeți această rezervare?", null);
        if (confirmResult == JOptionPane.YES_OPTION) {
            boolean success = rezervareDAO.delete(rezervareId);
            if (success) {
                showDialog(JOptionPane.INFORMATION_MESSAGE, "Rezervare ștearsă cu succes!", null);

                LocalDate today = LocalDate.now();
                view.setFormData(new Object[] {
                        today.format(dateFormatter),
                        today.plusDays(1).format(dateFormatter),
                        "",
                        "",
                        "",
                        "",
                        true
                });

                loadRezervari();
                currentRezervareId = -1;
                updateButtonState(false);
            } else {
                showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la ștergerea rezervării!", null);
            }
        }
    }

    private void backToCameras(ActionEvent e) {
        mainView.showPanel("camera");
        currentRezervareId = -1;
    }
}