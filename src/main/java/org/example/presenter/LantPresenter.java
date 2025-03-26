package org.example.presenter;

import org.example.model.dao.LantDAO;
import org.example.model.entities.Lant;
import org.example.view.LantGUI;
import org.example.view.MainView;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class LantPresenter {
    private LantGUI view;
    private LantDAO lantDAO;
    private MainView mainView;
    private int currentLantId = -1;
    private boolean isEditMode = false;
    private HotelPresenter hotelPresenter;

    public LantPresenter(LantGUI view, LantDAO lantDAO, MainView mainView) {
        this.view = view;
        this.lantDAO = lantDAO;
        this.mainView = mainView;

        loadLanturi();

        Map<String, ActionListener> listeners = new HashMap<>();
        listeners.put("add", this::addLant);
        listeners.put("edit", this::editLant);
        listeners.put("delete", this::deleteLant);
        listeners.put("back", this::backToMain);
        listeners.put("save", this::saveLant);
        listeners.put("cancel", e -> view.updateComponents(null, false, null));

        view.registerEventHandlers(listeners);
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

    public void setHotelPresenter(HotelPresenter hotelPresenter) {
        this.hotelPresenter = hotelPresenter;
    }

    private void loadLanturi() {
        Object[][] lanturi = lantDAO.getLanturiAsObjects();
        view.updateComponents(lanturi, false, null);
    }

    private void addLant(ActionEvent e) {
        currentLantId = -1;
        isEditMode = false;
        view.updateComponents(null, true, "");
    }

    private void editLant(ActionEvent e) {
        Object[] rowData = view.getTableSelection();
        if (rowData == null) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Selectați un lanț hotelier pentru a-l modifica!", null);
            return;
        }

        currentLantId = (int) rowData[0];
        String nume = (String) rowData[1];

        isEditMode = true;
        view.updateComponents(null, true, nume);
    }

    private void saveLant(ActionEvent e) {
        String nume = view.getNume();

        if (nume == null || nume.trim().isEmpty()) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Numele lanțului hotelier nu poate fi gol!", null);
            return;
        }

        boolean success;
        if (!isEditMode) {
            success = lantDAO.save(nume);
            if (success) {
                showDialog(JOptionPane.INFORMATION_MESSAGE, "Lanț hotelier adăugat cu succes!", null);
            } else {
                showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la adăugarea lanțului hotelier!", null);
            }
        } else {
            success = lantDAO.update(currentLantId, nume);
            if (success) {
                showDialog(JOptionPane.INFORMATION_MESSAGE, "Lanț hotelier actualizat cu succes!", null);
            } else {
                showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la actualizarea lanțului hotelier!", null);
            }
        }

        if (success) {
            view.updateComponents(null, false, null);
            loadLanturi();
            currentLantId = -1;
            isEditMode = false;

            if (hotelPresenter != null) {
                hotelPresenter.refreshLanturi();
            }
        }
    }

    private void deleteLant(ActionEvent e) {
        Object[] rowData = view.getTableSelection();
        if (rowData == null) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Selectați un lanț hotelier pentru a-l șterge!", null);
            return;
        }

        int id = (int) rowData[0];
        int confirmResult = showDialog(JOptionPane.QUESTION_MESSAGE, "Sigur doriți să ștergeți acest lanț hotelier?", null);

        if (confirmResult == JOptionPane.YES_OPTION) {
            boolean success = lantDAO.delete(id);
            if (success) {
                showDialog(JOptionPane.INFORMATION_MESSAGE, "Lanț hotelier șters cu succes!", null);
                loadLanturi();

                if (hotelPresenter != null) {
                    hotelPresenter.refreshLanturi();
                }
            } else {
                showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la ștergerea lanțului hotelier! Verificați dacă există hoteluri asociate.", null);
            }
        }
    }

    private void backToMain(ActionEvent e) {
        view.setVisible(false);
        currentLantId = -1;
        isEditMode = false;
    }
}