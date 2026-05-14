package mx.gob.isem.sistematizacion.biometrico.utilidades;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import mx.gob.isem.sistematizacion.biometrico.InstanciaBiometrico;

public class GestorDialogosUI {

	public static int solicitarIndice(JFrame vista) {
	    // Usamos un arreglo para manejar el índice
	    final int[] indiceSeleccionado = {-1};
	    // Creamos una ventana emergente
	    JDialog dialogo = new JDialog(vista, "Seleccionar Dedo a Enrolar", true);
	    dialogo.setSize(300, 250);
	    dialogo.setLocationRelativeTo(vista);
	    dialogo.setLayout(new BorderLayout(10, 10));
	    // Panel mano izquierda
	    JPanel panelIzquierdo = new JPanel(new GridLayout(5, 1, 5, 5));
	    panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Mano Izquierda"));    
	    // Nombres e índices de la mano izquierda
	    String[] nombresIzq = {"Meñique", "Anular", "Medio", "Índice", "Pulgar"};
	    int[] indicesIzq = {0, 1, 2, 3, 4};
	    for (int i = 0; i < nombresIzq.length; i++) {
	        JButton btn = new JButton(nombresIzq[i]);
	        // Variable final para el listener
	        final int index = indicesIzq[i];
	        btn.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					indiceSeleccionado[0] = index;
		            dialogo.dispose(); // Cierra la ventana y descongela el código
				}
			});
	        panelIzquierdo.add(btn);
	    }
	    // Panel mano derecha
	    JPanel panelDerecho = new JPanel(new GridLayout(5, 1, 5, 5));
	    panelDerecho.setBorder(BorderFactory.createTitledBorder("Mano Derecha"));	        
	    // Nombres e índices de la mano derecha
	    String[] nombresDer = {"Pulgar", "Índice", "Medio", "Anular", "Meñique"};
	    int[] indicesDer = {5, 6, 7, 8, 9};
	    for (int i = 0; i < nombresDer.length; i++) {
	        JButton btn = new JButton(nombresDer[i]);
	        final int index = indicesDer[i];
	        btn.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					indiceSeleccionado[0] = index;
		            dialogo.dispose();
				}
			});
	        panelDerecho.add(btn);
	    }
	    // Botón de cancelar
	    JButton btnCancelar = new JButton("Cancelar Enrolamiento");
	    btnCancelar.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogo.dispose();
			}
		});	    
	    JPanel panelInferior = new JPanel();
	    panelInferior.add(btnCancelar);
	    // Agregamos todo a la ventana
	    JPanel panelCentro = new JPanel(new GridLayout(1, 2, 10, 10));
	    panelCentro.add(panelIzquierdo);
	    panelCentro.add(panelDerecho);
	    dialogo.add(panelCentro, BorderLayout.CENTER);
	    dialogo.add(panelInferior, BorderLayout.SOUTH);
	    // Detenemos la aplicación principal
	    dialogo.setVisible(true);
	    // Retorna el dedo seleccionado
	    return indiceSeleccionado[0];
	}
	
	public static String solicitarPassword(JFrame vista) {
	    // Arreglo de un elemento para poder guardar el resultado desde los listeners
	    final String[] passwordFinal = {null}; 
	    JDialog dialogo = new JDialog(vista, "Crear Contraseña Numérica", true);
	    dialogo.setSize(300, 400);
	    dialogo.setLocationRelativeTo(vista);
	    dialogo.setLayout(new BorderLayout(10, 10));	    
	    JPanel panelArriba = new JPanel(new BorderLayout(5, 10));
	    panelArriba.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
	    JLabel lblInstrucciones = new JLabel("Ingrese un PIN de 1 a 8 dígitos", SwingConstants.CENTER);
	    lblInstrucciones.setFont(new Font("Arial", Font.BOLD, 14));
	    // Campo de contraseña	    
	    JTextField campoPassword = new JTextField();
	    // Evitamos que escriban letras con el teclado físico
	    campoPassword.setEditable(false); 
	    campoPassword.setHorizontalAlignment(JTextField.CENTER);
	    campoPassword.setFont(new Font("Arial", Font.BOLD, 32));
	    campoPassword.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createEmptyBorder(15, 15, 5, 15),
	            campoPassword.getBorder()));	    
	    panelArriba.add(lblInstrucciones, BorderLayout.NORTH);
	    panelArriba.add(campoPassword, BorderLayout.CENTER);    
	    dialogo.add(panelArriba, BorderLayout.NORTH);
	    // Teclado numérico
	    JPanel panelTeclado = new JPanel(new GridLayout(4, 3, 5, 5));
	    panelTeclado.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
	    String[] botones = {
	        "7", "8", "9",
	        "4", "5", "6",
	        "1", "2", "3",
	        "C", "0", "<-"
	    };
	    // Creamos los botones
	    for (String texto : botones) {
	        JButton btn = new JButton(texto);
	        btn.setFont(new Font("Arial", Font.BOLD, 18));	        
	        btn.addActionListener(new ActionListener() {			
				@Override
				public void actionPerformed(ActionEvent e) {
		            String actual = new String(campoPassword.getText());		            
		            if (texto.equals("C")) {
		                // Borrar todo
		                campoPassword.setText("");
		            } else if (texto.equals("<-")) {
		                // Borrar el último dígito
		                if (actual.length() > 0) {
		                    campoPassword.setText(actual.substring(0, actual.length() - 1));
		                }
		            } else {
		                // Agregar número con validación
		                if (actual.length() < 8) {
		                    campoPassword.setText(actual + texto);
		                }
		            }
				}
			});
	        panelTeclado.add(btn);
	    }	    
	    dialogo.add(panelTeclado, BorderLayout.CENTER);
	    // Botones de acción
	    JPanel panelAcciones = new JPanel();
	    JButton btnAceptar = new JButton("Aceptar");
	    JButton btnCancelar = new JButton("Cancelar");
	    btnAceptar.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String passStr = new String(campoPassword.getText());		        
		        if (passStr.isEmpty()) {
		            JOptionPane.showMessageDialog(dialogo, 
		                "La contraseña no puede estar vacía.", 
		                "Advertencia", JOptionPane.WARNING_MESSAGE);
		        } else {
		            // Guardamos la contraseña y cerramos la ventana
		            passwordFinal[0] = passStr;
		            dialogo.dispose();
		        }
			}
		});
	    btnCancelar.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogo.dispose();
			}
		});	    
	    panelAcciones.add(btnAceptar);
	    panelAcciones.add(btnCancelar);
	    dialogo.add(panelAcciones, BorderLayout.SOUTH);
	    // Congelamos la ejecución
	    dialogo.setVisible(true);
	    // Retorna la contraseña
	    return passwordFinal[0];
	}
	
	public static boolean procesarEnrolamiento(JFrame vista, String idGenerado, int indiceNuevo, InstanciaBiometrico maestro) {
	    // Usamos un arreglo para poder modificar el valor desde dentro
	    final boolean[] resultadoEnrolamiento = {false};
	    // Lanzamos una ventana emergente
	    JDialog dialogoEspera = new JDialog(vista, "Sensor Activo", true);
	    dialogoEspera.setSize(550, 150);
	    dialogoEspera.setLocationRelativeTo(vista);
	    dialogoEspera.setLayout(new BorderLayout(10, 10));	    
	    // Evitamos que el usuario cierre la ventana
	    dialogoEspera.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);	    
	    JLabel lblMensaje = new JLabel("Por favor, coloque el dedo en el sensor como se indica en el dispositivo.", SwingConstants.CENTER);
	    lblMensaje.setFont(new Font("Arial", Font.BOLD, 13));
	    JLabel lblTemporizador = new JLabel("Tiempo restante: " + 40 + "s", SwingConstants.CENTER);
	    lblTemporizador.setFont(new Font("Arial", Font.PLAIN, 22));
	    lblTemporizador.setForeground(new Color(200, 0, 0));
	    dialogoEspera.add(lblMensaje, BorderLayout.CENTER);
	    dialogoEspera.add(lblTemporizador, BorderLayout.SOUTH);
	    // Configuración del temporizador visual
	    final int[] tiempoRestante = {40};	    
	    Timer timer = new Timer(1000, new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				tiempoRestante[0]--;
		        lblTemporizador.setText("Tiempo restante: " + tiempoRestante[0] + "s");		        
		        if (tiempoRestante[0] <= 0) {
		            ((Timer)e.getSource()).stop();
		            lblTemporizador.setText("Tiempo agotado...");
		        }
			}
		});
	    // Hilo para manejar el evento
	    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
	        @Override
	        protected Boolean doInBackground() throws Exception {
	            // Llamamos al método de enrolamiento de la librería
	            return maestro.enrollFingerprint(idGenerado, indiceNuevo);
	        }
	        
	        @Override
	        protected void done() {
	            try {
	                // Rescatamos el resultado final cuando finaliza el trabajo en segundo plano 
	                resultadoEnrolamiento[0] = get(); 
	            } catch (Exception ex) {
	                System.err.println("Error interno durante el enrolamiento: " + ex.getMessage());
	                resultadoEnrolamiento[0] = false;
	            } finally {
	                // Limpiamos todo
	                timer.stop();
	                dialogoEspera.dispose();
	            }
	        }
	    };
	    timer.start();
	    worker.execute();
	    dialogoEspera.setVisible(true);
	    return resultadoEnrolamiento[0];
	}
}
