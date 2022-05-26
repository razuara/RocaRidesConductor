package com.rocasoftware.rocaridesconductor;

import com.google.firebase.firestore.Exclude;

public class VehiculoModel {
    String idVehiculo,marca,modelo,año,color,placas,idManager,urlImagenFotoVehiculo,urlImagenFotoTarjeta;

    public VehiculoModel() {
    }

    public VehiculoModel(String marca, String modelo, String año, String color, String placas, String idManager, String urlImagenFotoVehiculo, String urlImagenFotoTarjeta) {
        this.marca = marca;
        this.modelo = modelo;
        this.año = año;
        this.color = color;
        this.placas = placas;
        this.idManager = idManager;
        this.urlImagenFotoVehiculo = urlImagenFotoVehiculo;
        this.urlImagenFotoTarjeta = urlImagenFotoTarjeta;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAño() {
        return año;
    }

    public void setAño(String año) {
        this.año = año;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlacas() {
        return placas;
    }

    public void setPlacas(String placas) {
        this.placas = placas;
    }

    public String getIdManager() {
        return idManager;
    }

    public void setIdManager(String idManager) {
        this.idManager = idManager;
    }

    public String getUrlImagenFotoVehiculo() {
        return urlImagenFotoVehiculo;
    }

    public void setUrlImagenFotoVehiculo(String urlImagenFotoVehiculo) {
        this.urlImagenFotoVehiculo = urlImagenFotoVehiculo;
    }

    public String getUrlImagenFotoTarjeta() {
        return urlImagenFotoTarjeta;
    }

    public void setUrlImagenFotoTarjeta(String urlImagenFotoTarjeta) {
        this.urlImagenFotoTarjeta = urlImagenFotoTarjeta;
    }
@Exclude
    public String getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(String idVehiculo) {
        this.idVehiculo = idVehiculo;
    }
}
