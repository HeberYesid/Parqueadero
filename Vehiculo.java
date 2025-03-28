import java.time.LocalDateTime;

public class Vehiculo {
    private final String placa;
    private final String marca;
    private final String color;
    private final LocalDateTime tiempoEntrada;
    private final int espacioAsignado;
    private final boolean esDiscapacitado;

    public Vehiculo(String placa, String marca, String color, int espacioAsignado, boolean esDiscapacitado) {
        this.placa = placa.toUpperCase();
        this.marca = marca;
        this.color = color;
        this.espacioAsignado = espacioAsignado;
        this.tiempoEntrada = LocalDateTime.now();
        this.esDiscapacitado = esDiscapacitado;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getColor() {
        return color;
    }

    public int getEspacioAsignado() {
        return espacioAsignado;
    }

    public boolean esDiscapacitado() {
        return esDiscapacitado;
    }

    public LocalDateTime getTiempoEntrada() {
        return tiempoEntrada;
    }
}
