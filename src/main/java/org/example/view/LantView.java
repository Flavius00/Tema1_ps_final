package org.example.view;

import org.example.presenter.LantGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class LantView extends JFrame implements LantGUI {
    private JTable tableLanturi;
    private DefaultTableModel tableModel;
    private JButton btnAdauga;
    private JButton btnEditare;
    private JButton btnStergere;
    private JButton btnInapoi;

    // Pentru operații de adăugare/editare
    private JTextField txtNume;
    private JButton btnSalveaza;
    private JButton btnAnuleaza;
    private JPanel panelForm;
    private JPanel panelButtons;

    public LantView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestiune Lanturi Hoteliere");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Creăm modelul de tabel
        String[] columnNames = {"ID", "Nume"};
        tableModel = new DefaultTableModel(columnNames, 0);

        tableLanturi = new JTable(tableModel);
        tableLanturi.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableLanturi.getColumnModel().getColumn(1).setPreferredWidth(350);

        JScrollPane scrollPane = new JScrollPane(tableLanturi);

        // Creăm panoul pentru butoane
        panelButtons = new JPanel();
        btnAdauga = new JButton("Adaugă");
        btnEditare = new JButton("Editare");
        btnStergere = new JButton("Șterge");
        btnInapoi = new JButton("Înapoi");

        panelButtons.add(btnAdauga);
        panelButtons.add(btnEditare);
        panelButtons.add(btnStergere);
        panelButtons.add(btnInapoi);

        // Creăm formularul pentru adăugare/editare
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

        // Adăugăm componentele la frame
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        // Inițial formularul nu e vizibil
        panelForm.setVisible(false);
    }

    // Methods without logic

    public void afiseazaFormular() {
        remove(panelButtons);
        add(panelForm, BorderLayout.SOUTH);
        panelForm.setVisible(true);
        revalidate();
        repaint();
    }

    public void ascundeFormular() {
        remove(panelForm);
        add(panelButtons, BorderLayout.SOUTH);
        panelForm.setVisible(false);
        revalidate();
        repaint();
        txtNume.setText("");
    }

    public void setNume(String nume) {
        txtNume.setText(nume);
    }

    public String getNume() {
        return txtNume.getText();
    }

    public void setAdaugaButtonListener(ActionListener listener) {
        btnAdauga.addActionListener(listener);
    }

    public void setEditareButtonListener(ActionListener listener) {
        btnEditare.addActionListener(listener);
    }

    public void setStergereButtonListener(ActionListener listener) {
        btnStergere.addActionListener(listener);
    }

    public void setInapoiButtonListener(ActionListener listener) {
        btnInapoi.addActionListener(listener);
    }

    public void setSalveazaButtonListener(ActionListener listener) {
        btnSalveaza.addActionListener(listener);
    }

    public void setAnuleazaButtonListener(ActionListener listener) {
        btnAnuleaza.addActionListener(listener);
    }

    public void updateTable(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    public int getSelectedRow() {
        return tableLanturi.getSelectedRow();
    }

    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(row, column);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirmare", JOptionPane.YES_NO_OPTION);
    }
}