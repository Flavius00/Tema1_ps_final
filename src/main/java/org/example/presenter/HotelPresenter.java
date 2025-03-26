package org.example.presenter;

import org.example.model.dao.HotelDAO;
import org.example.model.dao.LantDAO;
import org.example.model.dao.LocatieDAO;
import org.example.model.entities.Hotel;
import org.example.view.HotelGUI;
import org.example.view.MainView;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class HotelPresenter {
    private HotelGUI view;
    private HotelDAO hotelDAO;
    private LocatieDAO locatieDAO;
    private LantDAO lantDAO;
    private MainView mainView;
    private CameraPresenter cameraPresenter;

    private Map<String, Integer> lantIdMap = new HashMap<>();
    private Map<String, Integer> locatieIdMap = new HashMap<>();
    private int currentHotelId = -1;

    public HotelPresenter(HotelGUI view, HotelDAO hotelDAO, LocatieDAO locatieDAO, LantDAO lantDAO, MainView mainView) {
        this.view = view;
        this.hotelDAO = hotelDAO;
        this.locatieDAO = locatieDAO;
        this.lantDAO = lantDAO;
        this.mainView = mainView;

        loadComboBoxes();
        loadHotels();

        Map<String, ActionListener> listeners = new HashMap<>();
        listeners.put("add", this::addHotel);
        listeners.put("edit", this::editHotel);
        listeners.put("delete", this::deleteHotel);
        listeners.put("viewCameras", this::viewCameras);
        listeners.put("back", this::backToMain);
        listeners.put("filterByChain", this::filterByChain);

        view.registerEventHandlers(this::handleHotelSelection, listeners);

        boolean[] buttonStates = {true, false, false, false};
        view.updateComponents(null, null, buttonStates);
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

    public void setCameraPresenter(CameraPresenter cameraPresenter) {
        this.cameraPresenter = cameraPresenter;
    }

    public void refreshLanturi() {
        loadLanturi();
    }

    private void handleHotelSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Object[] rowData = view.getTableSelection();
            if (rowData != null) {
                int hotelId = (int) rowData[0];
                currentHotelId = hotelId;

                Hotel hotel = hotelDAO.findById(hotelId);
                if (hotel != null) {
                    view.setFormData(new Object[] {
                            hotel.getNume(),
                            hotel.getTelefon(),
                            hotel.getEmail(),
                            hotel.getFacilitati(),
                            findLocationIndexById(hotel.getIdLocatie()),
                            findChainIndexById(hotel.getIdLant()),
                            false
                    });

                    boolean[] buttonStates = {true, true, true, true};
                    view.updateComponents(null, null, buttonStates);
                }
            } else {
                boolean[] buttonStates = {true, false, false, false};
                view.updateComponents(null, null, buttonStates);
                currentHotelId = -1;
            }
        }
    }

    private int findLocationIndexById(int locationId) {
        if (locatieIdMap.containsValue(locationId)) {
            for (Map.Entry<String, Integer> entry : locatieIdMap.entrySet()) {
                if (entry.getValue() == locationId) {
                    return 1;
                }
            }
        }

        return 0;
    }

    private int findChainIndexById(int chainId) {
        if (lantIdMap.containsValue(chainId)) {
            for (Map.Entry<String, Integer> entry : lantIdMap.entrySet()) {
                if (entry.getValue() == chainId) {
                    return 1;
                }
            }
        }

        return 0;
    }

    private void loadHotels() {
        Object[][] hotels = hotelDAO.getHoteluriAsObjects();
        view.updateComponents(hotels, null, null);
    }

    private void loadComboBoxes() {
        loadLocatii();
        loadLanturi();
    }

    private void loadLocatii() {
        Object[][] locatii = locatieDAO.getLocatiiAsObjects();

        locatieIdMap.clear();

        String[] locationItems = new String[locatii.length + 1];
        locationItems[0] = "Selectați o locație";

        for (int i = 0; i < locatii.length; i++) {
            int id = (Integer) locatii[i][0];
            String displayText = (String) locatii[i][1];
            locationItems[i + 1] = displayText;
            locatieIdMap.put(displayText, id);
        }

        Object[][] comboData = new Object[2][];
        comboData[0] = locationItems;

        view.updateComponents(null, comboData, null);
    }

    private void loadLanturi() {
        Object[][] lanturi = lantDAO.getLanturiAsObjects();

        lantIdMap.clear();

        String[] chainItems = new String[lanturi.length + 1];
        chainItems[0] = "Selectați un lanț";

        for (int i = 0; i < lanturi.length; i++) {
            int id = (Integer) lanturi[i][0];
            String nume = (String) lanturi[i][1];
            chainItems[i + 1] = nume;
            lantIdMap.put(nume, id);
        }

        Object[][] comboData = new Object[2][];
        comboData[1] = chainItems;

        view.updateComponents(null, comboData, null);
    }

    private void addHotel(ActionEvent e) {
        if(validateAndSaveHotel()){
            loadHotels();
        }

        view.setFormData(new Object[] {
                "",
                "",
                "",
                "",
                0,
                0,
                true
        });

        currentHotelId = -1;
    }

    private void editHotel(ActionEvent e) {
        Object[] rowData = view.getTableSelection();
        if (rowData == null) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Selectați un hotel pentru a-l modifica!", null);
            return;
        }

        if (validateAndSaveHotel()) {
            view.setFormData(new Object[] {"", "", "", "", 0, 0, true});
            loadHotels();
            currentHotelId = -1;
        }
    }

    private boolean validateAndSaveHotel() {
        Object[] formValues = view.getFormData();
        String nume = (String) formValues[0];
        String telefon = (String) formValues[1];
        String email = (String) formValues[2];
        String facilitati = (String) formValues[3];
        String selectedLocatie = (String) formValues[4];
        String selectedLant = (String) formValues[5];

        int idLocatie = -1;
        if (locatieIdMap.containsKey(selectedLocatie)) {
            idLocatie = locatieIdMap.get(selectedLocatie);
        }

        int idLant = -1;
        if (lantIdMap.containsKey(selectedLant)) {
            idLant = lantIdMap.get(selectedLant);
        }

        if (nume == null || nume.trim().isEmpty()) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Numele hotelului nu poate fi gol!", null);
            return false;
        }

        if (idLocatie == -1) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Selectați o locație validă!", null);
            return false;
        }

        if (idLant == -1) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Selectați un lanț hotelier valid!", null);
            return false;
        }

        telefon = (telefon != null) ? telefon : "";
        email = (email != null) ? email : "";
        facilitati = (facilitati != null) ? facilitati : "";

        boolean success;
        if (currentHotelId == -1) {
            success = hotelDAO.save(nume, idLocatie, telefon, email, facilitati, idLant);
            if (success) {
                showDialog(JOptionPane.INFORMATION_MESSAGE, "Hotel adăugat cu succes!", null);
            } else {
                showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la adăugarea hotelului!", null);
            }
        } else {
            success = hotelDAO.update(currentHotelId, nume, idLocatie, telefon, email, facilitati, idLant);
            if (success) {
                showDialog(JOptionPane.INFORMATION_MESSAGE, "Hotel actualizat cu succes!", null);
            } else {
                showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la actualizarea hotelului!", null);
            }
        }

        return success;
    }

    private void deleteHotel(ActionEvent e) {
        Object[] rowData = view.getTableSelection();
        if (rowData == null) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Selectați un hotel pentru a-l șterge!", null);
            return;
        }

        int id = (int) rowData[0];
        int confirmResult = showDialog(JOptionPane.QUESTION_MESSAGE, "Sigur doriți să ștergeți acest hotel?", null);
        if (confirmResult == JOptionPane.YES_OPTION) {
            boolean success = hotelDAO.delete(id);
            if (success) {
                showDialog(JOptionPane.INFORMATION_MESSAGE, "Hotel șters cu succes!", null);
                view.setFormData(new Object[] {"", "", "", "", 0, 0, true});
                loadHotels();
                currentHotelId = -1;
            } else {
                showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la ștergerea hotelului! Verificați dacă există camere asociate.", null);
            }
        }
    }

    private void viewCameras(ActionEvent e) {
        Object[] rowData = view.getTableSelection();
        if (rowData == null) {
            showDialog(JOptionPane.ERROR_MESSAGE, "Selectați un hotel pentru a vizualiza camerele!", null);
            return;
        }

        int id = (int) rowData[0];
        Hotel hotel = hotelDAO.findById(id);

        if (hotel != null) {
            cameraPresenter.setCurrentHotel(id, hotel.getNume());
            mainView.showPanel("camera");
        } else {
            showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la încărcarea datelor hotelului!", null);
        }
    }

    private void filterByChain(ActionEvent e) {
        Object[] formValues = view.getFormData();
        String selectedChain = (String) formValues[6];

        if (selectedChain.equals("Toate lanțurile")) {
            loadHotels();
            return;
        }

        Integer chainId = null;
        for (Map.Entry<String, Integer> entry : lantIdMap.entrySet()) {
            if (entry.getKey().equals(selectedChain)) {
                chainId = entry.getValue();
                break;
            }
        }

        if (chainId != null) {
            java.util.List<Hotel> filteredHotels = hotelDAO.findByLantId(chainId);

            Object[][] hotelData = new Object[filteredHotels.size()][7];

            for (int i = 0; i < filteredHotels.size(); i++) {
                Hotel hotel = filteredHotels.get(i);
                hotelData[i][0] = hotel.getId();
                hotelData[i][1] = hotel.getNume();
                hotelData[i][2] = locatieDAO.getDisplayString(hotel.getIdLocatie());
                hotelData[i][3] = hotel.getTelefon();
                hotelData[i][4] = hotel.getEmail();
                hotelData[i][5] = hotel.getFacilitati();
                hotelData[i][6] = lantDAO.findById(hotel.getIdLant()).getNume();
            }

            view.updateComponents(hotelData, null, null);

            if (filteredHotels.isEmpty()) {
                showDialog(JOptionPane.INFORMATION_MESSAGE, "Nu există hoteluri în lanțul selectat.", null);
            }
        } else {
            showDialog(JOptionPane.ERROR_MESSAGE, "Eroare la filtrarea hotelurilor!", null);
        }
    }

    private void backToMain(ActionEvent e) {
        mainView.showPanel("main");
        currentHotelId = -1;
    }
}