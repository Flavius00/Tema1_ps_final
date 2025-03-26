package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class MainView extends JFrame {
    private JButton btnLanturi;
    private JButton btnHoteluri;
    private JButton btnCamere;
    private JButton btnRezervari;

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private Map<String, JPanel> panels;
    private JPanel mainPanel;

    public MainView() {
        initComponents();

        panels = new HashMap<>();
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        panels.put("main", mainPanel);
        cardPanel.add(mainPanel, "main");

        setContentPane(cardPanel);
    }

    private void initComponents() {
        setTitle("Sistem de Management Hoteluri");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        btnLanturi = new JButton("Gestiune Lanturi Hoteliere");
        btnLanturi.setFont(new Font("Arial", Font.BOLD, 14));

        btnHoteluri = new JButton("Gestiune Hoteluri");
        btnHoteluri.setFont(new Font("Arial", Font.BOLD, 14));

        btnCamere = new JButton("Gestiune Camere");
        btnCamere.setFont(new Font("Arial", Font.BOLD, 14));

        btnRezervari = new JButton("Gestiune Rezervari");
        btnRezervari.setFont(new Font("Arial", Font.BOLD, 14));

        mainPanel.add(btnLanturi);
        mainPanel.add(btnHoteluri);
        mainPanel.add(btnCamere);
        mainPanel.add(btnRezervari);
    }


    public void addPanel(String name, JPanel panel) {
        panels.put(name, panel);
        cardPanel.add(panel, name);
    }


    public void showPanel(String name) {
        if (panels.containsKey(name)) {
            cardLayout.show(cardPanel, name);
        } else {
            showMessage("Panel-ul '" + name + "' nu există!");
        }
    }

    public void setLanturiButtonListener(ActionListener listener) {
        btnLanturi.addActionListener(listener);
    }

    public void setHoteluriButtonListener(ActionListener listener) {
        btnHoteluri.addActionListener(listener);
    }

    public void setCamereButtonListener(ActionListener listener) {
        btnCamere.addActionListener(listener);
    }

    public void setRezervariButtonListener(ActionListener listener) {
        btnRezervari.addActionListener(listener);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}