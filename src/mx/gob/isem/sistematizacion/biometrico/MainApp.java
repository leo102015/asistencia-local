package mx.gob.isem.sistematizacion.biometrico;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import mx.gob.isem.sistematizacion.biometrico.controladores.AsistenciaControlador;
import mx.gob.isem.sistematizacion.biometrico.controladores.EmpleadoBiometricoControlador;
import mx.gob.isem.sistematizacion.biometrico.controladores.EmpleadoControlador;
import mx.gob.isem.sistematizacion.biometrico.controladores.HuellaControlador;
import mx.gob.isem.sistematizacion.biometrico.dao.BiometricoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.ConfiguracionDAO;
import mx.gob.isem.sistematizacion.biometrico.modelos.Biometrico;
import mx.gob.isem.sistematizacion.biometrico.modelos.Configuracion;
import mx.gob.isem.sistematizacion.biometrico.vistas.VistaPrincipal;
import mx.gob.isem.sistematizacion.biometrico.ws.ClienteSistematizacionWS;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.BiometricosPortType;

public class MainApp {
	
	public static List<InstanciaBiometrico> dispositivosConectados = new ArrayList<>();
		
	public static void main(String[] args) {
		iniciarValidacion();		
	}
	
	
	private static void iniciarValidacion() {				
		new Thread(() -> {
			ConfiguracionDAO configuracionDao = new ConfiguracionDAO();
			Configuracion configuracion = configuracionDao.consultarConfiguracion();
			if (configuracion == null) {
	            SwingUtilities.invokeLater(() -> {
	                JOptionPane.showMessageDialog(null, 
	                    "Configuración incompleta: Faltan las credenciales del servidor central.\nEl sistema se cerrará.", 
	                    "Error Crítico", JOptionPane.ERROR_MESSAGE);
	                System.exit(0);
	            });
	            return;
	        }
			
			ClienteSistematizacionWS clienteBiometricos = new ClienteSistematizacionWS();
			BiometricoDAO dao = new BiometricoDAO();
            List<Biometrico> biometricos = dao.consultarBiometricos();
            
            if (biometricos.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, 
                        "Configuración incompleta: No hay dispositivos registrados en la base de datos.\nEl sistema se cerrará.", 
                        "Error Crítico", JOptionPane.ERROR_MESSAGE);
                    System.exit(0); // Cierra la aplicación completamente
                });
                return;
            }

            boolean todosConectados = false;

            // Validar que todos se conecten correctamente
            while (!todosConectados) {
                List<String> dispositivosFallidos = new ArrayList<>();
                dispositivosConectados.clear();
                 
                for (Biometrico biometico : biometricos) {
                	try {
                		InstanciaBiometrico dispositivo = LibreriaBiometrico.obtenerInstancia(
    	                        biometico.getNombre(), 
    	                        biometico.getTipo(), 
    	                        biometico.getIp(), 
    	                        biometico.getPuerto(), 
    	                        biometico.getCommKey()
    	                    );
	                    
	                    if (dispositivo.connect() && dispositivo != null) {
	                    	dispositivosConectados.add(dispositivo);
	                    } else {
	                        dispositivosFallidos.add("X " + biometico.getNombre() + " (IP: " + biometico.getIp() + ")");
	                    }
                	} catch (Exception e) {
                		System.err.println("Error al crear instancia: "+e.getMessage());
                		dispositivosFallidos.add("X " + biometico.getNombre() + " (IP: " + biometico.getIp() + ")");
                	}
                }

                if (dispositivosFallidos.isEmpty()) {
                    todosConectados = true;
                    
                    SwingUtilities.invokeLater(() -> {
                        VistaPrincipal ventana = new VistaPrincipal();
                        new AsistenciaControlador(ventana, dispositivosConectados, clienteBiometricos);
                        new EmpleadoBiometricoControlador(ventana, dispositivosConectados);
                        new EmpleadoControlador(ventana, dispositivosConectados, clienteBiometricos);          
                        new HuellaControlador(ventana, dispositivosConectados, clienteBiometricos);
                        ventana.setVisible(true);
                    });
                    
                } else {
                    StringBuilder mensajeError = new StringBuilder();
                    mensajeError.append("El sistema no puede iniciar. Los siguientes dispositivos no responden:\n\n");
                    
                    for (String error : dispositivosFallidos) {
                        mensajeError.append(error).append("\n");
                    }
                    
                    mensajeError.append("\nPor favor, verifique los cables de red y la energía eléctrica.\n¿Desea intentar la conexión nuevamente?");

                    final int[] opcion = new int[1];
                    try {
                        SwingUtilities.invokeAndWait(() -> {
                            Object[] opcionesBotones = {"Reintentar Conexión", "Salir del Sistema"};
                            opcion[0] = JOptionPane.showOptionDialog(null, 
                                    mensajeError.toString(), 
                                    "Error de Conectividad Biométrica", 
                                    JOptionPane.YES_NO_OPTION, 
                                    JOptionPane.ERROR_MESSAGE, 
                                    null, opcionesBotones, opcionesBotones[0]);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (opcion[0] != JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            }
		}).start();
	}

}
