package org.example.presenter;

import java.awt.event.ActionListener;

public interface LantGUI {
     void afiseazaFormular();
     void ascundeFormular();
     void setNume(String nume);
     String getNume();
     void setAdaugaButtonListener(ActionListener listener);
     void setEditareButtonListener(ActionListener listener);
     void setStergereButtonListener(ActionListener listener);
     void setInapoiButtonListener(ActionListener listener);
     void setSalveazaButtonListener(ActionListener listener);
     void setAnuleazaButtonListener(ActionListener listener);
     void updateTable(Object[][] data);
     int getSelectedRow();
     Object getValueAt(int row, int column);
     void showMessage(String message);
     int showConfirmDialog(String message);
     void setVisible(boolean b);
}
