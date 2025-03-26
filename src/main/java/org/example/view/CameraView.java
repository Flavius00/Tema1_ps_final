package org.example.view;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class CameraView extends JPanel implements CameraGUI {
    private JTable cameraTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, viewReservationsButton, backButton;
    private JButton exportCSVButton, exportDOCButton;
    private JComboBox<String> filterTypeComboBox;
    private JTextField filterValueField;
    private JButton applyFilterButton;
    private JTextField roomNumberField, priceField;
    private JLabel hotelNameLabel;
    private JPanel formPanel;
    private JPanel buttonPanel;

    private Map<String, JButton> buttonMap;

    public CameraView() {
        setLayout(new BorderLayout());

        JPanel hotelInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hotelInfoPanel.add(new JLabel("Hotel: "));
        hotelNameLabel = new JLabel();
        hotelInfoPanel.add(hotelNameLabel);
        add(hotelInfoPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Număr Cameră", "Preț per Noapte"};
        tableModel = new DefaultTableModel(columnNames, 0);
        cameraTable = new JTable(tableModel);
        cameraTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(cameraTable);
        add(scrollPane, BorderLayout.CENTER);

        formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Număr Cameră:"));
        roomNumberField = new JTextField(20);
        formPanel.add(roomNumberField);

        formPanel.add(new JLabel("Preț per Noapte:"));
        priceField = new JTextField(20);
        formPanel.add(priceField);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel filtrareLabel = new JLabel("Filtrare:");
        filterTypeComboBox = new JComboBox<>(new String[]{
                "Toate camerele", "Disponibilitate", "Preț minim", "Preț maxim", "Facilități"
        });
        filterValueField = new JTextField(15);
        applyFilterButton = new JButton("Aplică");

        filterPanel.add(filtrareLabel);
        filterPanel.add(filterTypeComboBox);
        filterPanel.add(filterValueField);
        filterPanel.add(applyFilterButton);

        buttonPanel = new JPanel(new FlowLayout());

        addButton = new JButton("Adaugă");
        editButton = new JButton("Modifică");
        deleteButton = new JButton("Șterge");
        viewReservationsButton = new JButton("Vezi Rezervări");
        backButton = new JButton("Înapoi");
        exportCSVButton = new JButton("Export CSV");
        exportDOCButton = new JButton("Export DOC");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewReservationsButton);
        buttonPanel.add(exportCSVButton);
        buttonPanel.add(exportDOCButton);
        buttonPanel.add(backButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(filterPanel, BorderLayout.NORTH);
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        buttonMap = new HashMap<>();
        buttonMap.put("add", addButton);
        buttonMap.put("edit", editButton);
        buttonMap.put("delete", deleteButton);
        buttonMap.put("viewReservations", viewReservationsButton);
        buttonMap.put("back", backButton);
        buttonMap.put("exportCSV", exportCSVButton);
        buttonMap.put("exportDOC", exportDOCButton);
        buttonMap.put("applyFilter", applyFilterButton);
    }

    @Override
    public void updateComponents(String hotelName, Object[][] tableData, boolean[] buttonStates) {
        if (hotelName != null) {
            hotelNameLabel.setText(hotelName);
        }

        if (tableData != null) {
            tableModel.setRowCount(0);
            for (Object[] row : tableData) {
                tableModel.addRow(row);
            }
        }

        if (buttonStates != null && buttonStates.length >= 5) {
            addButton.setEnabled(buttonStates[0]);
            editButton.setEnabled(buttonStates[1]);
            deleteButton.setEnabled(buttonStates[2]);
            viewReservationsButton.setEnabled(buttonStates[3]);
            exportCSVButton.setEnabled(buttonStates[4]);
            exportDOCButton.setEnabled(buttonStates[4]);
        }
    }

    @Override
    public Object[] getFormData() {
        return new Object[] {
                roomNumberField.getText(),
                priceField.getText(),
                filterTypeComboBox.getSelectedItem(),
                filterValueField.getText()
        };
    }

    @Override
    public void setFormData(Object[] formData) {
        if (formData == null) return;

        if (formData.length > 0 && formData[0] != null) {
            roomNumberField.setText(formData[0].toString());
        }

        if (formData.length > 1 && formData[1] != null) {
            priceField.setText(formData[1].toString());
        }

        if (formData.length > 2 && formData[2] instanceof Boolean && (Boolean)formData[2]) {
            roomNumberField.setText("");
            priceField.setText("");
            filterValueField.setText("");
        }
    }

    @Override
    public Object[] getTableSelection() {
        int selectedRow = cameraTable.getSelectedRow();
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
        cameraTable.getSelectionModel().addListSelectionListener(tableListener);

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
        } else if (dialogType == JOptionPane.PLAIN_MESSAGE && initialValue != null) {
            JOptionPane.showInputDialog(this, message, initialValue);
        } else {
            JOptionPane.showMessageDialog(this, message, "Mesaj", dialogType);
        }
    }
}