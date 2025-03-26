package org.example.presenter;

import org.example.model.dao.CameraDAO;
import org.example.model.dao.RezervareDAO;
import org.example.model.entities.Camera;
import org.example.util.ExportUtil;
import org.example.view.CameraGUI;
import org.example.view.MainView;

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

public class CameraPresenter {
    private CameraGUI view;
    private CameraDAO cameraDAO;
    private RezervareDAO rezervareDAO;
    private MainView mainView;
    private RezervarePresenter rezervarePresenter;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int currentHotelId = -1;
    private String currentHotelName = "";
    private int currentCameraId = -1;
    private String lastFilterType = "";

    public CameraPresenter(CameraGUI view, CameraDAO cameraDAO, RezervareDAO rezervareDAO, MainView mainView) {
        this.view = view;
        this.cameraDAO = cameraDAO;
        this.rezervareDAO = rezervareDAO;
        this.mainView = mainView;

        Map<String, ActionListener> listeners = new HashMap<>();
        listeners.put("add", this::addCamera);
        listeners.put("edit", this::editCamera);
        listeners.put("delete", this::deleteCamera);
        listeners.put("viewReservations", this::viewReservations);
        listeners.put("back", this::backToHotels);
        listeners.put("exportCSV", this::exportToCSV);
        listeners.put("exportDOC", this::exportToDOC);
        listeners.put("applyFilter", this::applyFilter);

        view.registerEventHandlers(this::handleTableSelection, listeners);

        updateButtonState(false);
    }

    private void showMessageDialog(int dialogType, String message) {
        view.displayDialog(dialogType, message, null);
    }

    private boolean showConfirmDialog(String message) {
        view.displayDialog(JOptionPane.QUESTION_MESSAGE, message, null);
        return true;
    }

    private String showInputDialog(String message, String initialValue) {
        view.displayDialog(JOptionPane.PLAIN_MESSAGE, message, initialValue);

        return initialValue;
    }

    private void handleTableSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Object[] rowData = view.getTableSelection();
            if (rowData != null) {
                currentCameraId = (int) rowData[0];

                updateButtonState(true);

                Camera camera = cameraDAO.findById(currentCameraId);
                if (camera != null) {
                    view.setFormData(new Object[] {
                            camera.getNrCamera(),
                            String.valueOf(camera.getPretPerNoapte()),
                            false
                    });
                }
            } else {
                updateButtonState(false);
                currentCameraId = -1;
            }
        }
    }

    private void updateButtonState(boolean rowSelected) {
        boolean hasHotel = currentHotelId > 0;
        boolean[] buttonStates = {
                hasHotel,
                rowSelected,
                rowSelected,
                rowSelected,
                hasHotel
        };
        view.updateComponents(currentHotelName, null, buttonStates);
    }

    public void setRezervarePresenter(RezervarePresenter rezervarePresenter) {
        this.rezervarePresenter = rezervarePresenter;
    }

    public void setCurrentHotel(int hotelId, String hotelName) {
        this.currentHotelId = hotelId;
        this.currentHotelName = hotelName;
        view.updateComponents(hotelName, null, null);
        loadCameras();

        currentCameraId = -1;
        view.setFormData(new Object[] {null, null, true});
        updateButtonState(false);
    }

    private void loadCameras() {
        if (currentHotelId > 0) {
            Object[][] cameras = cameraDAO.getCamereAsObjects(currentHotelId);
            view.updateComponents(null, cameras, null);
        }
    }

    private void addCamera(ActionEvent e) {
        if(validateAndSaveCamera()){
            loadCameras();
        }
        view.setFormData(new Object[] {null, null, true});
        currentCameraId = -1;
    }

    private void editCamera(ActionEvent e) {
        Object[] rowData = view.getTableSelection();
        if (rowData == null) {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Selectați o cameră pentru a o modifica!");
            return;
        }

        if (validateAndSaveCamera()) {
            loadCameras();
        }
    }

    private boolean validateAndSaveCamera() {
        Object[] formValues = view.getFormData();
        String roomNumber = (String) formValues[0];
        String priceText = (String) formValues[1];

        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Numărul camerei nu poate fi gol!");
            return false;
        }

        float price;
        try {
            if (priceText == null || priceText.trim().isEmpty()) {
                showMessageDialog(JOptionPane.ERROR_MESSAGE, "Prețul nu poate fi gol!");
                return false;
            }

            priceText = priceText.replace(",", ".");

            price = Float.parseFloat(priceText);
            if (price <= 0) {
                showMessageDialog(JOptionPane.ERROR_MESSAGE, "Prețul trebuie să fie un număr pozitiv!");
                return false;
            }
        } catch (NumberFormatException ex) {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Prețul trebuie să fie un număr valid!");
            return false;
        }

        boolean success;
        if (currentCameraId == -1) {
            success = cameraDAO.save(currentHotelId, roomNumber, price, 0);
            if (success) {
                showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "Cameră adăugată cu succes!");
                view.setFormData(new Object[] {null, null, true});

            } else {
                showMessageDialog(JOptionPane.ERROR_MESSAGE, "Eroare la adăugarea camerei!");
            }
        } else {
            success = cameraDAO.update(currentCameraId, currentHotelId, roomNumber, price, 0);
            if (success) {
                showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "Cameră actualizată cu succes!");
                view.setFormData(new Object[] {null, null, true});
                currentCameraId = -1;
            } else {
                showMessageDialog(JOptionPane.ERROR_MESSAGE, "Eroare la actualizarea camerei!");
            }
        }

        return success;
    }

    private void deleteCamera(ActionEvent e) {
        Object[] rowData = view.getTableSelection();
        if (rowData == null) {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Selectați o cameră pentru a o șterge!");
            return;
        }

        int cameraId = (int) rowData[0];

        boolean confirmResult = showConfirmDialog("Sigur doriți să ștergeți această cameră?");
        if (confirmResult) {
            boolean success = cameraDAO.delete(cameraId);
            if (success) {
                showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "Cameră ștearsă cu succes!");
                view.setFormData(new Object[] {null, null, true});
                loadCameras();
                currentCameraId = -1;
                updateButtonState(false);
            } else {
                showMessageDialog(JOptionPane.ERROR_MESSAGE, "Eroare la ștergerea camerei! Verificați dacă există rezervări asociate.");
            }
        }
    }

    private void viewReservations(ActionEvent e) {
        Object[] rowData = view.getTableSelection();
        if (rowData == null) {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Selectați o cameră pentru a vizualiza rezervările!");
            return;
        }

        int cameraId = (int) rowData[0];
        Camera camera = cameraDAO.findById(cameraId);

        if (camera != null) {
            String cameraInfo = cameraDAO.getCameraInfoById(cameraId);
            rezervarePresenter.setCurrentCamera(cameraId, cameraInfo);
            mainView.showPanel("rezervare");
        } else {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Eroare la încărcarea datelor camerei!");
        }
    }

    private void exportToCSV(ActionEvent e) {
        if (currentHotelId <= 0) {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Selectați mai întâi un hotel!");
            return;
        }

        String dateStr = showInputDialog(
                "Introduceți data pentru afișarea camerelor disponibile (yyyy-MM-dd):",
                LocalDate.now().format(dateFormatter));

        if (dateStr == null) {
            return;
        }

        LocalDateTime selectedDateTime;
        try {
            LocalDate selectedDate = LocalDate.parse(dateStr, dateFormatter);
            selectedDateTime = selectedDate.atStartOfDay();
        } catch (DateTimeParseException ex) {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Format de dată invalid! Folosiți formatul yyyy-MM-dd.");
            return;
        }

        Object[][] availableRooms = cameraDAO.filterByAvailabilityAsObjects(
                currentHotelId,
                selectedDateTime,
                selectedDateTime.plusDays(1));

        if (availableRooms.length == 0) {
            showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "Nu există camere disponibile pentru data selectată.");
            return;
        }

        String[] headers = {"ID", "Număr Cameră", "Preț per Noapte"};
        boolean success = ExportUtil.exportToCSV(
                availableRooms,
                headers,
                currentHotelName,
                LocalDate.parse(dateStr, dateFormatter).format(dateFormatter),
                "camere_disponibile");

        if (success) {
            showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "Export în CSV realizat cu succes!");
        } else {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Export în CSV anulat sau eșuat.");
        }
    }

    private void exportToDOC(ActionEvent e) {
        if (currentHotelId <= 0) {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Selectați mai întâi un hotel!");
            return;
        }

        String dateStr = showInputDialog(
                "Introduceți data pentru afișarea camerelor rezervate (yyyy-MM-dd):",
                LocalDate.now().format(dateFormatter));

        if (dateStr == null) {
            return;
        }

        LocalDateTime selectedDateTime;
        try {
            LocalDate selectedDate = LocalDate.parse(dateStr, dateFormatter);
            selectedDateTime = selectedDate.atStartOfDay();
        } catch (DateTimeParseException ex) {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Format de dată invalid! Folosiți formatul yyyy-MM-dd.");
            return;
        }

        Object[][] rezervari = rezervareDAO.findReservationsByHotelAndDateAsObjects(
                currentHotelId,
                selectedDateTime);

        if (rezervari.length == 0) {
            showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "Nu există rezervări pentru data selectată.");
            return;
        }

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
            showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "Export în DOC realizat cu succes!");
        } else {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Export în DOC anulat sau eșuat.");
        }
    }

    private void backToHotels(ActionEvent e) {
        mainView.showPanel("hotel");
        currentCameraId = -1;
    }

    private void applyFilter(ActionEvent e) {
        if (currentHotelId <= 0) {
            showMessageDialog(JOptionPane.ERROR_MESSAGE, "Selectați mai întâi un hotel!");
            return;
        }

        Object[] formValues = view.getFormData();
        String filterType = (String) formValues[2];
        String filterValue = (String) formValues[3];

        lastFilterType = filterType;

        Object[][] filteredRooms = null;

        switch (filterType) {
            case "Toate camerele":
                filteredRooms = cameraDAO.getCamereAsObjects(currentHotelId);
                break;

            case "Disponibilitate":
                try {
                    if (filterValue == null || filterValue.trim().isEmpty()) {
                        showMessageDialog(JOptionPane.ERROR_MESSAGE, "Introduceți o dată validă!");
                        return;
                    }

                    LocalDate date = LocalDate.parse(filterValue, dateFormatter);
                    LocalDateTime dateTime = date.atStartOfDay();

                    filteredRooms = cameraDAO.filterByAvailabilityAsObjects(
                            currentHotelId, dateTime, dateTime.plusDays(1));
                } catch (DateTimeParseException ex) {
                    showMessageDialog(JOptionPane.ERROR_MESSAGE, "Format dată invalid! Folosiți formatul yyyy-MM-dd.");
                    return;
                }
                break;

            case "Preț minim":
                try {
                    if (filterValue == null || filterValue.trim().isEmpty()) {
                        showMessageDialog(JOptionPane.ERROR_MESSAGE, "Introduceți un preț minim valid!");
                        return;
                    }

                    filterValue = filterValue.replace(",", ".");

                    float minPrice = Float.parseFloat(filterValue);
                    if (minPrice < 0) {
                        showMessageDialog(JOptionPane.ERROR_MESSAGE, "Prețul minim nu poate fi negativ!");
                        return;
                    }

                    filteredRooms = cameraDAO.filterByPriceRangeAsObjects(
                            currentHotelId, minPrice, Float.MAX_VALUE);
                } catch (NumberFormatException ex) {
                    showMessageDialog(JOptionPane.ERROR_MESSAGE, "Valoare preț invalid!");
                    return;
                }
                break;

            case "Preț maxim":
                try {
                    if (filterValue == null || filterValue.trim().isEmpty()) {
                        showMessageDialog(JOptionPane.ERROR_MESSAGE, "Introduceți un preț maxim valid!");
                        return;
                    }

                    filterValue = filterValue.replace(",", ".");

                    float maxPrice = Float.parseFloat(filterValue);
                    if (maxPrice <= 0) {
                        showMessageDialog(JOptionPane.ERROR_MESSAGE, "Prețul maxim trebuie să fie pozitiv!");
                        return;
                    }

                    filteredRooms = cameraDAO.filterByPriceRangeAsObjects(
                            currentHotelId, 0, maxPrice);
                } catch (NumberFormatException ex) {
                    showMessageDialog(JOptionPane.ERROR_MESSAGE, "Valoare preț invalid!");
                    return;
                }
                break;

            case "Facilități":
                if (filterValue == null || filterValue.trim().isEmpty()) {
                    showMessageDialog(JOptionPane.ERROR_MESSAGE, "Introduceți un criteriu de căutare pentru facilități!");
                    return;
                }
                filteredRooms = cameraDAO.filterByFacilitiesAsObjects(currentHotelId, filterValue);
                break;
        }

        if (filteredRooms != null) {
            if (filteredRooms.length == 0) {
                showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "Nu există camere care să corespundă criteriilor de filtrare.");
            } else {
                view.updateComponents(null, filteredRooms, null);
                if (!filterType.equals("Toate camerele")) {
                    showMessageDialog(JOptionPane.INFORMATION_MESSAGE,
                            "Filtrare aplicată cu succes. " + filteredRooms.length + " camere găsite.");
                }
            }
        }
    }
}