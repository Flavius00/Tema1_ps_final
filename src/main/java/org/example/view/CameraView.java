package org.example.view;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class CameraView extends JPanel {
    private JTable cameraTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, viewReservationsButton, backButton;
    private JButton exportCSVButton, exportDOCButton;
    // Filter controls
    private JComboBox<String> filterTypeComboBox;
    private JTextField filterValueField;
    private JButton applyFilterButton;
    private JTextField roomNumberField, priceField;
    private JLabel hotelNameLabel;
    private JPanel formPanel;
    private JPanel buttonPanel;

    public CameraView() {
        setLayout(new BorderLayout());

        // Create hotel info panel
        JPanel hotelInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hotelInfoPanel.add(new JLabel("Hotel: "));
        hotelNameLabel = new JLabel();
        hotelInfoPanel.add(hotelNameLabel);
        add(hotelInfoPanel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = {"ID", "Număr Cameră", "Preț per Noapte"};
        tableModel = new DefaultTableModel(columnNames, 0);
        cameraTable = new JTable(tableModel);
        cameraTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(cameraTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create form panel
        formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Număr Cameră:"));
        roomNumberField = new JTextField(20);
        formPanel.add(roomNumberField);

        formPanel.add(new JLabel("Preț per Noapte:"));
        priceField = new JTextField(20);
        formPanel.add(priceField);

        // Create filter panel
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

        // Create button panel
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

        // Create south panel with multiple components
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(filterPanel, BorderLayout.NORTH);
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    // Methods for presenter to control UI state

    public void setButtonsEnabled(boolean add, boolean edit, boolean delete, boolean viewReservations,
                                  boolean export) {
        editButton.setEnabled(edit);
        deleteButton.setEnabled(delete);
        viewReservationsButton.setEnabled(viewReservations);
        addButton.setEnabled(add);
        exportCSVButton.setEnabled(export);
        exportDOCButton.setEnabled(export);
    }

    public void setHotelName(String hotelName) {
        hotelNameLabel.setText(hotelName);
    }

    public void populateCameraTable(Object[][] cameras) {
        tableModel.setRowCount(0);
        for (Object[] camera : cameras) {
            tableModel.addRow(camera);
        }
    }

    public String getRoomNumber() {
        return roomNumberField.getText();
    }

    public String getPrice() {
        return priceField.getText();
    }

    public void clearForm() {
        roomNumberField.setText("");
        priceField.setText("");
    }

    public void setRoomNumber(String roomNumber) {
        roomNumberField.setText(roomNumber);
    }

    public void setPrice(String price) {
        priceField.setText(price);
    }

    public int getSelectedRow() {
        return cameraTable.getSelectedRow();
    }

    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(row, column);
    }

    public void addTableSelectionListener(ListSelectionListener listener) {
        cameraTable.getSelectionModel().addListSelectionListener(listener);
    }

    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addEditButtonListener(ActionListener listener) {
        editButton.addActionListener(listener);
    }

    public void addDeleteButtonListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }

    public void addViewReservationsButtonListener(ActionListener listener) {
        viewReservationsButton.addActionListener(listener);
    }

    public void addExportCSVButtonListener(ActionListener listener) {
        exportCSVButton.addActionListener(listener);
    }

    public void addExportDOCButtonListener(ActionListener listener) {
        exportDOCButton.addActionListener(listener);
    }

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    // Filter methods
    public void addApplyFilterButtonListener(ActionListener listener) {
        applyFilterButton.addActionListener(listener);
    }

    public String getFilterType() {
        return (String) filterTypeComboBox.getSelectedItem();
    }

    public String getFilterValue() {
        return filterValueField.getText();
    }

    public void clearFilterField() {
        filterValueField.setText("");
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirmare",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
    }

    public String showInputDialog(String message, String initialValue) {
        return JOptionPane.showInputDialog(this, message, initialValue);
    }
}