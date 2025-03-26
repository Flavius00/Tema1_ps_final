package org.example.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class LantView extends JFrame implements LantGUI {
    private JTable tableLanturi;
    private DefaultTableModel tableModel;
    private JButton btnAdauga;
    private JButton btnEditare;
    private JButton btnStergere;
    private JButton btnInapoi;
    private JTextField txtNume;
    private JButton btnSalveaza;
    private JButton btnAnuleaza;
    private JPanel panelForm;
    private JPanel panelButtons;

    private Map<String, JButton> buttonMap;

    public LantView() {
        initComponents();

        buttonMap = new HashMap<>();
        buttonMap.put("add", btnAdauga);
        buttonMap.put("edit", btnEditare);
        buttonMap.put("delete", btnStergere);
        buttonMap.put("back", btnInapoi);
        buttonMap.put("save", btnSalveaza);
        buttonMap.put("cancel", btnAnuleaza);
    }

    private void initComponents() {
        setTitle("Gestiune Lanturi Hoteliere");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = {"ID", "Nume"};
        tableModel = new DefaultTableModel(columnNames, 0);

        tableLanturi = new JTable(tableModel);
        tableLanturi.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableLanturi.getColumnModel().getColumn(1).setPreferredWidth(350);

        JScrollPane scrollPane = new JScrollPane(tableLanturi);

        panelButtons = new JPanel();
        btnAdauga = new JButton("Adaugă");
        btnEditare = new JButton("Editare");
        btnStergere = new JButton("Șterge");
        btnInapoi = new JButton("Înapoi");

        panelButtons.add(btnAdauga);
        panelButtons.add(btnEditare);
        panelButtons.add(btnStergere);
        panelButtons.add(btnInapoi);

        panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelForm.add(new JLabel("Nume:"));
        txtNume = new JTextField(20);
        panelForm.add(txtNume);

        btnSalveaza = new JButton("Salvează");
        btnAnuleaza = new JButton("Anulează");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnSalveaza);
        btnPanel.add(btnAnuleaza);

        panelForm.add(new JLabel(""));
        panelForm.add(btnPanel);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        panelForm.setVisible(false);
    }

    @Override
    public void updateComponents(Object[][] tableData, boolean showForm, String nume) {
        if (tableData != null) {
            tableModel.setRowCount(0);
            for (Object[] row : tableData) {
                tableModel.addRow(row);
            }
        }

        if (showForm) {
            if (nume != null) {
                txtNume.setText(nume);
            }

            getContentPane().remove(panelButtons);
            getContentPane().add(panelForm, BorderLayout.SOUTH);
            panelForm.setVisible(true);
        } else {
            getContentPane().remove(panelForm);
            getContentPane().add(panelButtons, BorderLayout.SOUTH);
            panelForm.setVisible(false);
            txtNume.setText("");
        }

        revalidate();
        repaint();
    }

    @Override
    public String getNume() {
        return txtNume.getText();
    }

    @Override
    public Object[] getTableSelection() {
        int selectedRow = tableLanturi.getSelectedRow();
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
    public void registerEventHandlers(Map<String, ActionListener> actionListeners) {
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