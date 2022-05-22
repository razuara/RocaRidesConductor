package com.rocasoftware.rocaridesconductor;

public class ConductorModel {
    String nombre, apellido, telefono, email, fechaRegistro, FechaUltimoLogin,cuentaActivada,esManager;

    public ConductorModel() {
    }

    public ConductorModel(String nombre, String apellido, String telefono, String email, String fechaRegistro, String fechaUltimoLogin,String cuentaActivada,String esManager) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.FechaUltimoLogin = fechaUltimoLogin;
        this.cuentaActivada = cuentaActivada;
        this.esManager = esManager;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFechaUltimoLogin() {
        return FechaUltimoLogin;
    }

    public void setFechaUltimoLogin(String fechaUltimoLogin) {
        FechaUltimoLogin = fechaUltimoLogin;
    }

    public String getCuentaActivada() {
        return cuentaActivada;
    }

    public void setCuentaActivada(String cuentaActivada) {
        this.cuentaActivada = cuentaActivada;
    }

    public String getEsManager() {
        return esManager;
    }

    public void setEsManager(String esManager) {
        this.esManager = esManager;
    }
}

