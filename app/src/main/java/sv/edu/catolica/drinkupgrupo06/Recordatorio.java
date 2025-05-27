package sv.edu.catolica.drinkupgrupo06;

public class Recordatorio {
    public int id;
    public String hora;
    public String estado;
    public int cantidad;

    public Recordatorio(int id, String hora, String estado, int cantidad) {
        this.id = id;
        this.hora = hora;
        this.estado = estado;
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return hora + " - " + estado + " - " + cantidad + " ml";
    }

    public String getHora() {
        return hora;
    }
    public String getEstado() {
        return estado;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
