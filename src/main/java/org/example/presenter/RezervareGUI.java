package org.example.presenter;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;

 public interface RezervareGUI {
     void setButtonsEnabled(boolean add, boolean edit, boolean delete);
     void addTableSelectionListener(ListSelectionListener listener);
     void setCameraInfo(String cameraInfo);
     int getSelectedRow();
     Object getValueAt(int row, int column);
     void populateRezervareTable(Object[][] rezervari);
     String getStartDate();
     String getEndDate();
     String getNumeClient();
     String getPrenumeClient();
     String getTelefonClient();
     String getEmailClient();
     void setStartDate(String startDate);
     void setEndDate(String endDate);
     void setNumeClient(String numeClient);
     void setPrenumeClient(String prenumeClient);
     void setTelefonClient(String telefonClient);
     void setEmailClient(String emailClient);
     void clearForm();
     void addAddButtonListener(ActionListener listener);
     void addEditButtonListener(ActionListener listener);
     void addDeleteButtonListener(ActionListener listener);
     void addBackButtonListener(ActionListener listener);
     void showMessage(String message);
     int showConfirmDialog(String message);
}
