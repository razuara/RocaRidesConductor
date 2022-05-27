package com.rocasoftware.rocaridesconductor;

import java.util.Date;

public class ManagerModel {
    String nombre, apellido, telefono, email,cuentaActivada,sexo,estado,ciudad,urlImagenFotoPerfil,urlImagenFotoLicencia;
    Date fechaRegistro,fechaUltimoLogin;
    public ManagerModel() {
    }

    public ManagerModel(String nombre, String apellido, String telefono, String email, Date fechaRegistro, Date fechaUltimoLogin,String cuentaActivada,String sexo,String estado,String ciudad,String urlImagenFotoPerfil,String urlImagenFotoLicencia) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.fechaUltimoLogin = fechaUltimoLogin;
        this.cuentaActivada = cuentaActivada;
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

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Date getFechaUltimoLogin() {
        return fechaUltimoLogin;
    }

    public void setFechaUltimoLogin(Date fechaUltimoLogin) {
        fechaUltimoLogin = fechaUltimoLogin;
    }

    public String getCuentaActivada() {
        return cuentaActivada;
    }

    public void setCuentaActivada(String cuentaActivada) {
        this.cuentaActivada = cuentaActivada;
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

