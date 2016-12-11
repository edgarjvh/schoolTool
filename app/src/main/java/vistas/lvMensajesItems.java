package vistas;


public class lvMensajesItems {
    int tipo;
    int status;
    String fechaHora;
    String mensaje;
    String header;

    public lvMensajesItems(int tipo, int status, String fechaHora, String mensaje, String header){
        this.tipo = tipo;
        this.status = status;
        this.fechaHora = fechaHora;
        this.mensaje = mensaje;
        this.header = header;
    }

    public int getStatus() {
        return status;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
