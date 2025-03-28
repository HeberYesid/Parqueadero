import java.util.HashMap;

public class Parqueadero {
    private final EspacioParqueadero[] espacios;
    private final HashMap<String, Vehiculo> vehiculos;

    public Parqueadero() {
        espacios = new EspacioParqueadero[22];
        vehiculos = new HashMap<>();

        for (int i = 0; i < 22; i++) {
            espacios[i] = new EspacioParqueadero();
        }
    }

    public int registrarVehiculo(String placa, String marca, String color, boolean esDiscapacitado) {
        if (vehiculos.containsKey(placa)) {
            return -2; // Ya registrado
        }

        int espacioLibre = obtenerEspacioLibre(esDiscapacitado);
        if (espacioLibre != -1) {
            Vehiculo vehiculo = new Vehiculo(placa, marca, color, espacioLibre, esDiscapacitado);
            espacios[espacioLibre].ocupar(vehiculo);
            vehiculos.put(placa, vehiculo);
        }
        return espacioLibre;
    }

    public boolean registrarSalida(String placa) {
        Vehiculo vehiculo = vehiculos.remove(placa);
        if (vehiculo == null) {
            return false;
        }
        espacios[vehiculo.getEspacioAsignado()].liberar();
        return true;
    }

    public int obtenerEspacioLibre(boolean paraDiscapacitados) {
        int inicio = paraDiscapacitados ? 20 : 0;
        int fin = paraDiscapacitados ? 22 : 20;

        for (int i = inicio; i < fin; i++) {
            if (!espacios[i].estaOcupado()) {
                return i;
            }
        }
        return -1;
    }

    public boolean validarPlaca(String placa, String pais) {
        if (pais.equals("Colombiana")) {
            return placa.matches("[A-Z]{3}[0-9]{3}");
        } else if (pais.equals("Venezolana")) {
            return placa.matches("[A-Z]{2}[0-9]{3}[A-Z]{2}");
        }
        return false;
    }

    public EspacioParqueadero[] getEspacios() {
        return espacios;
    }

    public HashMap<String, Vehiculo> getVehiculos() {
        return vehiculos;
    }
}
