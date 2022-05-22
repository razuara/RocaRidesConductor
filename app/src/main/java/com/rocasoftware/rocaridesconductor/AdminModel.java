package com.rocasoftware.rocaridesconductor;

import com.google.firebase.firestore.Exclude;

public class AdminModel
{
    String idDocument,nombre,apellido,telefono,email,fechaRegistro,fechaUltimoLogin,token;

    public AdminModel()
    {

    }

    public AdminModel(String nombre, String apellido, String telefono, String email,String fechaRegistro,String fechaUltimoLogin,String token) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.fechaUltimoLogin = fechaUltimoLogin;
        this.token = token;
    }
    @Exclude
    public String getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(String idDocument) {
        this.idDocument = idDocument;
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
        return fechaUltimoLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setFechaUltimoLogin(String fechaUltimoLogin) {
        this.fechaUltimoLogin = fechaUltimoLogin;


    }
}
