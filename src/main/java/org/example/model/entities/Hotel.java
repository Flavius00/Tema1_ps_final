package org.example.model.entities;

public class Hotel {
    private int id;
    private String nume;
    private int idLocatie;
    private String telefon;
    private String email;
    private String facilitati;
    private int idLant;

    public Hotel() {
    }

    public Hotel(int id, String nume, int idLocatie, String telefon, String email, String facilitati, int idLant) {
        this.id = id;
        this.nume = nume;
        this.idLocatie = idLocatie;
        this.telefon = telefon;
        this.email = email;
        this.facilitati = facilitati;
        this.idLant = idLant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public int getIdLocatie() {
        return idLocatie;
    }

    public void setIdLocatie(int idLocatie) {
        this.idLocatie = idLocatie;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacilitati() {
        return facilitati;
    }

    public void setFacilitati(String facilitati) {
        this.facilitati = facilitati;
    }

    public int getIdLant() {
        return idLant;
    }

    public void setIdLant(int idLant) {
        this.idLant = idLant;
    }
}