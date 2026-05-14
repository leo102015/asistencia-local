package mx.gob.isem.sistematizacion.biometrico.vistas;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class VistaPrincipal extends JFrame {

    public JButton btnConfigurar;
    public JButton btnEditarHuella;
    public JButton btnEliminarHuella;
    public JButton btnVerDetalle;
    public JButton btnCambiarPassword;
    public JTable tablaPrincipal;
    public DefaultTableModel modeloTabla;

    public VistaPrincipal() {    	
        setTitle("Biométricos Local - Directorio de Unidad");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
    	// Panel para buscar por RFC
    	JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JLabel lblBuscar = new JLabel("Buscar empleado por RFC:");
        JTextField txtBuscarRFC = new JTextField(20);
        
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscarRFC);        
        // Área centro de la tabla        
        String[] columnas = {"ID Empleado", "Nombre Completo", "RFC", "Configuración", "Configurado"};        
        // La tabla es solo de lectura
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPrincipal = new JTable(modeloTabla);
        // Evitamos que el usuario seleccione varias filas al mismo tiempo
        tablaPrincipal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPrincipal.setRowHeight(25);        
        // Aquí guardaremos el boolean 'configurado' para que el botón sepa si activarse o no, sin que el usuario lo vea.
        tablaPrincipal.getColumnModel().getColumn(4).setMinWidth(0);
        tablaPrincipal.getColumnModel().getColumn(4).setMaxWidth(0);
        tablaPrincipal.getColumnModel().getColumn(4).setWidth(0);
        
        TableRowSorter<DefaultTableModel> clasificador = new TableRowSorter<>(modeloTabla);
        tablaPrincipal.setRowSorter(clasificador);        
        //Método para buscar cuando se edita el campo de texto
        txtBuscarRFC.getDocument().addDocumentListener(new DocumentListener() {
            private void realizarBusqueda() {
                String texto = txtBuscarRFC.getText().trim();
                if (texto.length() == 0) {
                    clasificador.setRowFilter(null);
                } else {
                    clasificador.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 2));
                }
            }

            @Override public void insertUpdate(DocumentEvent e) { realizarBusqueda(); }
            @Override public void removeUpdate(DocumentEvent e) { realizarBusqueda(); }
            @Override public void changedUpdate(DocumentEvent e) { realizarBusqueda(); }
        });
        
        JScrollPane scrollPanel = new JScrollPane(tablaPrincipal);
        scrollPanel.setBorder(BorderFactory.createTitledBorder("Lista de Empleados"));
        // Área de botones
        JPanel panelAcciones = new JPanel();
        panelAcciones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelAcciones.setBorder(BorderFactory.createTitledBorder("Acciones (Seleccione un empleado)"));

        btnConfigurar = new JButton("Configurar Empleado");
        btnCambiarPassword = new JButton("Cambiar Contraseña");
        btnEditarHuella = new JButton("Editar Huella");
        btnEliminarHuella = new JButton("Eliminar Huella");
        btnVerDetalle = new JButton("Ver Detalle de Empleado");        
        // Los botones inician apagados porque no hay nadie seleccionado al abrir
        apagarBotones();
        panelAcciones.add(btnConfigurar);
        panelAcciones.add(btnCambiarPassword);
        panelAcciones.add(btnEditarHuella);
        panelAcciones.add(btnEliminarHuella);
        panelAcciones.add(btnVerDetalle);        
        tablaPrincipal.getSelectionModel().addListSelectionListener(new ListSelectionListener() {			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Este if evita que el evento se dispare dos veces (al presionar y al soltar el clic)
	            if (!e.getValueIsAdjusting()) {
	                int filaSeleccionada = tablaPrincipal.getSelectedRow();	                
	                if (filaSeleccionada != -1) {
	                    // Encendemos los botones generales
	                    btnEditarHuella.setEnabled(true);
	                    btnEliminarHuella.setEnabled(true);
	                    btnVerDetalle.setEnabled(true);
	                    btnCambiarPassword.setEnabled(true);	                    
	                    // Lógica especial para Configurar
	                    Object estadoConfigurado = tablaPrincipal.getValueAt(filaSeleccionada, 4);	                    
	                    if (estadoConfigurado != null && estadoConfigurado.toString().equalsIgnoreCase("true")) {
	                        btnConfigurar.setEnabled(false);
	                    } else {
	                        btnConfigurar.setEnabled(true);
	                    }	                    
	                } else {
	                    // Si el usuario hace clic en el vacío se pierde la selección
	                    apagarBotones();
	                }
	            }
			}
		});
              
        add(panelBusqueda, BorderLayout.NORTH);
        add(scrollPanel, BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    private void apagarBotones() {
        btnConfigurar.setEnabled(false);
        btnEditarHuella.setEnabled(false);
        btnEliminarHuella.setEnabled(false);
        btnVerDetalle.setEnabled(false);
        btnCambiarPassword.setEnabled(false);
    }
}