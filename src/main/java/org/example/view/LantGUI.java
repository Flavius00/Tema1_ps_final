package org.example.view;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.Map;

public interface LantGUI {
     void updateComponents(Object[][] tableData, boolean showForm, String nume);

     String getNume();

     Object[] getTableSelection();

     void registerEventHandlers(Map<String, ActionListener> actionListeners);

     void displayDialog(int dialogType, String message, Object initialValue);

     void setVisible(boolean visible);
}
