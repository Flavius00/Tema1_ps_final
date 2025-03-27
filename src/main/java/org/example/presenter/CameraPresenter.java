package org.example.presenter;

import org.example.model.dao.CameraDAO;
import org.example.model.dao.RezervareDAO;
import org.example.model.entities.Camera;
import org.example.util.ExportUtil;
import org.example.view.MainView;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.event.ListSelectionEvent;

public class CameraPresenter {
    private  CameraGUI view;
    private CameraDAO cameraDAO;
    private RezervareDAO rezervareDAO;
    private MainView mainView;
    private RezervarePresenter rezervarePresenter;
    // Modificat: formatul de dată fără oră
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int currentHotelId = -1;
    private String currentHotelName = "";
    private int currentCameraId = -1;

    // Field to store the last applied filter type
    private String lastFilterType = "";

    public CameraPresenter(CameraGUI view, CameraDAO cameraDAO, RezervareDAO rezervareDAO, MainView mainView) {
        this.view = view;
        this.cameraDAO = cameraDAO;
        this.rezervareDAO = rezervareDAO;
        this.mainView = mainView;

        // Set up listeners
        view.addAddButtonListener(this::addCamera);
        view.addEditButtonListener(this::editCamera);
        view.addDeleteButtonListener(this::deleteCamera);
        view.addViewReservationsButtonListener(this::viewReservations);
        view.addBackButtonListener(this::backToHotels);
        view.addExportCSVButtonListener(this::exportToCSV);
        view.addExportDOCButtonListener(this::exportToDOC);

        // Add apply filter button listener
        view.addApplyFilterButtonListener(this::applyFilter);

        // Add table selection listener
        view.addTableSelectionListener(this::handleTableSelection);

        // Set initial button state
        updateButtonState(false);
    }

    private void handleTableSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = view.getSelectedRow();
            if (selectedRow != -1) {
                // Get the camera ID from the selected row
                currentCameraId = (int) view.getValueAt(selectedRow, 0);

                // Enable buttons for actions on selected camera
                updateButtonState(true);

                // Populate form with selected camera data
                Camera camera = cameraDAO.findById(currentCameraId);
                if (camera != null) {
                    view.setRoomNumber(camera.getNrCamera());
                    view.setPrice(String.valueOf(camera.getPretPerNoapte()));
                }
            } else {
                // No row selected, disable buttons
                updateButtonState(false);
                currentCameraId = -1;
            }
        }
    }

    private void updateButtonState(boolean rowSelected) {
        boolean hasHotel = currentHotelId > 0;
        view.setButtonsEnabled(hasHotel, rowSelected, rowSelected, rowSelected, hasHotel);
    }

    public void setRezervarePresenter(RezervarePresenter rezervarePresenter) {
        this.rezervarePresenter = rezervarePresenter;
    }

    public void setCurrentHotel(int hotelId, String hotelName) {
        this.currentHotelId = hotelId;
        this.currentHotelName = hotelName;
        view.setHotelName(hotelName);
        loadCameras();

        // Reset selection
        currentCameraId = -1;
        view.clearForm();
        updateButtonState(false);
    }

    private void loadCameras() {
        if (currentHotelId > 0) {
            Object[][] cameras = cameraDAO.getCamereAsObjects(currentHotelId);
            view.populateCameraTable(cameras);
        }
    }

    private void addCamera(ActionEvent e) {
        // Clear form for a new camera
        if(validateAndSaveCamera()){
            loadCameras();
        }
        view.clearForm();
        currentCameraId = -1;

        // Inițial setăm câmpurile cu valori implicite
        view.setRoomNumber("");
        view.setPrice("");
    }

    private void editCamera(ActionEvent e) {
        int selectedRow = view.getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Selectați o cameră pentru a o modifica!");
            return;
        }

        // Camera form is already populated by selection handler
        if (validateAndSaveCamera()) {
            loadCameras();
        }
    }

    private boolean validateAndSaveCamera() {
        // Get form data
        String roomNumber = view.getRoomNumber();
        String priceText = view.getPrice();

        // Validate input
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            view.showMessage("Numărul camerei nu poate fi gol!");
            return false;
        }

        float price;
        try {
            // Verificăm dacă priceText nu este null sau gol
            if (priceText == null || priceText.trim().isEmpty()) {
                view.showMessage("Prețul nu poate fi gol!");
                return false;
            }

            // Înlocuim virgula cu punct pentru a permite formatul românesc
            priceText = priceText.replace(",", ".");

            price = Float.parseFloat(priceText);
            if (price <= 0) {
                view.showMessage("Prețul trebuie să fie un număr pozitiv!");
                return false;
            }
        } catch (NumberFormatException ex) {
            view.showMessage("Prețul trebuie să fie un număr valid!");
            return false;
        }

        // Save or update camera
        boolean success;
        if (currentCameraId == -1) {
            // Add new camera
            success = cameraDAO.save(currentHotelId, roomNumber, price, 0);
            if (success) {
                view.showMessage("Cameră adăugată cu succes!");
                view.clearForm();
            } else {
                view.showMessage("Eroare la adăugarea camerei!");
            }
        } else {
            // Update existing camera
            success = cameraDAO.update(currentCameraId, currentHotelId, roomNumber, price, 0);
            if (success) {
                view.showMessage("Cameră actualizată cu succes!");
                view.clearForm();
                currentCameraId = -1;
            } else {
                view.showMessage("Eroare la actualizarea camerei!");
            }
        }

        return success;
    }

    private void deleteCamera(ActionEvent e) {
        int selectedRow = view.getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Selectați o cameră pentru a o șterge!");
            return;
        }

        int cameraId = (int) view.getValueAt(selectedRow, 0);

        int confirmResult = view.showConfirmDialog("Sigur doriți să ștergeți această cameră?");
        if (confirmResult == 0) { // Yes option
            boolean success = cameraDAO.delete(cameraId);
            if (success) {
                view.showMessage("Cameră ștearsă cu succes!");
                view.clearForm();
                loadCameras();
                currentCameraId = -1;
                updateButtonState(false);
            } else {
                view.showMessage("Eroare la ștergerea camerei! Verificați dacă există rezervări asociate.");
            }
        }
    }

    private void viewReservations(ActionEvent e) {
        int selectedRow = view.getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Selectați o cameră pentru a vizualiza rezervările!");
            return;
        }

        int cameraId = (int) view.getValueAt(selectedRow, 0);
        Camera camera = cameraDAO.findById(cameraId);

        if (camera != null) {
            String cameraInfo = cameraDAO.getCameraInfoById(cameraId);
            rezervarePresenter.setCurrentCamera(cameraId, cameraInfo);
            mainView.showPanel("rezervare");
        } else {
            view.showMessage("Eroare la încărcarea datelor camerei!");
        }
    }

    // Export available rooms to CSV
    private void exportToCSV(ActionEvent e) {
        if (currentHotelId <= 0) {
            view.showMessage("Selectați mai întâi un hotel!");
            return;
        }

        // Ask for a date
        String dateStr = view.showInputDialog(
                "Introduceți data pentru afișarea camerelor disponibile (yyyy-MM-dd):",
                LocalDate.now().format(dateFormatter));

        if (dateStr == null || dateStr.trim().isEmpty()) {
            return; // User cancelled
        }

        // Parse the date and convert to LocalDateTime for DAO methods
        LocalDateTime selectedDateTime;
        try {
            LocalDate selectedDate = LocalDate.parse(dateStr, dateFormatter);
            // Setăm ora la începutul zilei (00:00)
            selectedDateTime = selectedDate.atStartOfDay();
        } catch (DateTimeParseException ex) {
            view.showMessage("Format de dată invalid! Folosiți formatul yyyy-MM-dd.");
            return;
        }

        // Get available rooms for this date
        Object[][] availableRooms = cameraDAO.filterByAvailabilityAsObjects(
                currentHotelId,
                selectedDateTime,
                selectedDateTime.plusDays(1));

        if (availableRooms.length == 0) {
            view.showMessage("Nu există camere disponibile pentru data selectată.");
            return;
        }

        // Export to CSV
        String[] headers = {"ID", "Număr Cameră", "Preț per Noapte"};
        boolean success = ExportUtil.exportToCSV(
                availableRooms,
                headers,
                currentHotelName,
                LocalDate.parse(dateStr, dateFormatter).format(dateFormatter),
                "camere_disponibile");

        if (success) {
            view.showMessage("Export în CSV realizat cu succes!");
        } else {
            view.showMessage("Export în CSV anulat sau eșuat.");
        }
    }

    // Export reserved rooms to DOC
    private void exportToDOC(ActionEvent e) {
        if (currentHotelId <= 0) {
            view.showMessage("Selectați mai întâi un hotel!");
            return;
        }

        // Ask for a date
        String dateStr = view.showInputDialog(
                "Introduceți data pentru afișarea camerelor rezervate (yyyy-MM-dd):",
                LocalDate.now().format(dateFormatter));

        if (dateStr == null || dateStr.trim().isEmpty()) {
            return; // User cancelled
        }

        // Parse the date and convert to LocalDateTime for DAO methods
        LocalDateTime selectedDateTime;
        try {
            LocalDate selectedDate = LocalDate.parse(dateStr, dateFormatter);
            // Setăm ora la începutul zilei (00:00)
            selectedDateTime = selectedDate.atStartOfDay();
        } catch (DateTimeParseException ex) {
            view.showMessage("Format de dată invalid! Folosiți formatul yyyy-MM-dd.");
            return;
        }

        // Get reserved rooms for this date
        Object[][] rezervari = rezervareDAO.findReservationsByHotelAndDateAsObjects(
                currentHotelId,
                selectedDateTime);

        if (rezervari.length == 0) {
            view.showMessage("Nu există rezervări pentru data selectată.");
            return;
        }

        // Export to DOC
        String[] headers = {"ID", "Data de început", "Data de sfârșit", "Client", "Telefon"};
        String title = "Rezervări pentru " + currentHotelName + " în data de " +
                LocalDate.parse(dateStr, dateFormatter).format(dateFormatter);

        boolean success = ExportUtil.exportToDOC(
                rezervari,
                headers,
                currentHotelName,
                LocalDate.parse(dateStr, dateFormatter).format(dateFormatter),
                title,
                "rezervari");

        if (success) {
            view.showMessage("Export în DOC realizat cu succes!");
        } else {
            view.showMessage("Export în DOC anulat sau eșuat.");
        }
    }

    private void backToHotels(ActionEvent e) {
        mainView.showPanel("hotel");
        currentCameraId = -1;
    }

    // Method to apply filter
    private void applyFilter(ActionEvent e) {
        if (currentHotelId <= 0) {
            view.showMessage("Selectați mai întâi un hotel!");
            return;
        }

        String filterType = view.getFilterType();
        String filterValue = view.getFilterValue();

        // Store the last filter type
        lastFilterType = filterType;

        Object[][] filteredRooms = null;

        switch (filterType) {
            case "Toate camerele":
                // Reset filter
                filteredRooms = cameraDAO.getCamereAsObjects(currentHotelId);
                break;

            case "Disponibilitate":
                // Validate and parse date
                try {
                    // Ne asigurăm că avem o valoare
                    if (filterValue == null || filterValue.trim().isEmpty()) {
                        view.showMessage("Introduceți o dată validă!");
                        return;
                    }

                    // Convertim din LocalDate în LocalDateTime pentru a folosi DAO-urile existente
                    LocalDate date = LocalDate.parse(filterValue, dateFormatter);
                    LocalDateTime dateTime = date.atStartOfDay();

                    filteredRooms = cameraDAO.filterByAvailabilityAsObjects(
                            currentHotelId, dateTime, dateTime.plusDays(1));
                } catch (DateTimeParseException ex) {
                    view.showMessage("Format dată invalid! Folosiți formatul yyyy-MM-dd.");
                    return;
                }
                break;

            case "Preț minim":
                try {
                    // Ne asigurăm că avem o valoare
                    if (filterValue == null || filterValue.trim().isEmpty()) {
                        view.showMessage("Introduceți un preț minim valid!");
                        return;
                    }

                    // Înlocuim virgula cu punct pentru a permite formatul românesc
                    filterValue = filterValue.replace(",", ".");

                    float minPrice = Float.parseFloat(filterValue);
                    if (minPrice < 0) {
                        view.showMessage("Prețul minim nu poate fi negativ!");
                        return;
                    }

                    filteredRooms = cameraDAO.filterByPriceRangeAsObjects(
                            currentHotelId, minPrice, Float.MAX_VALUE);
                } catch (NumberFormatException ex) {
                    view.showMessage("Valoare preț invalid!");
                    return;
                }
                break;

            case "Preț maxim":
                try {
                    // Ne asigurăm că avem o valoare
                    if (filterValue == null || filterValue.trim().isEmpty()) {
                        view.showMessage("Introduceți un preț maxim valid!");
                        return;
                    }

                    // Înlocuim virgula cu punct pentru a permite formatul românesc
                    filterValue = filterValue.replace(",", ".");

                    float maxPrice = Float.parseFloat(filterValue);
                    if (maxPrice <= 0) {
                        view.showMessage("Prețul maxim trebuie să fie pozitiv!");
                        return;
                    }

                    filteredRooms = cameraDAO.filterByPriceRangeAsObjects(
                            currentHotelId, 0, maxPrice);
                } catch (NumberFormatException ex) {
                    view.showMessage("Valoare preț invalid!");
                    return;
                }
                break;

            case "Facilități":
                if (filterValue == null || filterValue.trim().isEmpty()) {
                    view.showMessage("Introduceți un criteriu de căutare pentru facilități!");
                    return;
                }
                filteredRooms = cameraDAO.filterByFacilitiesAsObjects(currentHotelId, filterValue);
                break;
        }

        if (filteredRooms != null) {
            if (filteredRooms.length == 0) {
                view.showMessage("Nu există camere care să corespundă criteriilor de filtrare.");
            } else {
                view.populateCameraTable(filteredRooms);
                view.clearFilterField();
                if (!filterType.equals("Toate camerele")) {
                    view.showMessage("Filtrare aplicată cu succes. " + filteredRooms.length + " camere găsite.");
                }
            }
        }
    }
}