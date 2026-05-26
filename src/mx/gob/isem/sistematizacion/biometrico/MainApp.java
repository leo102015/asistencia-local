package mx.gob.isem.sistematizacion.biometrico;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import mx.gob.isem.sistematizacion.biometrico.controladores.AsistenciaControlador;
import mx.gob.isem.sistematizacion.biometrico.controladores.EmpleadoBiometricoControlador;
import mx.gob.isem.sistematizacion.biometrico.controladores.EmpleadoControlador;
import mx.gob.isem.sistematizacion.biometrico.controladores.HuellaControlador;
import mx.gob.isem.sistematizacion.biometrico.dao.BiometricoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.ConfiguracionDAO;
import mx.gob.isem.sistematizacion.biometrico.modelos.Biometrico;
import mx.gob.isem.sistematizacion.biometrico.modelos.Configuracion;
import mx.gob.isem.sistematizacion.biometrico.modelos.SesionSistema;
import mx.gob.isem.sistematizacion.biometrico.utilidades.FuncionesRepetidas;
import mx.gob.isem.sistematizacion.biometrico.utilidades.GestorDialogosUI;
import mx.gob.isem.sistematizacion.biometrico.vistas.VistaPrincipal;
import mx.gob.isem.sistematizacion.biometrico.ws.ClienteSistematizacionWS;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.BiometricosPortType;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.ProbarConexionRequest;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.ProbarConexionResponse;

public class MainApp {
	
	public static List<InstanciaBiometrico> dispositivosConectados = new ArrayList<>();
		
	public static void main(String[] args) {
		iniciarValidacion();		
	}
	
	
	private static void iniciarValidacion() {				
		new Thread(() -> {
			try {
				// Configuración de SSL
				System.setProperty("javax.net.ssl.trustStore", "local_truststore.p12");
                System.setProperty("javax.net.ssl.trustStorePassword", "Is3mSist3mat1z4..");
                System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        // Aquí autorizamos explícitamente a localhost
                        if (hostname.equals("localhost") || hostname.equals("127.0.0.1")) {
                            return true;
                        }
                        // if (hostname.equals("192.168.1.100")) return true;
                        
                        return false;
                    }
                });
                // Configuración para el login inicial
				ConfiguracionDAO configuracionDao = new ConfiguracionDAO();
				Configuracion configuracion = configuracionDao.consultarConfiguracion();
				if (configuracion == null || configuracion.getCentro() == null) {
					GestorDialogosUI.mostrarErrorCritico("La base de datos local no cuenta con la configuración de la unidad.\nEl sistema se cerrará.");
					System.exit(0);
				}
				ClienteSistematizacionWS clienteBiometricos = new ClienteSistematizacionWS();
				// Se dan 2 intentos para ingresar la contraseña 
				boolean autenticado = false;
				int intentosLogin = 0;
				while (!autenticado) {
					String passwordPlano = GestorDialogosUI.mostrarDialogoPassword(configuracion.getNombre(), configuracion.getCentro());					
					// Si el usuario presiona "Cancelar" o cierra la ventana, abortamos el inicio
					if (passwordPlano == null) {
						System.out.println("Inicio de aplicación cancelado por el usuario.");
						System.exit(0);
					}
					
					if (passwordPlano.trim().isEmpty()) {
						JOptionPane.showMessageDialog(null, "La contraseña no puede estar vacía.", "Aviso", JOptionPane.WARNING_MESSAGE);
						continue;
					}
					// Variable que contiene el hash antes de guardarla en RAM
					String hashCifrado = FuncionesRepetidas.generarSHA256(passwordPlano);
					// Guardamos el hash en memoria mientras esté en ejecución la aplicación
					SesionSistema.getInstancia().setCentro(configuracion.getCentro());
					SesionSistema.getInstancia().setHashPassword(hashCifrado);

					try {
						System.out.println("Validando credenciales con el Servidor Central...");
						
						// Invocamos el servicio que valida la contraseña rápidamente
						ProbarConexionRequest request = new ProbarConexionRequest();
						request.setPing("PING");						
						BiometricosPortType servicio = clienteBiometricos.obtenerPuerto(); // o obtenerPuertoFresco() según lo nombraras
						ProbarConexionResponse response = servicio.probarConexion(request);
						if (response != null && response.isExito()) {
							autenticado = true; 
						}
						
					} catch (SOAPFaultException soapEx) {
						// El servidor central rechazó el hash por credenciales incorrectas
						SesionSistema.getInstancia().cerrarSesion();
						intentosLogin++;
						JOptionPane.showMessageDialog(null, "Contraseña incorrecta de la unidad. Inténtelo de nuevo.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
						
						if (intentosLogin >= 3) {
							GestorDialogosUI.mostrarErrorCritico("Se alcanzó el límite de intentos permitidos.\nLa aplicación se cerrará.");
							System.exit(0);
						}
					} catch (WebServiceException redEx) {
						// El servidor está caído o no hay internet en el centro
						SesionSistema.getInstancia().cerrarSesion();
						GestorDialogosUI.mostrarErrorCritico("No se pudo establecer comunicación con el Servidor Central.\nVerifique su conexión de red.");
						System.exit(0);
					}
				}				
				// Procedemos a inicializar los dispositivos verificando que estén en red
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
			} catch (Exception e) {
				System.err.println("Error grave en el arranque de la aplicación: " + e.getMessage());
				e.printStackTrace();
			}				
		}).start();
	}

}
