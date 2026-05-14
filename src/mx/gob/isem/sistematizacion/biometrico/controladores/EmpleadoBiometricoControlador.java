package mx.gob.isem.sistematizacion.biometrico.controladores;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import mx.gob.isem.sistematizacion.biometrico.InstanciaBiometrico;
import mx.gob.isem.sistematizacion.biometrico.User;
import mx.gob.isem.sistematizacion.biometrico.utilidades.FuncionesRepetidas.ModoVerificacion;
import mx.gob.isem.sistematizacion.biometrico.dao.BiometricoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.EmpleadoBiometricoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.EmpleadoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.HuellaDAO;
import mx.gob.isem.sistematizacion.biometrico.modelos.Biometrico;
import mx.gob.isem.sistematizacion.biometrico.modelos.EmpleadoLocal;
import mx.gob.isem.sistematizacion.biometrico.modelos.EmpleadoBiometrico;
import mx.gob.isem.sistematizacion.biometrico.modelos.HuellaLocal;
import mx.gob.isem.sistematizacion.biometrico.vistas.VistaPrincipal;

public class EmpleadoBiometricoControlador {
	private VistaPrincipal vista;
	private EmpleadoDAO empleadoDao;
    private BiometricoDAO biometricoDao;
    private EmpleadoBiometricoDAO empleadoBiometricoDao;
    private HuellaDAO huellasDao;
	
	public EmpleadoBiometricoControlador(VistaPrincipal vista, List<InstanciaBiometrico> dispositivos) {
		this.vista = vista;
		this.empleadoDao = new EmpleadoDAO();
		this.biometricoDao = new BiometricoDAO();
		this.empleadoBiometricoDao = new EmpleadoBiometricoDAO();
		this.huellasDao = new HuellaDAO();
		inicializarEventos();		
	}
	
	private void inicializarEventos() {
		vista.btnVerDetalle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mostrarDetallesEmpleado();
			}
		});
	}
	
	private void mostrarDetallesEmpleado() {
	    int fila = vista.tablaPrincipal.getSelectedRow();
	    if (fila != -1) {
	        String idEmpleado = vista.tablaPrincipal.getValueAt(fila, 0).toString();
	        EmpleadoLocal empleado = empleadoDao.consultarEmpleado(idEmpleado);
	        if (empleado == null) {
	            JOptionPane.showMessageDialog(vista, 
            		"Error al consultar los datos del empleado.", 
            		"Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }
	        // Consultamos huellas y dispositivos
	        List<Biometrico> biometricos = biometricoDao.consultarBiometricos();
	        List<HuellaLocal> huellas = huellasDao.consultarHuellasEmpleado(empleado);
	        // Tabla de dispositivos
	        String[] colDispositivos = {"Dispositivo", "ID en Dispositivo"};
	        DefaultTableModel modeloDispositivos = new DefaultTableModel(colDispositivos, 0) {
	        	// Hacemos que la celda no se pueda editar
	            @Override public boolean isCellEditable(int r, int c) { return false; }
	        };
	        for (Biometrico biometrico : biometricos) {
	            EmpleadoBiometrico empleadoBiometrico = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometrico);
	            if (empleadoBiometrico != null) {
	                modeloDispositivos.addRow(new Object[]{biometrico.getNombre(), empleadoBiometrico.getIdGenerado()});
	            }
	        }

	        // Tabla de huellas
	        String[] colHuellas = {"Dedo", "Índice", "Estado"};
	        DefaultTableModel modeloHuellas = new DefaultTableModel(colHuellas, 0) {
	            @Override public boolean isCellEditable(int r, int c) { return false; }
	        };
	        if (huellas != null) {
	            for (HuellaLocal huella : huellas) {
	                modeloHuellas.addRow(new Object[]{
	                    obtenerNombreDedo(huella.getIndice()), 
	                    huella.getIndice(), 
	                    "Registrada"
	                });
	            }
	        }

	        JPanel panelPrincipal = new JPanel();
	        panelPrincipal.setLayout(new javax.swing.BoxLayout(panelPrincipal, javax.swing.BoxLayout.Y_AXIS));

	        // Datos generales
	        JPanel panelCabecera = new JPanel(new GridLayout(3, 1, 5, 5));
	        panelCabecera.setBorder(BorderFactory.createTitledBorder("Datos del Empleado"));
	        String nombreCompleto = empleado.getNombre() + " " + empleado.getPrimerApellido() + " " + empleado.getSegundoApellido();
	        panelCabecera.add(new JLabel("<html><b>Nombre:</b> " + nombreCompleto + "</html>"));
	        panelCabecera.add(new JLabel("<html><b>RFC:</b> " + empleado.getRfc() + "</html>"));
	        panelCabecera.add(new JLabel("<html><b>RFID:</b> " + (empleado.getRfid() > 0 ? empleado.getRfid() : "Sin tarjeta") + "</html>"));
	        panelCabecera.add(new JLabel("<html><b>Contraseña:</b> " + (empleado.getPassword().equals("") ? "Sin Contraseña" : "Configurada") + "</html>"));
	        // Tabla de dispositivos
	        JTable tablaDispositivos = new JTable(modeloDispositivos);
	        JScrollPane scrollDispositivos = new JScrollPane(tablaDispositivos);
	        scrollDispositivos.setPreferredSize(new Dimension(400, 100));
	        scrollDispositivos.setBorder(BorderFactory.createTitledBorder("Configuración en Dispositivos"));
	        // Tabla de huellas
	        JTable tablaHuellas = new JTable(modeloHuellas);
	        JScrollPane scrollHuellas = new JScrollPane(tablaHuellas);
	        scrollHuellas.setPreferredSize(new Dimension(400, 100));
	        scrollHuellas.setBorder(BorderFactory.createTitledBorder("Huellas Digitales en Base de Datos"));
	        // Añadimos todo a la ventana
	        panelPrincipal.add(panelCabecera);
	        panelPrincipal.add(javax.swing.Box.createVerticalStrut(10));
	        panelPrincipal.add(scrollDispositivos);
	        panelPrincipal.add(javax.swing.Box.createVerticalStrut(10));
	        panelPrincipal.add(scrollHuellas);
	        // Mostramos la ventana
	        JOptionPane.showMessageDialog(vista, 
        		panelPrincipal, 
        		"Detalle de Empleado", JOptionPane.PLAIN_MESSAGE);

	    } else {
	        JOptionPane.showMessageDialog(vista, 
        		"Seleccione un empleado de la tabla principal.", 
        		"Aviso", JOptionPane.WARNING_MESSAGE);
	    }
	}

	// Método para obtener nombre de índices
	private String obtenerNombreDedo(int indice) {
	    switch (indice) {
	        case 0: return "Meñique Izquierdo";
	        case 1: return "Anular Izquierdo";
	        case 2: return "Medio Izquierdo";
	        case 3: return "Índice Izquierdo";
	        case 4: return "Pulgar Izquierdo";
	        case 5: return "Pulgar Derecho";
	        case 6: return "Índice Derecho";
	        case 7: return "Medio Derecho";
	        case 8: return "Anular Derecho";
	        case 9: return "Meñique Derecho";
	        default: return "Desconocido";
	    }
	}
	
	// Método para sincronizar un usuario en un dispositivo junto con sus respectivas huellas
	public static boolean sincronizarUsuarioADispositivo(InstanciaBiometrico dispositivo, User usuario, List<HuellaLocal> huellas, ModoVerificacion modo) {
	    try {
	        if (!dispositivo.addUser(usuario)) return false;
	        // Si el modo requiere huellas, las enviamos todas
	        if (modo == ModoVerificacion.HUELLA_CREDENCIAL || modo == ModoVerificacion.HUELLA_PASSWORD) {
	            for (HuellaLocal h : huellas) {
	                if (!dispositivo.setFingerprintTemplate(usuario.serial, h.getIndice(), h.getTemplate())) {
	                    System.err.println("Error enviando huella " + h.getIndice() + " a " + dispositivo.obtenerIdentificador());
	                }
	            }
	        }	        
	        return dispositivo.setUserVerificationMode(usuario.serial, modo.getCodigo());
	    } catch (Exception e) {
	        System.err.println("Error en " + dispositivo.obtenerIdentificador() + ": " + e.getMessage());
	        return false;
	    }
	}		
}
