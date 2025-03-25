package org.example.view;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class HotelView extends JPanel implements HotelGUI {
    private JTable hotelTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, viewCamerasButton, backButton;
    private JTextField nameField, phoneField, emailField, facilitiesField;
    private JComboBox<String> lantComboBox;
    private JComboBox<String> locatieComboBox;
    // Filtru pentru lant
    private JComboBox<String> filterChainComboBox;
    private JButton filterByChainButton;
    private JPanel formPanel;
    private JPanel buttonPanel;

    public HotelView() {
        setLayout(new BorderLayout());

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtrare după lanț:"));
        filterChainComboBox = new JComboBox<>();
        filterChainComboBox.addItem("Toate lanțurile");
        filterPanel.add(filterChainComboBox);
        filterByChainButton = new JButton("Aplică");
        filterPanel.add(filterByChainButton);

        add(filterPanel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = {"ID", "Nume", "Locatie", "Telefon", "Email", "Facilitati", "Lant"};
        tableModel = new DefaultTableModel(columnNames, 0);
        hotelTable = new JTable(tableModel);
        hotelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(hotelTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create form panel
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

        // Create button panel
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
    }

    // Simple accessor and mutator methods without logic

    public void setButtonsEnabled(boolean add, boolean edit, boolean delete, boolean viewCameras) {
        addButton.setEnabled(add);
        editButton.setEnabled(edit);
        deleteButton.setEnabled(delete);
        viewCamerasButton.setEnabled(viewCameras);
    }

    public void addTableSelectionListener(ListSelectionListener listener) {
        hotelTable.getSelectionModel().addListSelectionListener(listener);
    }

    public int getSelectedRow() {
        return hotelTable.getSelectedRow();
    }

    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(row, column);
    }

    public void populateHotelTable(Object[][] hotels) {
        tableModel.setRowCount(0);
        for (Object[] hotel : hotels) {
            tableModel.addRow(hotel);
        }
    }

    public void removeAllLocatieItems() {
        locatieComboBox.removeAllItems();
    }

    public void removeAllLantItems() {
        lantComboBox.removeAllItems();
    }

    public void addLocatieItem(String item) {
        locatieComboBox.addItem(item);
    }

    public void addLantItem(String item) {
        lantComboBox.addItem(item);

        // Also add to filter combo box (except the "Selectați un lanț" item)
        if (!item.equals("Selectați un lanț")) {
            // Check if it already exists
            boolean exists = false;
            for (int i = 0; i < filterChainComboBox.getItemCount(); i++) {
                if (filterChainComboBox.getItemAt(i).equals(item)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                filterChainComboBox.addItem(item);
            }
        }
    }

    // Method to update filter chain combo box from lant combo box
    public void updateFilterChainComboBox() {
        // First clear all except "Toate lanțurile"
        while (filterChainComboBox.getItemCount() > 1) {
            filterChainComboBox.removeItemAt(1);
        }

        // Then add all items from lantComboBox except "Selectați un lanț"
        for (int i = 1; i < lantComboBox.getItemCount(); i++) {
            String item = lantComboBox.getItemAt(i);
            filterChainComboBox.addItem(item);
        }
    }

    public void setLocatieSelectedIndex(int index) {
        locatieComboBox.setSelectedIndex(index);
    }

    public void setLantSelectedIndex(int index) {
        lantComboBox.setSelectedIndex(index);
    }

    public int getLocatieSelectedIndex() {
        return locatieComboBox.getItemCount();
    }

    public int getLantSelectedIndex() {
        return lantComboBox.getItemCount();
    }

    public String getLocatieSelectedItem() {
        Object item = locatieComboBox.getSelectedItem();
        return item != null ? item.toString() : "";
    }

    public String getLantSelectedItem() {
        Object item = lantComboBox.getSelectedItem();
        return item != null ? item.toString() : "";
    }

    public String getSelectedFilterChain() {
        return (String) filterChainComboBox.getSelectedItem();
    }

    // Form field getters
    public String getName() {
        return nameField.getText();
    }

    public String getPhone() {
        return phoneField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getFacilities() {
        return facilitiesField.getText();
    }

    // Form field setters
    public void setName(String name) {
        nameField.setText(name);
    }

    public void setPhone(String phone) {
        phoneField.setText(phone);
    }

    public void setEmail(String email) {
        emailField.setText(email);
    }

    public void setFacilities(String facilities) {
        facilitiesField.setText(facilities);
    }

    public void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        facilitiesField.setText("");
        locatieComboBox.setSelectedIndex(0);
        lantComboBox.setSelectedIndex(0);
    }

    // Action listeners
    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addEditButtonListener(ActionListener listener) {
        editButton.addActionListener(listener);
    }

    public void addDeleteButtonListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }

    public void addViewCamerasButtonListener(ActionListener listener) {
        viewCamerasButton.addActionListener(listener);
    }

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public void addFilterByChainButtonListener(ActionListener listener) {
        filterByChainButton.addActionListener(listener);
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
}