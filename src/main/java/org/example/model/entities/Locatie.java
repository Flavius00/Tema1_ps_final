package org.example.model.entities;

public class Locatie {
    private int id;
    private String tara;
    private String oras;
    private String strada;
    private String numar;

    public Locatie() {
    }

    public Locatie(int id, String tara, String oras, String strada, String numar) {
        this.id = id;
        this.tara = tara;
        this.oras = oras;
        this.strada = strada;
        this.numar = numar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTara() {
        return tara;
    }

    public void setTara(String tara) {
        this.tara = tara;
    }

    public String getOras() {
        return oras;
    }

    public void setOras(String oras) {
        this.oras = oras;
    }

    public String getStrada() {
        return strada;
    }

    public void setStrada(String strada) {
        this.strada = strada;
    }

    public String getNumar() {
        return numar;
    }

    public void setNumar(String numar) {
        this.numar = numar;
    }
}