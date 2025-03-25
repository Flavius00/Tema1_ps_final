package org.example.presenter;

import org.example.model.dao.LantDAO;
import org.example.model.entities.Lant;
import org.example.view.LantGUI;
import org.example.view.MainView;

import java.awt.event.ActionEvent;

public class LantPresenter {
    private  LantGUI view;
    private LantDAO lantDAO;
    private MainView mainView;
    private int currentLantId = -1;
    private boolean isEditMode = false;
    private HotelPresenter hotelPresenter; // Added reference to HotelPresenter

    public LantPresenter(LantGUI view, LantDAO lantDAO, MainView mainView) {
        this.view = view;
        this.lantDAO = lantDAO;
        this.mainView = mainView;

        // Load initial data
        loadLanturi();

        // Set up listeners
        view.setAdaugaButtonListener(this::addLant);
        view.setEditareButtonListener(this::editLant);
        view.setStergereButtonListener(this::deleteLant);
        view.setInapoiButtonListener(this::backToMain);
        view.setSalveazaButtonListener(this::saveLant);
        view.setAnuleazaButtonListener(e -> view.ascundeFormular());
    }

    // Add method to set HotelPresenter reference
    public void setHotelPresenter(HotelPresenter hotelPresenter) {
        this.hotelPresenter = hotelPresenter;
    }

    private void loadLanturi() {
        Object[][] lanturi = lantDAO.getLanturiAsObjects();
        view.updateTable(lanturi);
    }

    private void addLant(ActionEvent e) {
        currentLantId = -1;
        isEditMode = false;
        view.setNume("");
        view.afiseazaFormular();
    }

    private void editLant(ActionEvent e) {
        int selectedRow = view.getSelectedRow();
        if (selectedRow != -1) {
            // Get data from selected row
            currentLantId = (int) view.getValueAt(selectedRow, 0);
            String nume = (String) view.getValueAt(selectedRow, 1);

            // Set edit mode and populate form
            isEditMode = true;
            view.setNume(nume);
            view.afiseazaFormular();
        } else {
            view.showMessage("Selectați un lanț hotelier pentru a-l modifica!");
        }
    }

    private void saveLant(ActionEvent e) {
        String nume = view.getNume();

        // Validate input
        if (nume == null || nume.trim().isEmpty()) {
            view.showMessage("Numele lanțului hotelier nu poate fi gol!");
            return;
        }

        boolean success;
        if (!isEditMode) {
            // Add new
            success = lantDAO.save(nume);
            if (success) {
                view.showMessage("Lanț hotelier adăugat cu succes!");
            } else {
                view.showMessage("Eroare la adăugarea lanțului hotelier!");
            }
        } else {
            // Update existing
            success = lantDAO.update(currentLantId, nume);
            if (success) {
                view.showMessage("Lanț hotelier actualizat cu succes!");
            } else {
                view.showMessage("Eroare la actualizarea lanțului hotelier!");
            }
        }

        if (success) {
            view.ascundeFormular();
            loadLanturi();
            currentLantId = -1;
            isEditMode = false;

            // Notify HotelPresenter to refresh its dropdowns
            if (hotelPresenter != null) {
                hotelPresenter.refreshLanturi();
            }
        }
    }

    private void deleteLant(ActionEvent e) {
        int selectedRow = view.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) view.getValueAt(selectedRow, 0);
            int confirmResult = view.showConfirmDialog("Sigur doriți să ștergeți acest lanț hotelier?");

            if (confirmResult == 0) { // Yes option
                boolean success = lantDAO.delete(id);
                if (success) {
                    view.showMessage("Lanț hotelier șters cu succes!");
                    loadLanturi();

                    // Notify HotelPresenter to refresh its dropdowns
                    if (hotelPresenter != null) {
                        hotelPresenter.refreshLanturi();
                    }
                } else {
                    view.showMessage("Eroare la ștergerea lanțului hotelier! Verificați dacă există hoteluri asociate.");
                }
            }
        } else {
            view.showMessage("Selectați un lanț hotelier pentru a-l șterge!");
        }
    }

    private void backToMain(ActionEvent e) {
        view.setVisible(false);
        currentLantId = -1;
        isEditMode = false;
    }
}