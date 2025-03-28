public class EspacioParqueadero {
    private boolean ocupado;
    private Vehiculo vehiculo;

    public EspacioParqueadero() {
        this.ocupado = false;
        this.vehiculo = null;
    }

    public boolean estaOcupado() {
        return ocupado;
    }

    public void ocupar(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
        this.ocupado = true;
    }

    public void liberar() {
        this.vehiculo = null;
        this.ocupado = false;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }
}
