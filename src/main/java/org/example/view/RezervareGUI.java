package org.example.view;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.Map;

public interface RezervareGUI {
    void updateComponents(String cameraInfo, Object[][] tableData, boolean[] buttonStates);

    Object[] getFormData();

    void setFormData(Object[] formData);

    Object[] getTableSelection();

    void registerEventHandlers(ListSelectionListener tableListener, Map<String, ActionListener> actionListeners);

    void displayDialog(int dialogType, String message, Object initialValue);
}