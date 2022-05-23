package com.rocasoftware.rocaridesconductor;

public class ManagerModel {
    String nombre, apellido, telefono, email, fechaRegistro, FechaUltimoLogin,cuentaActivada,sexo,estado,ciudad;

    public ManagerModel() {
    }

    public ManagerModel(String nombre, String apellido, String telefono, String email, String fechaRegistro, String fechaUltimoLogin,String cuentaActivada,String sexo,String estado,String ciudad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.FechaUltimoLogin = fechaUltimoLogin;
        this.cuentaActivada = cuentaActivada;
        this.sexo = sexo;
        this.estado = estado;
        this.ciudad = ciudad;
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
}
