package org.example.model.entities;

public class Camera {
    private int id;
    private int idHotel;
    private String nrCamera;
    private float pretPerNoapte;
    private int idPoze;

    public Camera() {
    }

    public Camera(int id, int idHotel, String nrCamera, float pretPerNoapte, int idPoze) {
        this.id = id;
        this.idHotel = idHotel;
        this.nrCamera = nrCamera;
        this.pretPerNoapte = pretPerNoapte;
        this.idPoze = idPoze;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public String getNrCamera() {
        return nrCamera;
    }

    public void setNrCamera(String nrCamera) {
        this.nrCamera = nrCamera;
    }

    public float getPretPerNoapte() {
        return pretPerNoapte;
    }

    public void setPretPerNoapte(float pretPerNoapte) {
        this.pretPerNoapte = pretPerNoapte;
    }

    public int getIdPoze() {
        return idPoze;
    }

    public void setIdPoze(int idPoze) {
        this.idPoze = idPoze;
    }
}