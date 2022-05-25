package com.rocasoftware.rocaridesconductor;

public class ConductorModel {
    String nombre, apellido, telefono, email, fechaRegistro, FechaUltimoLogin,cuentaActivada,esManager,idManager,sexo,estado,ciudad,urlImagenFotoPerfil,urlImagenFotoLicencia;

    public ConductorModel() {
    }

    public ConductorModel(String nombre, String apellido, String telefono, String email, String fechaRegistro, String fechaUltimoLogin,String cuentaActivada,String esManager,String idManager,String sexo,String estado,String ciudad,String urlImagenFotoPerfil,String urlImagenFotoLicencia) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.FechaUltimoLogin = fechaUltimoLogin;
        this.cuentaActivada = cuentaActivada;
        this.esManager = esManager;
        this.idManager = idManager;
        this.sexo = sexo;
        this.estado = estado;
        this.ciudad = ciudad;
        this.urlImagenFotoPerfil = urlImagenFotoPerfil;
        this.urlImagenFotoLicencia = urlImagenFotoLicencia;
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

    public String getIdManager() {
        return idManager;
    }

    public void setIdManager(String idManager) {
        this.idManager = idManager;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getUrlImagenFotoPerfil() {
        return urlImagenFotoPerfil;
    }

    public void setUrlImagenFotoPerfil(String urlImagenFotoPerfil) {
        this.urlImagenFotoPerfil = urlImagenFotoPerfil;
    }

    public String getUrlImagenFotoLicencia() {
        return urlImagenFotoLicencia;
    }

    public void setUrlImagenFotoLicencia(String urlImagenFotoLicencia) {
        this.urlImagenFotoLicencia = urlImagenFotoLicencia;
    }
}

