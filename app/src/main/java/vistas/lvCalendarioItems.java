package vistas;

public class lvCalendarioItems {
    int icono;
    String header;
    String titulo;
    String fecha;
    int antiguedad;

    public lvCalendarioItems(String header, int icono, int antiguedad, String titulo, String fecha) {
        super();
        this.icono = icono;
        this.header = header;
        this.titulo = titulo;
        this.fecha = fecha;
        this.antiguedad = antiguedad;
    }
}
