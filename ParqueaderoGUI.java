import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.*;

public class ParqueaderoGUI {
    private final Parqueadero parqueadero;
    private final JFrame frame;
    private JTextArea[] espacioAreas;
    private JTextField txtPlaca;
    private JComboBox<String> cbPais;
    private JCheckBox chkDiscapacitado;
    private JComboBox<String> cbMarca;
    private JComboBox<String> cbColor;

    public ParqueaderoGUI() {
        parqueadero = new Parqueadero();
        frame = new JFrame("Parqueadero Amigable");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panelDatos = crearPanelDatos();
        JPanel panelParqueadero = crearPanelParqueadero();

        frame.add(panelDatos, BorderLayout.WEST);
        frame.add(panelParqueadero, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel crearPanelDatos() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Registrar Vehículo");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        // Campo de texto para la placa
        JLabel lblPlaca = new JLabel("Placa:");
        txtPlaca = new JTextField();

        // Selección de país
        JLabel lblPais = new JLabel("País:");
        String[] opcionesPais = {"Colombiana", "Venezolana"};
        cbPais = new JComboBox<>(opcionesPais);

        // Checkbox para discapacitados
        chkDiscapacitado = new JCheckBox("¿Es discapacitado?");

        // Selección de marca
        JLabel lblMarca = new JLabel("Marca:");
        String[] marcas = {"Toyota", "Chevrolet", "Mazda", "Nissan", "Hyundai","Kia", "Ford", "Volkswagen", "Renault", "Honda","BMW", "Mercedes-Benz", "Audi", "Peugeot", "Jeep","Fiat", "Subaru", "Mitsubishi", "Suzuki", "Land Rover"};
        cbMarca = new JComboBox<>(marcas);

        // Selección de color
        JLabel lblColor = new JLabel("Color:");
        String[] colores = {"Rojo", "Azul", "Negro", "Blanco", "Plateado","Gris", "Verde", "Amarillo", "Naranja", "Marrón"};
        cbColor = new JComboBox<>(colores);

        // Botones de acción
        JButton btnRegistrar = new JButton("Registrar Entrada");
        btnRegistrar.addActionListener(this::registrarEntrada);

        JButton btnSalida = new JButton("Registrar Salida");
        btnSalida.addActionListener(this::registrarSalida);

        // Agregar elementos al panel
        panel.add(lblTitulo);
        panel.add(lblPlaca);
        panel.add(txtPlaca);
        panel.add(lblPais);
        panel.add(cbPais);
        panel.add(chkDiscapacitado);
        panel.add(lblMarca);
        panel.add(cbMarca);
        panel.add(lblColor);
        panel.add(cbColor);
        panel.add(btnRegistrar);
        panel.add(btnSalida);

        return panel;
    }

    private JPanel crearPanelParqueadero() {
        JPanel panel = new JPanel(new GridLayout(4, 6, 5, 5));
        espacioAreas = new JTextArea[22];

        for (int i = 0; i < 22; i++) {
            espacioAreas[i] = new JTextArea(i >= 20 ? 
                    "Discapacitado " + (i - 19) : 
                    "Espacio " + (i + 1));
            espacioAreas[i].setEditable(false);
            espacioAreas[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            espacioAreas[i].setBackground(Color.GREEN);
            espacioAreas[i].setOpaque(true);
            panel.add(espacioAreas[i]);
        }

        panel.setBorder(BorderFactory.createTitledBorder("Mapa del Parqueadero"));
        return panel;
    }

    private void registrarEntrada(ActionEvent e) {
    String placa = txtPlaca.getText().trim().toUpperCase();
    if (placa.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "Por favor ingrese la placa.");
        return;
    }

    String pais = (String) cbPais.getSelectedItem();
    boolean esDiscapacitado = chkDiscapacitado.isSelected();
    String marca = (String) cbMarca.getSelectedItem();
    String color = (String) cbColor.getSelectedItem();

    // Validar placa según país
    if (pais.equals("Colombiana") && !placa.matches("[A-Z]{3}[0-9]{3}")) {
        JOptionPane.showMessageDialog(frame, "Placa colombiana inválida (Formato: ABC123).");
        return;
    } else if (pais.equals("Venezolana") && !placa.matches("[A-Z]{2}[0-9]{3}[A-Z]{2}")) {
        JOptionPane.showMessageDialog(frame, "Placa venezolana inválida (Formato: AB123CD).");
        return;
    }

    if (parqueadero.getVehiculos().containsKey(placa)) {
        JOptionPane.showMessageDialog(frame, "El vehículo ya está registrado.");
        return;
    }

    // Insertar datos en la base de datos
    try (Connection conn =(DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", ""));
        ) {
        String sql = "INSERT INTO vehiculos (placa, pais, discapacitado, marca, color) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, placa);
            stmt.setString(2, pais);
            stmt.setBoolean(3, esDiscapacitado);
            stmt.setString(4, marca);
            stmt.setString(5, color);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(frame, "Vehículo registrado en la base de datos exitosamente.");
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(frame, "Error al registrar el vehículo en la base de datos: " + ex.getMessage());
        return;
    }

    // Registrar en el parqueadero y actualizar GUI
    int espacio = parqueadero.registrarVehiculo(placa, marca, color, esDiscapacitado);
    if (espacio == -1) {
        JOptionPane.showMessageDialog(frame, "No hay espacios disponibles.");
    } else {
        actualizarEspacios();
        JOptionPane.showMessageDialog(frame, "Vehículo registrado exitosamente en el sistema.");
    }
}


    private void registrarSalida(ActionEvent e) {
        String placa = JOptionPane.showInputDialog(frame, "Ingrese la placa del vehículo para registrar su salida:");
        if (placa == null || placa.trim().isEmpty()) return;

        placa = placa.trim().toUpperCase();

        boolean salidaExitosa = parqueadero.registrarSalida(placa);
        if (salidaExitosa) {
            actualizarEspacios();
            JOptionPane.showMessageDialog(frame, "Salida registrada exitosamente.");
        } else {
            JOptionPane.showMessageDialog(frame, "No se encontró el vehículo.");
        }
    }

    private void actualizarEspacios() {
        EspacioParqueadero[] espacios = parqueadero.getEspacios();
        for (int i = 0; i < 22; i++) {
            if (espacios[i].estaOcupado()) {
                Vehiculo vehiculo = espacios[i].getVehiculo();
                espacioAreas[i].setText("Placa: " + vehiculo.getPlaca() + "\nMarca: " + vehiculo.getMarca() +
                        "\nColor: " + vehiculo.getColor());
                espacioAreas[i].setBackground(Color.RED);
            } else {
                espacioAreas[i].setText(i >= 20 ? 
                        "Discapacitado " + (i - 19) : 
                        "Espacio " + (i + 1));
                espacioAreas[i].setBackground(Color.GREEN);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParqueaderoGUI::new);
    }
}
