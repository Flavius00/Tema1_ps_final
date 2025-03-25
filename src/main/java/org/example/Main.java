package org.example;

import org.example.model.dao.*;
import org.example.model.database.DatabaseConnection;
import org.example.presenter.*;
import org.example.view.*;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {


        // Create main view
        MainView mainView = new MainView();

        // Initialize DAOs
        LantDAO lantDAO = new LantDAO();
        LocatieDAO locatieDAO = new LocatieDAO();
        HotelDAO hotelDAO = new HotelDAO();
        CameraDAO cameraDAO = new CameraDAO();
        RezervareDAO rezervareDAO = new RezervareDAO();

        // Create views
        LantView lantView = new LantView();
        HotelView hotelView = new HotelView();
        CameraView cameraView = new CameraView();
        RezervareView rezervareView = new RezervareView();

        // Create presenters
        LantPresenter lantPresenter = new LantPresenter(lantView, lantDAO, mainView);
        HotelPresenter hotelPresenter = new HotelPresenter(hotelView, hotelDAO, locatieDAO, lantDAO, mainView);
        CameraPresenter cameraPresenter = new CameraPresenter(cameraView, cameraDAO, rezervareDAO, mainView);
        RezervarePresenter rezervarePresenter = new RezervarePresenter(rezervareView, rezervareDAO, mainView);

        // Wire up components
        cameraPresenter.setRezervarePresenter(rezervarePresenter);
        hotelPresenter.setCameraPresenter(cameraPresenter);

        // Connect LantPresenter to HotelPresenter to refresh chains when needed
        lantPresenter.setHotelPresenter(hotelPresenter);

        // Set up main view listeners
        mainView.setLanturiButtonListener(e -> lantView.setVisible(true));
        mainView.setHoteluriButtonListener(e -> mainView.showPanel("hotel"));

        // For camera button, just show hotel panel if needed
        mainView.setCamereButtonListener(e -> {
            mainView.showPanel("camera");
        });

        // For rezervari button, just show camera panel if needed
        mainView.setRezervariButtonListener(e -> {
            mainView.showPanel("rezervare");
        });

        // Add panels to main view
        mainView.addPanel("hotel", hotelView);
        mainView.addPanel("camera", cameraView);
        mainView.addPanel("rezervare", rezervareView);

        // Display main view
        mainView.setVisible(true);

        // Add shutdown hook to close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConnection::closeConnection));
    }
}