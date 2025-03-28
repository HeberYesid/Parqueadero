import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


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
        // Panel principal con BorderLayout
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel para el título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblTitulo = new JLabel("Registrar Vehículo");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelTitulo.add(lblTitulo);

        // Panel para los campos de entrada
        JPanel panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Configuración de los campos
        JLabel lblPlaca = new JLabel("Placa:");
        txtPlaca = new JTextField(15);
        
        JLabel lblPais = new JLabel("País:");
        String[] opcionesPais = {"Colombiana", "Venezolana"};
        cbPais = new JComboBox<>(opcionesPais);
        
        chkDiscapacitado = new JCheckBox("¿Es discapacitado?");
        
        JLabel lblMarca = new JLabel("Marca:");
        String[] marcas = {"Toyota", "Chevrolet", "Mazda", "Nissan", "Hyundai", "Kia", "Ford", 
                          "Volkswagen", "Renault", "Honda", "BMW", "Mercedes-Benz", "Audi", 
                          "Peugeot", "Jeep", "Fiat", "Subaru", "Mitsubishi", "Suzuki", "Land Rover"};
        cbMarca = new JComboBox<>(marcas);
        
        JLabel lblColor = new JLabel("Color:");
        String[] colores = {"Rojo", "Azul", "Negro", "Blanco", "Plateado", "Gris", 
                           "Verde", "Amarillo", "Naranja", "Marrón"};
        cbColor = new JComboBox<>(colores);

        // Agregar componentes usando GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0;
        panelCampos.add(lblPlaca, gbc);
        gbc.gridx = 1;
        panelCampos.add(txtPlaca, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelCampos.add(lblPais, gbc);
        gbc.gridx = 1;
        panelCampos.add(cbPais, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelCampos.add(chkDiscapacitado, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        panelCampos.add(lblMarca, gbc);
        gbc.gridx = 1;
        panelCampos.add(cbMarca, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelCampos.add(lblColor, gbc);
        gbc.gridx = 1;
        panelCampos.add(cbColor, gbc);

        // Panel para los botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnRegistrar = new JButton("Registrar Entrada");
        JButton btnSalida = new JButton("Registrar Salida");
        btnRegistrar.addActionListener(this::registrarEntrada);
        btnSalida.addActionListener(this::registrarSalida);
        
        // Estilo para los botones
        btnRegistrar.setPreferredSize(new Dimension(150, 30));
        btnSalida.setPreferredSize(new Dimension(150, 30));
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnSalida);

        // Agregar todos los paneles al panel principal
        panel.add(panelTitulo, BorderLayout.NORTH);
        panel.add(panelCampos, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

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

        // Primero verificar si hay espacio disponible
        int espacio = parqueadero.registrarVehiculo(placa, marca, color, esDiscapacitado);
        if (espacio == -1) {
            JOptionPane.showMessageDialog(frame, "No hay espacios disponibles en el parqueadero.");
            return;
        }

        // Si hay espacio, entonces registrar en la base de datos
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "")) {
            // Primero insertamos en la tabla de vehículos activos
            String sqlVehiculo = "INSERT INTO vehiculos (placa, pais, discapacitado, marca, color) VALUES (?, ?, ?, ?, ?)";
            String sqlHistorico = "INSERT INTO historico_vehiculos (placa, pais, discapacitado, marca, color, tipo_movimiento) VALUES (?, ?, ?, ?, ?, 'ENTRADA')";
            
            try (PreparedStatement stmtVehiculo = conn.prepareStatement(sqlVehiculo);
                 PreparedStatement stmtHistorico = conn.prepareStatement(sqlHistorico)) {
                
                // Preparar datos para ambas tablas
                for (PreparedStatement stmt : new PreparedStatement[]{stmtVehiculo, stmtHistorico}) {
                    stmt.setString(1, placa);
                    stmt.setString(2, pais);
                    stmt.setBoolean(3, esDiscapacitado);
                    stmt.setString(4, marca);
                    stmt.setString(5, color);
                }

                // Ejecutar ambas inserciones
                stmtVehiculo.executeUpdate();
                stmtHistorico.executeUpdate();
                
                actualizarEspacios();
                JOptionPane.showMessageDialog(frame, 
                    "Vehículo registrado exitosamente y asignado al espacio " + (espacio + 1));
            }
        } catch (SQLException ex) {
            // Si hay error en la BD, eliminar el vehículo del parqueadero
            parqueadero.registrarSalida(placa);
            actualizarEspacios();
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Error al registrar el vehículo en la base de datos: " + ex.getMessage());
        }
    }

    private void registrarSalida(ActionEvent e) {
        String placa = JOptionPane.showInputDialog(frame, "Ingrese la placa del vehículo para registrar su salida:");
        if (placa == null || placa.trim().isEmpty()) return;

        placa = placa.trim().toUpperCase();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "")) {
            // Primero obtener los datos del vehículo antes de eliminarlo
            String sqlSelect = "SELECT * FROM vehiculos WHERE placa = ?";
            String sqlDelete = "DELETE FROM vehiculos WHERE placa = ?";
            String sqlHistorico = "INSERT INTO historico_vehiculos (placa, pais, discapacitado, marca, color, tipo_movimiento) VALUES (?, ?, ?, ?, ?, 'SALIDA')";

            try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
                stmtSelect.setString(1, placa);
                ResultSet rs = stmtSelect.executeQuery();

                
                if (rs.next()) {
                    // Guardar los datos en el histórico
                    try (PreparedStatement stmtHistorico = conn.prepareStatement(sqlHistorico)) {
                        stmtHistorico.setString(1, rs.getString("placa"));
                        stmtHistorico.setString(2, rs.getString("pais"));
                        stmtHistorico.setBoolean(3, rs.getBoolean("discapacitado"));
                        stmtHistorico.setString(4, rs.getString("marca"));
                        stmtHistorico.setString(5, rs.getString("color"));
                        stmtHistorico.executeUpdate();
                    }

                    // Eliminar el vehículo de la tabla de activos
                    try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {
                        stmtDelete.setString(1, placa);
                        stmtDelete.executeUpdate();
                    }

                    boolean salidaExitosa = parqueadero.registrarSalida(placa);
                    if (salidaExitosa) {
                        actualizarEspacios();
                        JOptionPane.showMessageDialog(frame, 
                            "Vehículo eliminado exitosamente y registrado en el histórico.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "No se encontró el vehículo en el sistema.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Error al procesar la salida del vehículo: " + ex.getMessage());
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
