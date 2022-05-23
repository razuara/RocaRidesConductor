package com.rocasoftware.rocaridesconductor;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class SexoModel {
    String idDocumento, nombre;

    public SexoModel() {
    }

    public SexoModel(String nombre) {
        this.nombre = nombre;
    }

    @Exclude
    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }
}
