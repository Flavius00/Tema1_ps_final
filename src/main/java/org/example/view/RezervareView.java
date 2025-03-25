package org.example.view;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class RezervareView extends JPanel implements RezervareGUI {
    private JTable rezervareTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, backButton;
    private JTextField startDateField, endDateField, numeClientField, prenumeClientField, telefonClientField, emailClientField;
    private JLabel cameraInfoLabel;
    private JPanel formPanel;
    private JPanel buttonPanel;

    public RezervareView() {
        setLayout(new BorderLayout());

        // Create camera info panel
        JPanel cameraInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cameraInfoPanel.add(new JLabel("Camera: "));
        cameraInfoLabel = new JLabel();
        cameraInfoPanel.add(cameraInfoLabel);
        add(cameraInfoPanel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = {"ID", "Data de început", "Data de sfârșit", "Nume Client", "Telefon Client"};
        tableModel = new DefaultTableModel(columnNames, 0);
        rezervareTable = new JTable(tableModel);
        rezervareTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(rezervareTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create form panel
        formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Modificat să afișeze noul format de dată
        formPanel.add(new JLabel("Data de început (yyyy-MM-dd):"));
        startDateField = new JTextField(20);
        formPanel.add(startDateField);

        formPanel.add(new JLabel("Data de sfârșit (yyyy-MM-dd):"));
        endDateField = new JTextField(20);
        formPanel.add(endDateField);

        formPanel.add(new JLabel("Nume client:"));
        numeClientField = new JTextField(20);
        formPanel.add(numeClientField);

        formPanel.add(new JLabel("Prenume client:"));
        prenumeClientField = new JTextField(20);
        formPanel.add(prenumeClientField);

        formPanel.add(new JLabel("Telefon client:"));
        telefonClientField = new JTextField(20);
        formPanel.add(telefonClientField);

        formPanel.add(new JLabel("Email client:"));
        emailClientField = new JTextField(20);
        formPanel.add(emailClientField);

        // Create button panel
        buttonPanel = new JPanel(new FlowLayout());

        addButton = new JButton("Adaugă");
        editButton = new JButton("Modifică");
        deleteButton = new JButton("Șterge");
        backButton = new JButton("Înapoi");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    // Methods without logic

    public void setButtonsEnabled(boolean add, boolean edit, boolean delete) {
        addButton.setEnabled(add);
        editButton.setEnabled(edit);
        deleteButton.setEnabled(delete);
    }

    public void addTableSelectionListener(ListSelectionListener listener) {
        rezervareTable.getSelectionModel().addListSelectionListener(listener);
    }

    public void setCameraInfo(String cameraInfo) {
        cameraInfoLabel.setText(cameraInfo);
    }

    public int getSelectedRow() {
        return rezervareTable.getSelectedRow();
    }

    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(row, column);
    }

    public void populateRezervareTable(Object[][] rezervari) {
        tableModel.setRowCount(0);
        for (Object[] rezervare : rezervari) {
            tableModel.addRow(rezervare);
        }
    }

    // Getters for all form fields
    public String getStartDate() {
        return startDateField.getText();
    }

    public String getEndDate() {
        return endDateField.getText();
    }

    public String getNumeClient() {
        return numeClientField.getText();
    }

    public String getPrenumeClient() {
        return prenumeClientField.getText();
    }

    public String getTelefonClient() {
        return telefonClientField.getText();
    }

    public String getEmailClient() {
        return emailClientField.getText();
    }

    // Setters for all form fields
    public void setStartDate(String startDate) {
        startDateField.setText(startDate);
    }

    public void setEndDate(String endDate) {
        endDateField.setText(endDate);
    }

    public void setNumeClient(String numeClient) {
        numeClientField.setText(numeClient);
    }

    public void setPrenumeClient(String prenumeClient) {
        prenumeClientField.setText(prenumeClient);
    }

    public void setTelefonClient(String telefonClient) {
        telefonClientField.setText(telefonClient);
    }

    public void setEmailClient(String emailClient) {
        emailClientField.setText(emailClient);
    }

    public void clearForm() {
        startDateField.setText("");
        endDateField.setText("");
        numeClientField.setText("");
        prenumeClientField.setText("");
        telefonClientField.setText("");
        emailClientField.setText("");
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

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
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