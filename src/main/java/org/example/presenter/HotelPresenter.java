package org.example.presenter;

import org.example.model.dao.HotelDAO;
import org.example.model.dao.LantDAO;
import org.example.model.dao.LocatieDAO;
import org.example.model.entities.Hotel;
import org.example.view.MainView;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class HotelPresenter {
    private  HotelGUI view;
    private HotelDAO hotelDAO;
    private LocatieDAO locatieDAO;
    private LantDAO lantDAO;
    private MainView mainView;
    private CameraPresenter cameraPresenter;

    // Maps to store ID mapping for combo boxes
    private Map<String, Integer> lantIdMap = new HashMap<>();
    private Map<String, Integer> locatieIdMap = new HashMap<>();
    private int currentHotelId = -1;

    public HotelPresenter(HotelGUI view, HotelDAO hotelDAO, LocatieDAO locatieDAO, LantDAO lantDAO, MainView mainView) {
        this.view = view;
        this.hotelDAO = hotelDAO;
        this.locatieDAO = locatieDAO;
        this.lantDAO = lantDAO;
        this.mainView = mainView;

        // Load initial data
        loadComboBoxes();
        loadHotels();

        // Set up listeners
        view.addAddButtonListener(this::addHotel);
        view.addEditButtonListener(this::editHotel);
        view.addDeleteButtonListener(this::deleteHotel);
        view.addViewCamerasButtonListener(this::viewCameras);
        view.addBackButtonListener(this::backToMain);

        // Add filter by chain listener
        view.addFilterByChainButtonListener(this::filterByChain);

        // Add table selection listener
        view.addTableSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = view.getSelectedRow();
                if (selectedRow != -1) {
                    handleHotelSelection(selectedRow);
                    view.setButtonsEnabled(true, true, true, true);
                } else {
                    view.setButtonsEnabled(true, false, false, false);
                }
            }
        });

        // Set initial button state
        view.setButtonsEnabled(true, false, false, false);
    }

    public void setCameraPresenter(CameraPresenter cameraPresenter) {
        this.cameraPresenter = cameraPresenter;
    }


    // Method to refresh lanturi dropdown (called from LantPresenter)
    public void refreshLanturi() {
        loadLanturi();
    }

    private void handleHotelSelection(int selectedRow) {
        int hotelId = (int) view.getValueAt(selectedRow, 0);
        currentHotelId = hotelId;

        Hotel hotel = hotelDAO.findById(hotelId);
        if (hotel != null) {
            // Populate form fields
            view.setName(hotel.getNume());
            view.setPhone(hotel.getTelefon());
            view.setEmail(hotel.getEmail());
            view.setFacilities(hotel.getFacilitati());

            // Find and select location in combobox
            selectLocationInComboBox(hotel.getIdLocatie());

            // Find and select chain in combobox
            selectChainInComboBox(hotel.getIdLant());
        }
    }

    private void selectLocationInComboBox(int locationId) {
        // Start from index 1 (index 0 is "Select a location")
        for (int i = 1; i < view.getLocatieSelectedIndex(); i++) {
            view.setLocatieSelectedIndex(i);
            String displayText = view.getLocatieSelectedItem();

            if (locatieIdMap.containsKey(displayText) && locatieIdMap.get(displayText) == locationId) {
                return; // Found and selected
            }
        }

        // If not found, select the first item (empty option)
        view.setLocatieSelectedIndex(0);
    }

    private void selectChainInComboBox(int chainId) {
        // Start from index 1 (index 0 is "Select a chain")
        for (int i = 1; i < view.getLantSelectedIndex(); i++) {
            view.setLantSelectedIndex(i);
            String displayText = view.getLantSelectedItem();

            if (lantIdMap.containsKey(displayText) && lantIdMap.get(displayText) == chainId) {
                return; // Found and selected
            }
        }

        // If not found, select the first item (empty option)
        view.setLantSelectedIndex(0);
    }

    private void loadHotels() {
        Object[][] hotels = hotelDAO.getHoteluriAsObjects();
        view.populateHotelTable(hotels);
    }

    private void loadComboBoxes() {
        loadLocatii();
        loadLanturi();
    }

    private void loadLocatii() {
        Object[][] locatii = locatieDAO.getLocatiiAsObjects();

        // Clear previous data
        view.removeAllLocatieItems();
        locatieIdMap.clear();

        // Add empty selection item
        view.addLocatieItem("Selectați o locație");

        // Add all locations
        for (Object[] locatie : locatii) {
            int id = (Integer) locatie[0];
            String displayText = (String) locatie[1];
            view.addLocatieItem(displayText);
            locatieIdMap.put(displayText, id);
        }
    }

    private void loadLanturi() {
        Object[][] lanturi = lantDAO.getLanturiAsObjects();

        // Clear previous data
        view.removeAllLantItems();
        lantIdMap.clear();

        // Add empty selection item
        view.addLantItem("Selectați un lanț");

        // Add all hotel chains
        for (Object[] lant : lanturi) {
            int id = (Integer) lant[0];
            String nume = (String) lant[1];
            view.addLantItem(nume);
            lantIdMap.put(nume, id);
        }

        // Also update the filter combo box
        view.updateFilterChainComboBox();
    }

    private void addHotel(ActionEvent e) {
        // Clear form for new hotel entry
        if(validateAndSaveHotel()){
            loadHotels();
        }
        view.clearForm();
        currentHotelId = -1;

        // Inițial setăm câmpurile cu valori implicite
        view.setName("");
        view.setPhone("");
        view.setEmail("");
        view.setFacilities("");
        view.setLocatieSelectedIndex(0);
        view.setLantSelectedIndex(0);
    }

    private void editHotel(ActionEvent e) {
        int selectedRow = view.getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Selectați un hotel pentru a-l modifica!");
            return;
        }

        // Hotel is already selected and form is populated by selection listener
        // Just validate and save the changes
        if (validateAndSaveHotel()) {
            view.clearForm();
            loadHotels();
            currentHotelId = -1;
        }
    }

    private boolean validateAndSaveHotel() {
        // Get form data
        String nume = view.getName();
        String telefon = view.getPhone();
        String email = view.getEmail();
        String facilitati = view.getFacilities();

        // Get selected location ID
        int idLocatie = -1;
        String selectedLocatie = view.getLocatieSelectedItem();
        if (locatieIdMap.containsKey(selectedLocatie)) {
            idLocatie = locatieIdMap.get(selectedLocatie);
        }

        // Get selected chain ID
        int idLant = -1;
        String selectedLant = view.getLantSelectedItem();
        if (lantIdMap.containsKey(selectedLant)) {
            idLant = lantIdMap.get(selectedLant);
        }

        // Validate data
        if (nume == null || nume.trim().isEmpty()) {
            view.showMessage("Numele hotelului nu poate fi gol!");
            return false;
        }

        if (idLocatie == -1) {
            view.showMessage("Selectați o locație validă!");
            return false;
        }

        if (idLant == -1) {
            view.showMessage("Selectați un lanț hotelier valid!");
            return false;
        }

        // Asigurăm că restul câmpurilor nu sunt null
        telefon = (telefon != null) ? telefon : "";
        email = (email != null) ? email : "";
        facilitati = (facilitati != null) ? facilitati : "";

        boolean success;
        if (currentHotelId == -1) {
            // Adding new hotel
            success = hotelDAO.save(nume, idLocatie, telefon, email, facilitati, idLant);
            if (success) {
                view.showMessage("Hotel adăugat cu succes!");
            } else {
                view.showMessage("Eroare la adăugarea hotelului!");
            }
        } else {
            // Updating existing hotel
            success = hotelDAO.update(currentHotelId, nume, idLocatie, telefon, email, facilitati, idLant);
            if (success) {
                view.showMessage("Hotel actualizat cu succes!");
            } else {
                view.showMessage("Eroare la actualizarea hotelului!");
            }
        }

        return success;
    }

    private void deleteHotel(ActionEvent e) {
        int selectedRow = view.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) view.getValueAt(selectedRow, 0);
            int confirmResult = view.showConfirmDialog("Sigur doriți să ștergeți acest hotel?");
            if (confirmResult == 0) { // Yes option
                boolean success = hotelDAO.delete(id);
                if (success) {
                    view.showMessage("Hotel șters cu succes!");
                    view.clearForm();
                    loadHotels();
                    currentHotelId = -1;
                } else {
                    view.showMessage("Eroare la ștergerea hotelului! Verificați dacă există camere asociate.");
                }
            }
        } else {
            view.showMessage("Selectați un hotel pentru a-l șterge!");
        }
    }

    private void viewCameras(ActionEvent e) {
        int selectedRow = view.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) view.getValueAt(selectedRow, 0);
            Hotel hotel = hotelDAO.findById(id);

            if (hotel != null) {
                cameraPresenter.setCurrentHotel(id, hotel.getNume());
                mainView.showPanel("camera");
            } else {
                view.showMessage("Eroare la încărcarea datelor hotelului!");
            }
        } else {
            view.showMessage("Selectați un hotel pentru a vizualiza camerele!");
        }
    }

    // Method to filter hotels by selected chain
    private void filterByChain(ActionEvent e) {
        String selectedChain = view.getSelectedFilterChain();

        if (selectedChain.equals("Toate lanțurile")) {
            // Reset to show all hotels
            loadHotels();
            return;
        }

        // Get the chain ID
        Integer chainId = null;
        for (Map.Entry<String, Integer> entry : lantIdMap.entrySet()) {
            if (entry.getKey().equals(selectedChain)) {
                chainId = entry.getValue();
                break;
            }
        }

        if (chainId != null) {
            // Filter hotels by chain ID
            java.util.List<Hotel> filteredHotels = hotelDAO.findByLantId(chainId);

            // Convert to Object[][] for display
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

            // Update the table
            view.populateHotelTable(hotelData);

            if (filteredHotels.isEmpty()) {
                view.showMessage("Nu există hoteluri în lanțul selectat.");
            }
        } else {
            view.showMessage("Eroare la filtrarea hotelurilor!");
        }
    }

    private void backToMain(ActionEvent e) {
        mainView.showPanel("main");
        currentHotelId = -1;
    }
}