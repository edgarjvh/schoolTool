package clases;

import java.io.Serializable;

public class Representante implements Serializable {
    private int id;
    private String nombres;
    private String Apellidos;
    private int telefono1;
    private int cedula;
    private int telefono2;
    private String direccion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCedula() {
        return cedula;
    }

    public void setCedula(int cedula) {
        int cedula1 = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public void setApellidos(String apellidos) {
        Apellidos = apellidos;
    }

    public int getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(int telefono1) {
        int telefono11 = telefono1;
    }

    public int getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(int telefono2) {
        int telefono21 = telefono2;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        String direccion1 = direccion;
    }
}
