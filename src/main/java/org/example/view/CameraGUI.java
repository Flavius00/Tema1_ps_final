package org.example.view;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;

public interface CameraGUI {
    void setButtonsEnabled(boolean add, boolean edit, boolean delete, boolean viewReservations, boolean export);
    void setHotelName(String hotelName);
    void populateCameraTable(Object[][] cameras);
    String getRoomNumber();
    String getPrice();
    void clearForm();
    void setRoomNumber(String roomNumber);
    void setPrice(String price);
    int getSelectedRow();
    Object getValueAt(int row, int column);
    void addTableSelectionListener(ListSelectionListener listener);
    void addAddButtonListener(ActionListener listener);
    void addEditButtonListener(ActionListener listener);
    void addDeleteButtonListener(ActionListener listener);
    void addViewReservationsButtonListener(ActionListener listener);
    void addExportCSVButtonListener(ActionListener listener);
    void addExportDOCButtonListener(ActionListener listener);
    void addBackButtonListener(ActionListener listener);
    void addApplyFilterButtonListener(ActionListener listener);
    String getFilterType();
    String getFilterValue();
    void clearFilterField();
    void showMessage(String message);
    int showConfirmDialog(String message);
    String showInputDialog(String message, String initialValue);
}
