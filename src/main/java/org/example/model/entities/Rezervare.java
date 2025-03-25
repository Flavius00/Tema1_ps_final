package org.example.model.entities;

import java.time.LocalDateTime;

public class Rezervare {
    private int id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int idCamera;
    private String numeClient;
    private String prenumeClient;
    private String telefonClient;
    private String emailClient;

    public Rezervare() {
    }

    public Rezervare(int id, LocalDateTime startDate, LocalDateTime endDate, int idCamera,
                     String numeClient, String prenumeClient, String telefonClient, String emailClient) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.idCamera = idCamera;
        this.numeClient = numeClient;
        this.prenumeClient = prenumeClient;
        this.telefonClient = telefonClient;
        this.emailClient = emailClient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getIdCamera() {
        return idCamera;
    }

    public void setIdCamera(int idCamera) {
        this.idCamera = idCamera;
    }

    public String getNumeClient() {
        return numeClient;
    }

    public void setNumeClient(String numeClient) {
        this.numeClient = numeClient;
    }

    public String getPrenumeClient() {
        return prenumeClient;
    }

    public void setPrenumeClient(String prenumeClient) {
        this.prenumeClient = prenumeClient;
    }

    public String getTelefonClient() {
        return telefonClient;
    }

    public void setTelefonClient(String telefonClient) {
        this.telefonClient = telefonClient;
    }

    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }
}