package org.example.presenter;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;

public interface HotelGUI {
     void setButtonsEnabled(boolean add, boolean edit, boolean delete, boolean viewCameras);
     void addTableSelectionListener(ListSelectionListener listener);
     int getSelectedRow();
     Object getValueAt(int row, int column);
     void populateHotelTable(Object[][] hotels);
     void removeAllLocatieItems();
     void removeAllLantItems();
     void addLocatieItem(String item);
     void addLantItem(String item);
     void updateFilterChainComboBox();
     void setLocatieSelectedIndex(int index);
     void setLantSelectedIndex(int index);
     int getLocatieSelectedIndex();
     int getLantSelectedIndex();
     String getLocatieSelectedItem();
     String getLantSelectedItem();
     String getSelectedFilterChain();
     String getName();
     String getPhone();
     String getEmail();
     String getFacilities();
     void setName(String name);
     void setPhone(String phone);
     void setEmail(String email);
     void setFacilities(String facilities);
     void clearForm();
     void addAddButtonListener(ActionListener listener);
     void addEditButtonListener(ActionListener listener);
     void addDeleteButtonListener(ActionListener listener);
     void addViewCamerasButtonListener(ActionListener listener);
     void addBackButtonListener(ActionListener listener);
     void addFilterByChainButtonListener(ActionListener listener);
     void showMessage(String message);
     int showConfirmDialog(String message);
}
