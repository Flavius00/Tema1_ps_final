package org.example.view;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class RezervareView extends JPanel implements RezervareGUI {
    private JTable rezervareTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, backButton;
    private JTextField startDateField, endDateField, numeClientField, prenumeClientField, telefonClientField, emailClientField;
    private JLabel cameraInfoLabel;
    private JPanel formPanel;
    private JPanel buttonPanel;

    private Map<String, JButton> buttonMap;

    public RezervareView() {
        setLayout(new BorderLayout());

        JPanel cameraInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cameraInfoPanel.add(new JLabel("Camera: "));
        cameraInfoLabel = new JLabel();
        cameraInfoPanel.add(cameraInfoLabel);
        add(cameraInfoPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Data de început", "Data de sfârșit", "Nume Client", "Telefon Client"};
        tableModel = new DefaultTableModel(columnNames, 0);
        rezervareTable = new JTable(tableModel);
        rezervareTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(rezervareTable);
        add(scrollPane, BorderLayout.CENTER);

        formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        buttonMap = new HashMap<>();
        buttonMap.put("add", addButton);
        buttonMap.put("edit", editButton);
        buttonMap.put("delete", deleteButton);
        buttonMap.put("back", backButton);
    }

    @Override
    public void updateComponents(String cameraInfo, Object[][] tableData, boolean[] buttonStates) {
        if (cameraInfo != null) {
            cameraInfoLabel.setText(cameraInfo);
        }

        if (tableData != null) {
            tableModel.setRowCount(0);
            for (Object[] row : tableData) {
                tableModel.addRow(row);
            }
        }

        if (buttonStates != null && buttonStates.length >= 3) {
            addButton.setEnabled(buttonStates[0]);
            editButton.setEnabled(buttonStates[1]);
            deleteButton.setEnabled(buttonStates[2]);
        }
    }

    @Override
    public Object[] getFormData() {
        return new Object[] {
                startDateField.getText(),
                endDateField.getText(),
                numeClientField.getText(),
                prenumeClientField.getText(),
                telefonClientField.getText(),
                emailClientField.getText()
        };
    }

    @Override
    public void setFormData(Object[] formData) {
        if (formData == null) return;

        if (formData.length > 0 && formData[0] != null) startDateField.setText(formData[0].toString());
        if (formData.length > 1 && formData[1] != null) endDateField.setText(formData[1].toString());
        if (formData.length > 2 && formData[2] != null) numeClientField.setText(formData[2].toString());
        if (formData.length > 3 && formData[3] != null) prenumeClientField.setText(formData[3].toString());
        if (formData.length > 4 && formData[4] != null) telefonClientField.setText(formData[4].toString());
        if (formData.length > 5 && formData[5] != null) emailClientField.setText(formData[5].toString());

        if (formData.length > 6 && formData[6] instanceof Boolean && (Boolean)formData[6]) {
            startDateField.setText("");
            endDateField.setText("");
            numeClientField.setText("");
            prenumeClientField.setText("");
            telefonClientField.setText("");
            emailClientField.setText("");
        }
    }

    @Override
    public Object[] getTableSelection() {
        int selectedRow = rezervareTable.getSelectedRow();
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
        rezervareTable.getSelectionModel().addListSelectionListener(tableListener);

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
            JOptionPane.showMessageDialog(this, message);
        }
    }
}