package org.example.view;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.Map;

public interface HotelGUI {
     void updateComponents(Object[][] tableData, Object[][] comboData, boolean[] buttonStates);

     Object[] getFormData();
     void setFormData(Object[] formData);

     Object[] getTableSelection();

     void registerEventHandlers(ListSelectionListener tableListener, Map<String, ActionListener> actionListeners);

     void displayDialog(int dialogType, String message, Object initialValue);
}
