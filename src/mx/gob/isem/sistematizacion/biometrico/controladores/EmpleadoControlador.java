package mx.gob.isem.sistematizacion.biometrico.controladores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import mx.gob.isem.sistematizacion.biometrico.InstanciaBiometrico;
import mx.gob.isem.sistematizacion.biometrico.User;
import mx.gob.isem.sistematizacion.biometrico.dao.BiometricoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.ConfiguracionDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.EmpleadoBiometricoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.EmpleadoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.HuellaDAO;
import mx.gob.isem.sistematizacion.biometrico.modelos.*;
import mx.gob.isem.sistematizacion.biometrico.utilidades.GestorDialogosUI;
import mx.gob.isem.sistematizacion.biometrico.utilidades.FuncionesRepetidas;
import mx.gob.isem.sistematizacion.biometrico.utilidades.FuncionesRepetidas.*;
import mx.gob.isem.sistematizacion.biometrico.vistas.VistaPrincipal;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.BiometricosPortType;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.ConsultarEmpleadosCambiosRequest;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.ConsultarEmpleadosCambiosResponse;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.Empleado;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.Huella;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.ListaEmpleados;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.ObjectFactory;

public class EmpleadoControlador {
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private VistaPrincipal vista;
	private List<InstanciaBiometrico> dispositivos;
	private BiometricoDAO biometricoDao;
	private ConfiguracionDAO configuracionDao;
	private EmpleadoBiometricoDAO empleadoBiometricoDao;
	private EmpleadoDAO empleadoDao;
	private HuellaDAO huellaDao;
	private BiometricosPortType servicio;
	private ObjectFactory factory;
	
	public EmpleadoControlador (VistaPrincipal vista, List<InstanciaBiometrico> dispostivos, BiometricosPortType servicio) {
		this.vista = vista;
		this.dispositivos = dispostivos;
		this.biometricoDao = new BiometricoDAO();
		this.configuracionDao = new ConfiguracionDAO();
		this.empleadoBiometricoDao = new EmpleadoBiometricoDAO();
		this.empleadoDao = new EmpleadoDAO();
		this.huellaDao = new HuellaDAO();
		this.servicio = servicio;
		this.factory = new ObjectFactory();
		inicializarEventos();
		refrescarTabla();
	}
	
	private void refrescarTabla() {
		vista.modeloTabla.setRowCount(0);
		List<EmpleadoLocal> empleados = empleadoDao.consultarEmpleados();
		if (empleados != null && !empleados.isEmpty()) {
			for (EmpleadoLocal empleado : empleados) {
				String nombreCompleto = empleado.getNombre() + " " + empleado.getPrimerApellido() + " " + empleado.getSegundoApellido();			    
			    Object[] fila = {
		    		empleado.getId(),
			        nombreCompleto,
			        empleado.getRfc(),
			        empleado.getConfigurado() ? "Configurado" : "Sin configurar",
			        empleado.getConfigurado() // El boolean oculto que apaga o prende el botón de configuración
			    };
			    vista.modeloTabla.addRow(fila);
			}
		}
	}

	private void inicializarEventos() {						
	    vista.btnConfigurar.addActionListener(new ActionListener() {			
	        @Override
	        public void actionPerformed(ActionEvent ae) {	            
	            int filaSeleccionada = vista.tablaPrincipal.getSelectedRow();	            
	            if (filaSeleccionada != -1) {
	                String idEmpleado = vista.tablaPrincipal.getValueAt(filaSeleccionada, 0).toString();
	                EmpleadoLocal empleado = empleadoDao.consultarEmpleado(idEmpleado);	                
	                if (empleado != null) {
	                    List<Biometrico> listaBiometricos = biometricoDao.consultarBiometricos();	 	                    
	                    if (listaBiometricos == null || listaBiometricos.isEmpty()) {
	                    	// Verificamos si tenemos biométricos registrados
	                        JOptionPane.showMessageDialog(vista, 
	                            "No hay dispositivos biométricos registrados en la base de datos.", 
	                            "Error", JOptionPane.ERROR_MESSAGE);
	                        return; // Cortamos la ejecución
	                    }
	                    // Ventana para seleccionar biométrico
	                    Biometrico biometricoSeleccionado = (Biometrico) JOptionPane.showInputDialog(
	                        vista,
	                        "Seleccione el dispositivo para configurar a " + empleado.getNombre() + ":",
	                        "Seleccionar Biométrico",
	                        JOptionPane.QUESTION_MESSAGE,
	                        null,
	                        listaBiometricos.toArray(),
	                        listaBiometricos.get(0)
	                    );	                    	                    
	                    if (biometricoSeleccionado != null) {
	                    	// Validamos si el usuario seleccionó un biométrico
	                    	// Mandamos a llamar a nuestro método para configurar un empleado
	                        int resultado = configurarEmpleado(empleado, biometricoSeleccionado); 	                        
	                        if (resultado == 1) {
	                            JOptionPane.showMessageDialog(vista, 
	                                "Empleado configurado correctamente en: " + biometricoSeleccionado.getNombre(), 
	                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
	                            refrescarTabla();
	                        } else if (resultado == -1) {
	                            JOptionPane.showMessageDialog(vista, 
	                                "Hubo un error al intentar configurar al empleado en el dispositivo.", 
	                                "Error", JOptionPane.ERROR_MESSAGE);
	                        }
	                    }	
	                } else {
	                	// La consulta en la base de datos no trajo ningún empleado como resultado
	                	JOptionPane.showMessageDialog(vista, 
                            "El empleado seleccionado no existe en la base de datos.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
	                }
	            }
	        }
	    });
	    
	    vista.btnCambiarPassword.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {	            
	            int filaSeleccionada = vista.tablaPrincipal.getSelectedRow();	            
	            if (filaSeleccionada != -1) {
	                String idEmpleado = vista.tablaPrincipal.getValueAt(filaSeleccionada, 0).toString();               	                
	                EmpleadoLocal empleado = empleadoDao.consultarEmpleado(idEmpleado);	                
	                if (empleado != null) {
	                    // Verificamos que el empleado ya esté configurado
	                    if (empleado.getConfigurado()) {
	                    	// Mandamos a llamar a nuestro método para cambiar el password
	                        int resultado = cambiarPassword(empleado);	                        
	                        if (resultado == 1) {
	                        	// El método se ejecutó con éxito
	                            JOptionPane.showMessageDialog(vista, 
	                                "Contraseña actualizada y sincronizada correctamente.", 
	                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
	                        } else if (resultado == -1) {
	                        	// Hubo un error en la ejecución del método
	                        	JOptionPane.showMessageDialog(vista, 
        			        		"Hubo un error al cambiar la contraseña. Inténtelo de nuevo.", 
        			        		"Error", JOptionPane.ERROR_MESSAGE);
	                        }	                        
	                    } else {
	                    	// El empleado no está configurado
	                        JOptionPane.showMessageDialog(vista, 
	                            "Este empleado aún no está configurado en los dispositivos. Utilice el botón 'Configurar' primero.", 
	                            "Acción no permitida", JOptionPane.WARNING_MESSAGE);
	                    }
	                } else {
	                	// La consulta en la base de datos no trajo ningún empleado como resultado
	                    JOptionPane.showMessageDialog(vista, 
                    		"El empleado seleccionado no existe en la base de datos.", 
	                        "Error", JOptionPane.ERROR_MESSAGE);
	                }
	            } else {
	                // Si el usuario presiona el botón sin seleccionar a nadie
	                JOptionPane.showMessageDialog(vista, 
	                    "Por favor, seleccione un empleado de la tabla principal.", 
	                    "Aviso", JOptionPane.WARNING_MESSAGE);
	            }
	        }
	    });
	    
		// Los parámetros es la hora en formato de 24 horas y los minutos
		programarRecoleccionEmpleadosDiaria(02, 30);	
	}
	
	private void programarRecoleccionEmpleadosDiaria(int hora, int minuto) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime proximaEjecucion = ahora.withHour(hora).withMinute(minuto).withSecond(0);
        // Programamos para el siguiente día
        if (ahora.isAfter(proximaEjecucion)) {
            proximaEjecucion = proximaEjecucion.plusDays(1);
        }
        // Ajuste para ejecutarse cada día
        long delayInicial = Duration.between(ahora, proximaEjecucion).toSeconds();
        scheduler.scheduleAtFixedRate(() -> {
        	obtenerEmpleadosServidor();
        }, delayInicial, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
        System.out.println("Programador iniciado. Próxima recolección: " + proximaEjecucion);
    }
	
	private void obtenerEmpleadosServidor() {
	    int maxIntentos = 3;
	    int intentoActual = 0;
	    boolean exito = false;
	    while (intentoActual < maxIntentos && !exito) {
	        intentoActual++;
	        try {
	            ConsultarEmpleadosCambiosRequest request = new ConsultarEmpleadosCambiosRequest();
	            List<EmpleadoLocal> empleadosLocales = empleadoDao.consultarEmpleados();
                ListaEmpleados empleados = factory.createListaEmpleados();
                for (EmpleadoLocal empleadoLocal : empleadosLocales) {
                    Empleado empleado = new Empleado();
                    empleado.setId(empleadoLocal.getId());
                    empleado.setNombre(empleadoLocal.getNombre());
                    empleado.setPrimerApellido(empleadoLocal.getPrimerApellido());
                    empleado.setSegundoApellido(empleadoLocal.getSegundoApellido());
                    empleado.setRfc(empleadoLocal.getRfc());
                    empleado.setRfid(empleadoLocal.getRfid());
                    empleados.getEmpleado().add(empleado);
                }
                request.setCentro(configuracionDao.consultarConfiguracion().getCentro());
                request.setEmpleados(empleados); 	        
                // Consumimos el Web Service
                ConsultarEmpleadosCambiosResponse response = servicio.consultarEmpleadosCambios(request);
                
                modificarEmpleadosLocal(response);
                exito = true;             
	        } catch (Exception e) {
	            System.err.println("Error de red al conectar con el servidor central: " + e.getMessage());
	            if (intentoActual < maxIntentos) {
	                System.out.println("Reintentando en 5 minutos...");
	                try { 
	                	Thread.sleep(TimeUnit.MINUTES.toMillis(1)); 
	                } catch (InterruptedException ie) { 
	                	Thread.currentThread().interrupt(); 
	                	break; 
	                }
	            } 
	        }
	    }
	}
	
	private void modificarEmpleadosLocal(ConsultarEmpleadosCambiosResponse response) {
		List<Empleado> empleados = response.getEmpleados().getEmpleado();
		if (!empleados.isEmpty()) {
			for (Empleado empleado : empleados) {
				if (Boolean.TRUE.equals(empleado.isEliminado())) {
					// Caso para las bajas
					borrarEmpleado(empleado.getId());
					continue;
				} else if (Boolean.TRUE.equals(empleado.isModificado())) {
					// Se modificó la información del empleado
					agregarEmpleado(
						empleado.getId(),
						empleado.getNombre(),
						empleado.getPrimerApellido(),
						empleado.getSegundoApellido(),
						empleado.getRfc(),
						empleado.getRfid(),
						null
					);
					continue;
				} else if (Boolean.TRUE.equals(empleado.isAgregado())) {
					// Se dió de alta al empleado
					Map<Integer, byte[]> huellasLocales = null;
					if (empleado.getHuellas() != null && empleado.getHuellas().getHuella() != null) {
						huellasLocales = convertirHuellasLocal(empleado.getHuellas().getHuella());
					}					
					agregarEmpleado(
						empleado.getId(), 
						empleado.getNombre(), 
						empleado.getPrimerApellido(), 
						empleado.getSegundoApellido(), 
						empleado.getRfc(), 
						empleado.getRfid(), 
						huellasLocales
					);
					continue;
				}
			}
			javax.swing.SwingUtilities.invokeLater(() -> {
				refrescarTabla();
			});
		}
	}
	
	private Map<Integer, byte[]> convertirHuellasLocal(List<Huella> huellas) {
		Map<Integer, byte[]> huellasMapa = new java.util.HashMap<>();
		if (huellas != null) {
			for (Huella huella : huellas) {
				huellasMapa.put(huella.getIndice(), huella.getTemplate());
			}
		}
		return huellasMapa;
	}
	
	private boolean agregarEmpleado(String id, String nombre, String primerApellido, String segundoApellido, String rfc, int rfid, Map<Integer, byte[]> huellas) {
		EmpleadoLocal empleado = empleadoDao.consultarEmpleado(id);
		boolean insertarEmpleado = false;		
		if (empleado == null) {
			// Si no hay ningun empleado en la base lo agregamos
	        empleado = new EmpleadoLocal();
	        empleado.setId(id);
	        empleado.setNombre(nombre);
	        empleado.setPrimerApellido(primerApellido);
	        empleado.setSegundoApellido(segundoApellido);
	        empleado.setRfc(rfc);
	        empleado.setRfid(rfid);
	        insertarEmpleado = empleadoDao.insertarEmpleado(empleado);
	    } else {
	        // Si hay un empleado en la base lo actualizamos
	        empleado.setNombre(nombre);
	        empleado.setPrimerApellido(primerApellido);
	        empleado.setSegundoApellido(segundoApellido);
	        empleado.setRfc(rfc);
	        empleado.setRfid(rfid);
	        insertarEmpleado = empleadoDao.modificarEmpleado(empleado);
	        actualizarEmpleadoEnBiometricos(empleado);
	    }
		if (insertarEmpleado) {
			// Procedemos si la inserción en la base de datos fue correcta
			if (huellas != null && !huellas.isEmpty()) {
				// Verificamos si las huellas traen contenido para agregar en la base de datos
				boolean huellasGuardadas = true;
				for (Map.Entry<Integer, byte[]> entry : huellas.entrySet()) {
					// Ciclo para insertar cada huella en la base de datos
					int indiceDedo = entry.getKey();
					byte[] template = entry.getValue();
					HuellaLocal huellaExistente = huellaDao.consultarHuella(empleado, indiceDedo);	
					boolean insertarHuella;
					if (huellaExistente == null) {
						//La huella es nueva
						HuellaLocal huella = new HuellaLocal(empleado, indiceDedo, template);
						insertarHuella = huellaDao.insertarHuella(huella);
					} else {
						//La huella ya existe, la sobreescribimos
						huellaExistente.setTemplate(template);
						insertarHuella = huellaDao.modificarHuella(huellaExistente);
					}
					huellasGuardadas = huellasGuardadas && insertarHuella;
				}
				return insertarEmpleado && huellasGuardadas;
			} 
		}
		return insertarEmpleado;
	}		
	
	private void actualizarEmpleadoEnBiometricos(EmpleadoLocal empleado) {
	    List<Biometrico> biometricos = biometricoDao.consultarBiometricos();
	    List<HuellaLocal> huellas = huellaDao.consultarHuellasEmpleado(empleado);
	    for (InstanciaBiometrico dispositivo : dispositivos) {
	        Biometrico biometricoActual = FuncionesRepetidas.buscarBiometricoAsociado(dispositivo, biometricos);
	        if (biometricoActual == null) continue;
	        EmpleadoBiometrico empleadoBiometrico = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometricoActual);	        
	        if (empleadoBiometrico != null) {
	            try {
	                User usuarioActualizado = new User();
	                usuarioActualizado.id = empleadoBiometrico.getIdGenerado();
	                usuarioActualizado.serial = empleadoBiometrico.getSerial();	                
	                // Actualizamos con los nuevos datos recibidos del servidor
	                usuarioActualizado.name = prepararNombreBiometrico(empleado.getNombre(), empleado.getPrimerApellido(), empleado.getSegundoApellido());
	                usuarioActualizado.cardNumber = empleado.getRfid();
	                usuarioActualizado.password = empleado.getPassword() != null ? empleado.getPassword() : "";
	                ModoVerificacion modoDestino = obtenerModoPorCodigo(empleadoBiometrico.getModoVerificacion());
	                // Hacemos el upsert en el aparato
	                boolean exito = EmpleadoBiometricoControlador.sincronizarUsuarioADispositivo(dispositivo, usuarioActualizado, huellas, modoDestino);	                
	                if (!exito) {
	                    System.err.println("El SDK rechazó la actualización para el dispositivo: " + biometricoActual.getNombre());
	                }
	            } catch (Exception e) {
	                System.err.println("Error físico al intentar actualizar el dispositivo " + biometricoActual.getNombre() + ": " + e.getMessage());
	            }
	        }
	    }
	}
	
	private int configurarEmpleado(EmpleadoLocal empleado, Biometrico biometricoSeleccionado) {
		String passwordGeneral = "";
		ModoVerificacion modoFinal = ModoVerificacion.SOLO_PASSWORD;
		// Lista de lo que vamos haciendo
		List<Biometrico> dispositivosModificadosConExito = new ArrayList<>();
	    List<HuellaLocal> huellasNuevasEnroladas = new ArrayList<>();
		// Primero vamos a configurar el dispositivo en un biometrico determinado
		InstanciaBiometrico maestro = null;
		for (InstanciaBiometrico dispositivo : dispositivos) {
			// Coincidimos el objeto de aplicacion con el objeto de red
			if (dispositivo.obtenerIdentificador().equals(biometricoSeleccionado.getNombre())) {
				maestro = dispositivo;
				break;
			}
		}
		if (maestro == null) {
	        JOptionPane.showMessageDialog(vista, 
        		"No se encontró la conexión con el dispositivo maestro.", 
        		"Error", JOptionPane.ERROR_MESSAGE);
	        return -1;
	    }
		// Generamos el id que el empleado tendrá en determinado dispositivo
		int serial = empleadoBiometricoDao.generarIdEmpleado(empleado, biometricoSeleccionado);
		EmpleadoBiometrico empleadoBiometrico = new EmpleadoBiometrico(
			empleado,
			biometricoSeleccionado,			
			String.valueOf(serial),
			serial,
			0
		);
		// Conversión de los empleados a los objetos de la librería
		User usuario = new User();
		usuario.serial = empleadoBiometrico.getSerial();
		usuario.password = passwordGeneral;
		usuario.cardNumber = empleado.getRfid();
		usuario.id = empleadoBiometrico.getIdGenerado();
		usuario.name = prepararNombreBiometrico(empleado.getNombre(), empleado.getPrimerApellido(), empleado.getSegundoApellido());
		empleadoBiometricoDao.insertarEmpleadoBiometrico(empleadoBiometrico);
		// Una vez guardado en la BD lo agregamos a la lista en caso de rollback
		dispositivosModificadosConExito.add(biometricoSeleccionado); 
		List<HuellaLocal> huellas = huellaDao.consultarHuellasEmpleado(empleado);	
		try {
			//Verificamos si el dispositivo soporta autenticación con credencial y si el empleado tiene asignada una credencia válida
			if (biometricoSeleccionado.getTieneRfid() && empleado.getRfid() > 0) {
				//El dispositivo si soporta credencial y el empleado si tiene credencial válida
				if (huellas != null && !huellas.isEmpty()) {
					//El usuario si tiene huellas en la BD
					modoFinal = ModoVerificacion.HUELLA_CREDENCIAL;
				} else {				
					//El usuario no tiene huellas registradas, vamos a intentar que enrole una
					if (maestro.addUser(usuario)) {
						// Metodo para seleccionar un dedo de la mano					
						int indiceNuevo = GestorDialogosUI.solicitarIndice(vista);
						if (indiceNuevo != -1) {
							boolean enrolamiento = GestorDialogosUI.procesarEnrolamiento(vista, empleadoBiometrico.getIdGenerado(), indiceNuevo, maestro);
							if (enrolamiento) {
								// El usuario si pudo enrolar su huella
								byte[] templateNuevo = maestro.getFingerprintTemplate(empleadoBiometrico.getSerial(), indiceNuevo);							
								HuellaLocal huella = new HuellaLocal(empleado, indiceNuevo, templateNuevo);
								huellaDao.insertarHuella(huella);
								huellas.add(huella);
								huellasNuevasEnroladas.add(huella);
								modoFinal = ModoVerificacion.HUELLA_CREDENCIAL;
							} else {
								//El usuario no enroló su huella o no se pudo
								passwordGeneral = GestorDialogosUI.solicitarPassword(vista);
								if (passwordGeneral != null) {
									empleado.setPassword(passwordGeneral);
									modoFinal = ModoVerificacion.CREDENCIAL_PASSWORD;
									usuario.password = empleado.getPassword();									
								} else {
									revertirConfiguracionMasiva(empleado, dispositivosModificadosConExito, huellasNuevasEnroladas);
	                                return 0;
								}
							}
						} else {
							revertirConfiguracionMasiva(empleado, dispositivosModificadosConExito, huellasNuevasEnroladas);
	                        return -1;
						}
					}
				}			
			} else {
				// El dispositivo no soporta credencial o el empleado no tiene credencial válida
				passwordGeneral = GestorDialogosUI.solicitarPassword(vista);
				if (passwordGeneral != null) {
					empleado.setPassword(passwordGeneral); 			
					usuario.password = empleado.getPassword();
					empleadoDao.modificarEmpleado(empleado);
					if (huellas != null && !huellas.isEmpty()) {
						// El usuario si tiene huellas en la BD
						modoFinal = ModoVerificacion.HUELLA_PASSWORD;
					} else {
						// El usuario no tiene huellas registradas, vamos a intentar que enrole una
						if (maestro.addUser(usuario)) {
							// Lanzamos ventana para solicitar índice				
							int indiceNuevo = GestorDialogosUI.solicitarIndice(vista);
							if (indiceNuevo != -1) {
								// Lanzamos ventana para enrolar huella
								boolean enrolamiento = GestorDialogosUI.procesarEnrolamiento(vista, empleadoBiometrico.getIdGenerado(), indiceNuevo, maestro);
								if (enrolamiento) {
									// El usuario si pudo enrolar su huella
									byte[] templateNuevo = maestro.getFingerprintTemplate(empleadoBiometrico.getSerial(), indiceNuevo);							
									HuellaLocal huella = new HuellaLocal(empleado, indiceNuevo, templateNuevo);
									huellaDao.insertarHuella(huella);
									huellas.add(huella);
									huellasNuevasEnroladas.add(huella);
									modoFinal = ModoVerificacion.HUELLA_PASSWORD;
								} else {
									// El usuario no enroló su huella o no se pudo									
                                    modoFinal = ModoVerificacion.SOLO_PASSWORD;								
								}
							} else {
								revertirConfiguracionMasiva(empleado, dispositivosModificadosConExito, huellasNuevasEnroladas);
	                            return 0;
							}
						}
					}
				} else {
					revertirConfiguracionMasiva(empleado, dispositivosModificadosConExito, huellasNuevasEnroladas);
	                return 0;
				}			
			}
			empleadoBiometrico.setModoVerificacion(modoFinal.getCodigo());
			empleadoBiometricoDao.actualizarModoVerificacion(empleado, biometricoSeleccionado, modoFinal.getCodigo());
			if (!EmpleadoBiometricoControlador.sincronizarUsuarioADispositivo(maestro, usuario, huellas, modoFinal)) {
	            JOptionPane.showMessageDialog(vista, 
            		"Fallo al sincronizar con el dispositivo maestro. Inténtelo de nuevo.", 
            		"Error", JOptionPane.ERROR_MESSAGE);
	            revertirConfiguracionMasiva(empleado, dispositivosModificadosConExito, huellasNuevasEnroladas);
	            return -1;
	        }
		}  catch (Exception e) {
	        System.err.println("Excepción durante captura en maestro: " + e.getMessage());
	        revertirConfiguracionMasiva(empleado, dispositivosModificadosConExito, huellasNuevasEnroladas);
	        return -1;
	    }
		
		//Si se agregó el usuario al dispositivo mandamos la misma información a cada dispositivo
		List<Biometrico> biometricos = biometricoDao.consultarBiometricos();
		for (InstanciaBiometrico dispositivo : dispositivos) {
			if (dispositivo == maestro) continue;
			Biometrico biometricoActual = FuncionesRepetidas.buscarBiometricoAsociado(dispositivo, biometricos);			
			//Generamos un serial distinto para cada dispositivo
			serial = empleadoBiometricoDao.generarIdEmpleado(empleado, biometricoActual);
			ModoVerificacion modoDestino;
			// Verificamos si se configura con credencial o no
	        if (biometricoActual.getTieneRfid() && empleado.getRfid() > 0) {
	            modoDestino = (huellas != null && !huellas.isEmpty()) ? ModoVerificacion.HUELLA_CREDENCIAL : ModoVerificacion.CREDENCIAL_PASSWORD;
	        } else {
	            modoDestino = (huellas != null && !huellas.isEmpty()) ? ModoVerificacion.HUELLA_PASSWORD : ModoVerificacion.SOLO_PASSWORD;
	        }
			empleadoBiometrico.setBiometrico(biometricoActual);
			empleadoBiometrico.setIdGenerado(String.valueOf(serial));
			empleadoBiometrico.setSerial(serial);
			empleadoBiometrico.setModoVerificacion(modoDestino.getCodigo());
			usuario.id = empleadoBiometrico.getIdGenerado();
			usuario.serial = empleadoBiometrico.getSerial();
			boolean sincronizado = false;
			while (!sincronizado) {
				// Ciclo para volver a configurar en un dispositivo por si hubo un error
				try {
					empleadoBiometricoDao.insertarEmpleadoBiometrico(empleadoBiometrico);
					sincronizado = EmpleadoBiometricoControlador.sincronizarUsuarioADispositivo(dispositivo, usuario, huellas, modoDestino);
					if (sincronizado) {
						dispositivosModificadosConExito.add(biometricoActual);
					} else {
	                    throw new Exception("El dispositivo rechazó la configuración.");
	                }
				} catch (Exception e) {
	                // Falló la sincronización. Deshacemos el guardado local
	                empleadoBiometricoDao.eliminarEmpleadoBiometrico(empleado, biometricoActual);	                
	                // Preguntamos al usuario si quiere reintentarlo
	                int respuesta = JOptionPane.showConfirmDialog(vista, 
	                    "Error al enviar datos al dispositivo: " + biometricoActual.getNombre() + ".\n" +
	                    "¿Desea reintentar la conexión con este equipo?", 
	                    "Fallo de Sincronización", 
	                    JOptionPane.YES_NO_OPTION, 
	                    JOptionPane.WARNING_MESSAGE);	                
	                if (respuesta == JOptionPane.YES_OPTION) {
	                    System.out.println("Reintentando conexión con: " + biometricoActual.getNombre());
	                } else {
	                    // El usuario canceló. Limpiamos todo el sistema.
	                    revertirConfiguracionMasiva(empleado, dispositivosModificadosConExito, huellasNuevasEnroladas);
	                    JOptionPane.showMessageDialog(vista, 
                    		"Operación abortada. Se han revertido los cambios en todos los equipos.",
                    		"Rollback", JOptionPane.INFORMATION_MESSAGE);
	                    return -1;
	                }
	            }
			}
		}
		// Si llegamos a este punto todo salió bien, si podemos modificar el campo configurado en la BD
		// Devolvemos éxito (1), en caso contrario devolvemos error (-1)
		return empleadoDao.configurarEmpleado(empleado) ? 1 : -1;
	}
	
	private int cambiarPassword(EmpleadoLocal empleado) {		
	    String passwordNuevo = GestorDialogosUI.solicitarPassword(vista);	    
	    if (passwordNuevo == null) return 0; // El usuario dió en cancelar a la ventana de ingresar contraseña 
	    // Guardamos datos necesarios para el rollback
	    String passwordViejo = empleado.getPassword() != null ? empleado.getPassword() : "";
	    List<InstanciaBiometrico> dispositivosAfectados = new ArrayList<>();
	    List<HuellaLocal> huellas = huellaDao.consultarHuellasEmpleado(empleado);
	    try {
	        // Actualizamos la contraseña en la BD
	        empleado.setPassword(passwordNuevo);
	        boolean dbActualizada = empleadoDao.modificarEmpleado(empleado);	        
	        if (!dbActualizada) {
	        	// Si no se actualizó la BD lanzamos una excepción
	            throw new Exception("El DAO no pudo actualizar el empleado en la base de datos.");
	        }
	        // Realizamos el cambio en todos los biométricos
	        List<Biometrico> biometricos = biometricoDao.consultarBiometricos();	        
	        for (InstanciaBiometrico dispositivo : dispositivos) { 
	            Biometrico biometricoActual = FuncionesRepetidas.buscarBiometricoAsociado(dispositivo, biometricos);   
	            if (biometricoActual == null) continue;	            
	            // Cruzamos la información del empleado con el biométrico
	            EmpleadoBiometrico empleadoBiometrico = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometricoActual);	            
	            if (empleadoBiometrico != null) {
	                User usuarioActualizado = new User();
	                usuarioActualizado.id = empleadoBiometrico.getIdGenerado();
	                usuarioActualizado.serial = empleadoBiometrico.getSerial();
	                usuarioActualizado.name = prepararNombreBiometrico(empleado.getNombre(), empleado.getPrimerApellido(), empleado.getSegundoApellido());
	                usuarioActualizado.password = passwordNuevo; 
	                usuarioActualizado.cardNumber = empleado.getRfid();
	                // Obtenemos su modo de verificación con el que estaba trabajando
	                ModoVerificacion modoDestino = obtenerModoPorCodigo(empleadoBiometrico.getModoVerificacion());
	                boolean sincronizado = false;
	                while (!sincronizado) {
	                	// Ciclo para reintentar en caso de error
	                    try {
	                        // Sincronizamos usando el modo específico de este hardware
	                        boolean exitoRed = EmpleadoBiometricoControlador.sincronizarUsuarioADispositivo(dispositivo, usuarioActualizado, huellas, modoDestino);	                        
	                        if (!exitoRed) {
	                            throw new Exception("El SDK devolvió false al sincronizar el usuario.");
	                        }	                        
	                        sincronizado = true;
	                        dispositivosAfectados.add(dispositivo);	                        
	                    } catch (Exception ex) {
	                        int respuesta = JOptionPane.showConfirmDialog(vista, 
	                            "Error de red al actualizar la contraseña en el dispositivo: " + biometricoActual.getNombre() + ".\n" +
	                            "¿Desea reintentar la conexión?", 
	                            "Fallo de Sincronización", 
	                            JOptionPane.YES_NO_OPTION, 
	                            JOptionPane.WARNING_MESSAGE);	                        
	                        if (respuesta != JOptionPane.YES_OPTION) {
	                            revertirCambioPassword(empleado, passwordViejo, dispositivosAfectados, huellas);
	                            JOptionPane.showMessageDialog(vista, 
	                                "Operación abortada. Se restauró la contraseña anterior en la base de datos y en los equipos.", 
	                                "Rollback Completado", JOptionPane.INFORMATION_MESSAGE);
	                            return -1; 
	                        }
	                    }
	                }
	            } else {
	            	revertirCambioPassword(empleado, passwordViejo, dispositivosAfectados, huellas);
                    JOptionPane.showMessageDialog(vista, 
                        "No existe registro de la base de datos del empleado en el biométrico " + biometricoActual.getNombre(), 
                        "Rollback Completado", JOptionPane.INFORMATION_MESSAGE);
                    return -1; 
	            }
	        }
	        return 1; // Éxito total
	    } catch (Exception e) {
	        System.err.println("Error crítico guardando en BD: " + e.getMessage());
	        revertirCambioPassword(empleado, passwordViejo, dispositivosAfectados, huellas);
	        JOptionPane.showMessageDialog(vista, 
	            "Hubo un error interno. No se guardaron los cambios de contraseña.", 
	            "Error", JOptionPane.ERROR_MESSAGE);
	        return -1;
	    }		
	}
	
	private boolean borrarEmpleado(String id) {
		EmpleadoLocal empleado = empleadoDao.consultarEmpleado(id);		
		boolean ok = true;
		List<Biometrico> biometricos = biometricoDao.consultarBiometricos();
		for (InstanciaBiometrico dispositivo : dispositivos) {
			Biometrico biometricoActual = FuncionesRepetidas.buscarBiometricoAsociado(dispositivo, biometricos);			
			EmpleadoBiometrico empleadoBiometrico = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometricoActual);
			if (empleadoBiometrico == null) {
				continue;
			}
			try {
				if (dispositivo.deleteUser(empleadoBiometrico.getSerial())) {
					empleadoBiometricoDao.eliminarEmpleadoBiometrico(empleado, biometricoActual);
				} else ok = false;
			} catch (IllegalStateException e) {
				if (e.getMessage().equals("ERROR_CONEXION"))
					System.err.println("Hubo un error en la conexión con el dispositivo");
				if (e.getMessage().equals("ERROR_REFRESH"))
					System.err.println("Hubo un error al actualizar el dispositivo");
				ok = false;
			} catch (Exception e) {
				System.err.println("Ocurrió un error inesperado: "+e.getMessage());
				ok = false;
			}
		}
		huellaDao.eliminarHuellasEmpleado(empleado);
		boolean empleadoEliminado = empleadoDao.eliminarEmpleado(id);
		return empleadoEliminado && ok;
	}
	
	private void revertirConfiguracionMasiva(EmpleadoLocal empleado, List<Biometrico> dispositivosAfectados, List<HuellaLocal> huellasNuevas) {
	    System.out.println("Iniciando ROLLBACK masivo para el empleado: " + empleado.getNombre());	    
	    // Se borran las huellas nuevas en la BD
	    if (huellasNuevas != null && !huellasNuevas.isEmpty()) {
	        for (HuellaLocal huella : huellasNuevas) {
	            huellaDao.eliminarHuellaEmpleado(huella);
	        }
	    }
	    // Recorremos los dispositivos que fueron afectados
	    for (Biometrico biometrico : dispositivosAfectados) {
	        EmpleadoBiometrico relacion = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometrico);
	        if (relacion != null) {
	            // Buscamos la instancia de red de este dispositivo
	            for (InstanciaBiometrico dispositivo : dispositivos) {
	                if (dispositivo.obtenerIdentificador().equals(biometrico.getNombre())) {
	                    try {
	                        // Borramos al usuario del dispositivo
	                    	dispositivo.deleteUser(relacion.getSerial());
	                    } catch (Exception e) {
	                        System.err.println("No se pudo borrar del dispositivo físico durante el rollback: " + biometrico.getNombre());
	                    }
	                    break;
	                }
	            }
	            // Borramos al empleado referenciado a un biométrico en la BD
	            empleadoBiometricoDao.eliminarEmpleadoBiometrico(empleado, biometrico);
	        }
	    }	    
	    // Le quitamos la configuración al empleado en la BD
	    empleado.setConfigurado(false);
	    empleadoDao.modificarEmpleado(empleado);	    
	    System.out.println("Rollback finalizado. El sistema quedó limpio.");
	}
	
	private void revertirCambioPassword(EmpleadoLocal empleado, String passwordViejo, List<InstanciaBiometrico> dispositivosAfectados, List<HuellaLocal> huellas) {
	    System.out.println("Iniciando ROLLBACK de contraseña para: " + empleado.getNombre());
	    // Restauramos la contraseña en la BD
	    empleado.setPassword(passwordViejo);
	    empleadoDao.modificarEmpleado(empleado);
	    // Restauramos los dispositivos que fueron afectados
	    for (InstanciaBiometrico dispositivo : dispositivosAfectados) {
	        Biometrico biometricoAsociado = null;
	        for (Biometrico biometrico : biometricoDao.consultarBiometricos()) {
	            if (biometrico.getNombre().equals(dispositivo.obtenerIdentificador())) {
	            	biometricoAsociado = biometrico; 
	                break;
	            }
	        }	        
	        if (biometricoAsociado != null) {
	            EmpleadoBiometrico empleadoBiometrico = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometricoAsociado);
	            if (empleadoBiometrico != null) {
	                try {
	                    User usuarioRestaurado = new User();
	                    usuarioRestaurado.id = empleadoBiometrico.getIdGenerado();
	                    usuarioRestaurado.serial = empleadoBiometrico.getSerial();
	                    usuarioRestaurado.name = prepararNombreBiometrico(empleado.getNombre(), empleado.getPrimerApellido(), empleado.getSegundoApellido());
	                    usuarioRestaurado.password = passwordViejo;
	                    usuarioRestaurado.cardNumber = empleado.getRfid();
	                    // Rescatamos su modo original de la BD
	                    ModoVerificacion modoOriginal = obtenerModoPorCodigo(empleadoBiometrico.getModoVerificacion());
	                    EmpleadoBiometricoControlador.sincronizarUsuarioADispositivo(dispositivo, usuarioRestaurado, huellas, modoOriginal);	                    
	                } catch (Exception e) {
	                    System.err.println("Error crítico al restaurar la contraseña en el dispositivo: " + dispositivo.obtenerIdentificador());
	                }
	            }
	        }
	    }
	    System.out.println("Rollback de contraseña finalizado con éxito.");
	}
	
	private ModoVerificacion obtenerModoPorCodigo(int codigo) {
	    for (ModoVerificacion modo : ModoVerificacion.values()) {
	        if (modo.getCodigo() == codigo) {
	            return modo;
	        }
	    }
	    return ModoVerificacion.SOLO_PASSWORD; // Valor seguro por defecto en caso de datos corruptos
	}
	
	private String prepararNombreBiometrico(String nombre, String primerApellido, String segundoApellido) {
	    // Manejamos los nulos
	    String strNombre = (nombre != null) ? nombre.trim() : "";
	    String strPrimerApellido = (primerApellido != null) ? primerApellido.trim() : "";
	    String strSegundoApellido = (segundoApellido != null) ? segundoApellido.trim() : "";
	    StringBuilder nombreCompleto = new StringBuilder();

	    if (!strNombre.isEmpty()) {
	        nombreCompleto.append(strNombre);
	    }

	    if (!strPrimerApellido.isEmpty()) {
	        if (nombreCompleto.length() > 0) nombreCompleto.append(" ");
	        nombreCompleto.append(strPrimerApellido);
	    }

	    if (!strSegundoApellido.isEmpty()) {
	        if (nombreCompleto.length() > 0) nombreCompleto.append(" ");
	        nombreCompleto.append(strSegundoApellido);
	    }	    
	    // Normalizamos caracteres y quitamos acentos y ñ
	    String resultado = Normalizer.normalize(nombreCompleto.toString(), Normalizer.Form.NFD)
	            .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");	            
	    // Convertimos a mayúsculas
	    resultado = resultado.toUpperCase();
	    // En caso de exceder los 24 bytes cortamos
	    if (resultado.length() > 24) {
	        resultado = resultado.substring(0, 24);
	    }
	    return resultado;
	}
	
}
