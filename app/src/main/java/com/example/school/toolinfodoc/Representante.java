package com.example.school.toolinfodoc;

import java.io.Serializable;

class Representante implements Serializable {
    private int id;
    private String nombres;
    private String Apellidos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public int getCedula() {
        return cedula;
    }*/

    /*void setCedula(int cedula) {
        int cedula1 = cedula;
    }*/

    String getNombres() {
        return nombres;
    }

    void setNombres(String nombres) {
        this.nombres = nombres;
    }

    String getApellidos() {
        return Apellidos;
    }

    void setApellidos(String apellidos) {
        Apellidos = apellidos;
    }

    /*public int getTelefono1() {
        return telefono1;
    }*/

    /*void setTelefono1(int telefono1) {
        int telefono11 = telefono1;
    }*/

    /*public int getTelefono2() {
        return telefono2;
    }*/

    /*void setTelefono2(int telefono2) {
        int telefono21 = telefono2;
    }*/

    /*public String getDireccion() {
        return direccion;
    }*/

    /*void setDireccion(String direccion) {
        String direccion1 = direccion;
    }*/
}
