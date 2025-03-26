package org.example;

import org.example.model.dao.*;
import org.example.model.database.DatabaseConnection;
import org.example.presenter.*;
import org.example.view.*;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        MainView mainView = new MainView();

        LantDAO lantDAO = new LantDAO();
        LocatieDAO locatieDAO = new LocatieDAO();
        HotelDAO hotelDAO = new HotelDAO();
        CameraDAO cameraDAO = new CameraDAO();
        RezervareDAO rezervareDAO = new RezervareDAO();

        LantGUI lantView = new LantView();
        HotelGUI hotelView = new HotelView();
        CameraGUI cameraView = new CameraView();
        RezervareGUI rezervareView = new RezervareView();

        LantPresenter lantPresenter = new LantPresenter(lantView, lantDAO, mainView);
        HotelPresenter hotelPresenter = new HotelPresenter(hotelView, hotelDAO, locatieDAO, lantDAO, mainView);
        CameraPresenter cameraPresenter = new CameraPresenter(cameraView, cameraDAO, rezervareDAO, mainView);
        RezervarePresenter rezervarePresenter = new RezervarePresenter(rezervareView, rezervareDAO, mainView);

        cameraPresenter.setRezervarePresenter(rezervarePresenter);
        hotelPresenter.setCameraPresenter(cameraPresenter);
        lantPresenter.setHotelPresenter(hotelPresenter);

        mainView.setLanturiButtonListener(e -> lantView.setVisible(true));
        mainView.setHoteluriButtonListener(e -> mainView.showPanel("hotel"));
        mainView.setCamereButtonListener(e -> mainView.showPanel("camera"));
        mainView.setRezervariButtonListener(e -> mainView.showPanel("rezervare"));

        mainView.addPanel("hotel", (JPanel) hotelView);
        mainView.addPanel("camera", (JPanel) cameraView);
        mainView.addPanel("rezervare", (JPanel) rezervareView);

        mainView.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConnection::closeConnection));
    }
}