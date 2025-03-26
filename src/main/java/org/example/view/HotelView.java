package org.example.view;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class HotelView extends JPanel implements HotelGUI {
    private JTable hotelTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, viewCamerasButton, backButton;
    private JTextField nameField, phoneField, emailField, facilitiesField;
    private JComboBox<String> lantComboBox;
    private JComboBox<String> locatieComboBox;
    private JComboBox<String> filterChainComboBox;
    private JButton filterByChainButton;
    private JPanel formPanel;
    private JPanel buttonPanel;

    private Map<String, JButton> buttonMap;

    public HotelView() {
        setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtrare după lanț:"));
        filterChainComboBox = new JComboBox<>();
        filterChainComboBox.addItem("Toate lanțurile");
        filterPanel.add(filterChainComboBox);
        filterByChainButton = new JButton("Aplică");
        filterPanel.add(filterByChainButton);

        add(filterPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Nume", "Locatie", "Telefon", "Email", "Facilitati", "Lant"};
        tableModel = new DefaultTableModel(columnNames, 0);
        hotelTable = new JTable(tableModel);
        hotelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(hotelTable);
        add(scrollPane, BorderLayout.CENTER);

        formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nume:"));
        nameField = new JTextField(20);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Locatie:"));
        locatieComboBox = new JComboBox<>();
        formPanel.add(locatieComboBox);

        formPanel.add(new JLabel("Telefon:"));
        phoneField = new JTextField(20);
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        formPanel.add(emailField);

        formPanel.add(new JLabel("Facilitati:"));
        facilitiesField = new JTextField(20);
        formPanel.add(facilitiesField);

        formPanel.add(new JLabel("Lant:"));
        lantComboBox = new JComboBox<>();
        formPanel.add(lantComboBox);

        buttonPanel = new JPanel(new FlowLayout());

        addButton = new JButton("Adauga");
        editButton = new JButton("Modifica");
        deleteButton = new JButton("Sterge");
        viewCamerasButton = new JButton("Vezi Camere");
        backButton = new JButton("Inapoi");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewCamerasButton);
        buttonPanel.add(backButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        buttonMap = new HashMap<>();
        buttonMap.put("add", addButton);
        buttonMap.put("edit", editButton);
        buttonMap.put("delete", deleteButton);
        buttonMap.put("viewCameras", viewCamerasButton);
        buttonMap.put("back", backButton);
        buttonMap.put("filterByChain", filterByChainButton);
    }

    @Override
    public void updateComponents(Object[][] tableData, Object[][] comboData, boolean[] buttonStates) {
        if (tableData != null) {
            tableModel.setRowCount(0);
            for (Object[] row : tableData) {
                tableModel.addRow(row);
            }
        }

        if (comboData != null) {
            if (comboData.length > 0 && comboData[0] != null) {
                locatieComboBox.removeAllItems();
                for (Object item : comboData[0]) {
                    if (item != null) {
                        locatieComboBox.addItem(item.toString());
                    }
                }
            }

            if (comboData.length > 1 && comboData[1] != null) {
                lantComboBox.removeAllItems();
                filterChainComboBox.removeAllItems();
                filterChainComboBox.addItem("Toate lanțurile");

                for (Object item : comboData[1]) {
                    if (item != null) {
                        String itemStr = item.toString();
                        lantComboBox.addItem(itemStr);
                        if (!itemStr.equals("Selectați un lanț")) {
                            filterChainComboBox.addItem(itemStr);
                        }
                    }
                }
            }
        }

        if (buttonStates != null && buttonStates.length >= 4) {
            addButton.setEnabled(buttonStates[0]);
            editButton.setEnabled(buttonStates[1]);
            deleteButton.setEnabled(buttonStates[2]);
            viewCamerasButton.setEnabled(buttonStates[3]);
        }
    }

    @Override
    public Object[] getFormData() {
        return new Object[] {
                nameField.getText(),
                phoneField.getText(),
                emailField.getText(),
                facilitiesField.getText(),
                locatieComboBox.getSelectedItem(),
                lantComboBox.getSelectedItem(),
                filterChainComboBox.getSelectedItem()
        };
    }

    @Override
    public void setFormData(Object[] formData) {
        if (formData == null) return;

        if (formData.length > 0 && formData[0] != null) nameField.setText(formData[0].toString());
        if (formData.length > 1 && formData[1] != null) phoneField.setText(formData[1].toString());
        if (formData.length > 2 && formData[2] != null) emailField.setText(formData[2].toString());
        if (formData.length > 3 && formData[3] != null) facilitiesField.setText(formData[3].toString());

        if (formData.length > 4 && formData[4] instanceof Integer) {
            int index = (Integer) formData[4];
            if (index >= 0 && index < locatieComboBox.getItemCount()) {
                locatieComboBox.setSelectedIndex(index);
            }
        }

        if (formData.length > 5 && formData[5] instanceof Integer) {
            int index = (Integer) formData[5];
            if (index >= 0 && index < lantComboBox.getItemCount()) {
                lantComboBox.setSelectedIndex(index);
            }
        }

        if (formData.length > 6 && formData[6] instanceof Boolean && (Boolean)formData[6]) {
            nameField.setText("");
            phoneField.setText("");
            emailField.setText("");
            facilitiesField.setText("");
            if (locatieComboBox.getItemCount() > 0) locatieComboBox.setSelectedIndex(0);
            if (lantComboBox.getItemCount() > 0) lantComboBox.setSelectedIndex(0);
        }
    }

    @Override
    public Object[] getTableSelection() {
        int selectedRow = hotelTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }

        Object[] rowData = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < rowData.length; i++) {
            rowData[i] = tableModel.getValueAt(selectedRow, i);
        }

        return rowData;
    }

    @Override
    public void registerEventHandlers(ListSelectionListener tableListener, Map<String, ActionListener> actionListeners) {
        hotelTable.getSelectionModel().addListSelectionListener(tableListener);

        if (actionListeners != null) {
            for (Map.Entry<String, ActionListener> entry : actionListeners.entrySet()) {
                JButton button = buttonMap.get(entry.getKey());
                if (button != null) {
                    button.addActionListener(entry.getValue());
                }
            }
        }
    }

    @Override
    public void displayDialog(int dialogType, String message, Object initialValue) {
        if (dialogType == JOptionPane.QUESTION_MESSAGE) {
            JOptionPane.showConfirmDialog(this, message, "Confirmare", JOptionPane.YES_NO_OPTION, dialogType);
        } else {
            JOptionPane.showMessageDialog(this, message);
        }
    }
}