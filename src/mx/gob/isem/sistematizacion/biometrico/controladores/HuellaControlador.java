package mx.gob.isem.sistematizacion.biometrico.controladores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import mx.gob.isem.sistematizacion.biometrico.InstanciaBiometrico;
import mx.gob.isem.sistematizacion.biometrico.User;
import mx.gob.isem.sistematizacion.biometrico.utilidades.FuncionesRepetidas;
import mx.gob.isem.sistematizacion.biometrico.utilidades.FuncionesRepetidas.*;
import mx.gob.isem.sistematizacion.biometrico.dao.BiometricoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.EmpleadoBiometricoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.EmpleadoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.HuellaDAO;
import mx.gob.isem.sistematizacion.biometrico.modelos.Biometrico;
import mx.gob.isem.sistematizacion.biometrico.modelos.EmpleadoLocal;
import mx.gob.isem.sistematizacion.biometrico.modelos.EmpleadoBiometrico;
import mx.gob.isem.sistematizacion.biometrico.modelos.HuellaLocal;
import mx.gob.isem.sistematizacion.biometrico.utilidades.GestorDialogosUI;
import mx.gob.isem.sistematizacion.biometrico.vistas.VistaPrincipal;
import mx.gob.isem.sistematizacion.biometrico.ws.ClienteSistematizacionWS;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.BiometricosPortType;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.Huella;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.SincronizarHuellaRequest;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.SincronizarHuellaResponse;

public class HuellaControlador {
	private VistaPrincipal vista;
	private List<InstanciaBiometrico> dispositivos;
	private EmpleadoDAO empleadoDao;
    private HuellaDAO huellaDao;
    private BiometricoDAO biometricoDao;
    private EmpleadoBiometricoDAO empleadoBiometricoDao;
    private ClienteSistematizacionWS clienteWS;
	
	public HuellaControlador(VistaPrincipal vista, List<InstanciaBiometrico> dispositivos, ClienteSistematizacionWS clienteWS) {
		this.vista = vista;
		this.dispositivos = dispositivos;
		this.empleadoDao = new EmpleadoDAO();
        this.huellaDao = new HuellaDAO();
        this.biometricoDao = new BiometricoDAO();
        this.empleadoBiometricoDao = new EmpleadoBiometricoDAO();
        this.clienteWS = clienteWS;
		inicializarEventos();
	}
	
	private void inicializarEventos() {
		vista.btnEditarHuella.addActionListener(new ActionListener() {			
	        @Override
	        public void actionPerformed(ActionEvent ae) {
	            int filaSeleccionada = vista.tablaPrincipal.getSelectedRow();	            
	            if (filaSeleccionada != -1) {
	                String idEmpleado = vista.tablaPrincipal.getValueAt(filaSeleccionada, 0).toString();
	                EmpleadoLocal empleado = empleadoDao.consultarEmpleado(idEmpleado);	                
	                // Validemos que el empleado ya esté configurado
	                if (!empleado.getConfigurado()) {
	                    JOptionPane.showMessageDialog(vista, 
	                        "Este empleado aún no está configurado en los dispositivos. Utilice el botón 'Configurar' primero.", 
	                        "Acción no permitida", JOptionPane.WARNING_MESSAGE);
	                    return;
	                }	                
	                List<Biometrico> listaBiometricos = biometricoDao.consultarBiometricos();
	                // Ventana para seleccionar dispositivo
	                Biometrico biometricoSeleccionado = (Biometrico) JOptionPane.showInputDialog(
	                    vista,
	                    "¿En qué dispositivo el empleado '" + empleado.getNombre() + "' colocará su huella nueva?",
	                    "Seleccionar Lector Biométrico",
	                    JOptionPane.QUESTION_MESSAGE,
	                    null,
	                    listaBiometricos.toArray(),
	                    listaBiometricos.get(0)
	                );	                
	                if (biometricoSeleccionado != null) {
	                    HuellaLocal huella = editarHuella(empleado, biometricoSeleccionado);
	                    if (huella != null) {
                            System.out.println("Enviando nueva plantilla biométrica a Servidor Central...");
                            boolean exitoServidor = enviarHuellaAlServidor(huella);	                    
		                    if (exitoServidor) {
	                            JOptionPane.showMessageDialog(vista, 
	                    		    "Huella actualizada en los equipos locales y sincronizada con el Servidor Central.", 
	                    		    "Éxito", JOptionPane.INFORMATION_MESSAGE);
	                        } else {
	                            JOptionPane.showMessageDialog(vista, 
	                    		    "La huella se guardó en los equipos del centro, pero hubo un error de red con el Servidor Central.", 
	                    		    "Aviso de Red", JOptionPane.WARNING_MESSAGE);
	                        }
	                    }
	                }
	            } else {
	                 JOptionPane.showMessageDialog(vista, 
	            		 "Por favor seleccione un empleado de la tabla.", 
	            		 "Aviso", JOptionPane.WARNING_MESSAGE);
	            }
	        }
        });
		
		vista.btnEliminarHuella.addActionListener(new ActionListener() {			
	        @Override
	        public void actionPerformed(ActionEvent ae) {
			    int fila = vista.tablaPrincipal.getSelectedRow();
			    if (fila != -1) {
			        String id = vista.tablaPrincipal.getValueAt(fila, 0).toString();
			        EmpleadoLocal empleado = empleadoDao.consultarEmpleado(id);
			        // Validamos que tenga huellas asignadas en la base de datos
			        List<HuellaLocal> huellasExistentes = huellaDao.consultarHuellasEmpleado(empleado);
			        if (huellasExistentes == null || huellasExistentes.isEmpty()) {
			            JOptionPane.showMessageDialog(vista, 
		            		"El empleado no tiene huellas registradas.", 
		            		"Aviso", JOptionPane.WARNING_MESSAGE);
			            return;
			        }	
			        // Llamamos al método de eliminación
			        int resultado = eliminarHuella(empleado, huellasExistentes);			        
			        if (resultado == 1) {
			            JOptionPane.showMessageDialog(vista, 
		            		"Huella eliminada y sincronizada correctamente.", 
		            		"Éxito", JOptionPane.INFORMATION_MESSAGE);
			        } else if (resultado == -1) {
			        	JOptionPane.showMessageDialog(vista, 
			        		"Hubo un error al eliminar la Huella. Inténtelo de nuevo.", 
			        		"Error", JOptionPane.ERROR_MESSAGE);
			        }
			    } else {
			        JOptionPane.showMessageDialog(vista, 
		        		"Seleccione un empleado de la tabla.", 
		        		"Error", JOptionPane.ERROR_MESSAGE);
			    }
	        }
		});
	}	
	
	private boolean enviarHuellaAlServidor(HuellaLocal huellaLocal) {
		int maxIntentos = 3;
	    int intentoActual = 0;
	    while (intentoActual < maxIntentos) {
	    	intentoActual++;
	    	try {
	    		SincronizarHuellaRequest request = new SincronizarHuellaRequest();
				Huella huellaCentral = new Huella();
				huellaCentral.setIdEmpleado(huellaLocal.getEmpleado().getId());
				huellaCentral.setIndice(huellaLocal.getIndice());
				huellaCentral.setTemplate(huellaLocal.getTemplate());
				request.setHuella(huellaCentral);				
				BiometricosPortType servicio = clienteWS.obtenerPuerto();				
				SincronizarHuellaResponse response = servicio.sincronizarHuella(request);
				if (Boolean.TRUE.equals(response.isProcesada())) {
					return true;
				}				
	    	} catch (SOAPFaultException soapEx) {
	    		// Error en las credenciales de la API
	    		// Checar en la Base de datos local
	    		JOptionPane.showMessageDialog(vista, 
                        "Acceso Denegado por el Servidor Central.\nRevise las credenciales de conexión en la configuración.", 
                        "Error de Credenciales", JOptionPane.ERROR_MESSAGE);
	    		break;	    		
	    	} catch (WebServiceException redEx) {
	    		// Error al conectar con la red
	            if (intentoActual < maxIntentos) {
	                JOptionPane.showMessageDialog(vista, 
	                        "Error en la conexión con el servidor. Intentando de nuevo en 1 minuto", 
	                        "Error de Red", JOptionPane.ERROR_MESSAGE);
	                try { 
	                	Thread.sleep(TimeUnit.MINUTES.toMillis(1)); 
	                } catch (InterruptedException ie) { 
	                	Thread.currentThread().interrupt(); 
	                	break; 
	                }
	            } 	            
	        } catch (Exception e) {
                System.err.println("Error de lógica interna al procesar la huella: " + e.getMessage());
                break;
            }
	    }
		return false;
	}

	private HuellaLocal editarHuella(EmpleadoLocal empleado, Biometrico biometricoSeleccionado) {
	    // Buscamos la instancia de red correspondiente al dispositivo seleccionado
	    InstanciaBiometrico maestro = null;
	    for (InstanciaBiometrico dispositivo : dispositivos) {
	        if (dispositivo.obtenerIdentificador().equals(biometricoSeleccionado.getNombre())) {
	            maestro = dispositivo;
	            break;
	        }
	    }
	    // Cruzamos la información del empleado con el dispositivo
	    EmpleadoBiometrico empleadoBiometrico = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometricoSeleccionado);
	    if (empleadoBiometrico == null) {
	        JOptionPane.showMessageDialog(vista, 
	            "Error de integridad: El empleado no está vinculado a este dispositivo en la Base de Datos.", 
	            "Error", JOptionPane.ERROR_MESSAGE);
	        return null;
	    }	    
	    int indiceNuevo = GestorDialogosUI.solicitarIndice(vista);        
	    if (indiceNuevo != -1) {
	        // Preparamos los datos para el Rollback
	        HuellaLocal huellaExistente = huellaDao.consultarHuella(empleado, indiceNuevo);
	        byte[] templateViejo = (huellaExistente != null) ? huellaExistente.getTemplate() : null;
	        int modoOriginal = empleadoBiometrico.getModoVerificacion();	        
	        List<InstanciaBiometrico> dispositivosAfectados = new ArrayList<>();
	        try {          
	            maestro.deleteFingerprint(empleadoBiometrico.getIdGenerado(), indiceNuevo);
	            dispositivosAfectados.add(maestro);
	            boolean enrolamientoExitoso = GestorDialogosUI.procesarEnrolamiento(vista, empleadoBiometrico.getIdGenerado(), indiceNuevo, maestro);                
	            
	            if (enrolamientoExitoso) {
	                try {
	                    // Traemos los datos de la nueva huella desde el dispositivo
	                    byte[] templateNuevo = maestro.getFingerprintTemplate(empleadoBiometrico.getSerial(), indiceNuevo);
	                    HuellaLocal huellaResultante = null;
	                    // Verificamos, si ya existe la huella la editamos, si no existe la insertamos
	                    if (huellaExistente != null) {
	                        huellaExistente.setTemplate(templateNuevo);
	                        huellaDao.modificarHuella(huellaExistente);
	                        huellaResultante = huellaExistente;
	                    } else {
	                        HuellaLocal nuevaHuella = new HuellaLocal(empleado, indiceNuevo, templateNuevo);
	                        huellaDao.insertarHuella(nuevaHuella);
	                        huellaResultante = nuevaHuella;
	                    }
	                    // Calculamos el nuevo modo de verificación
	                    ModoVerificacion modoMaestro = (biometricoSeleccionado.getTieneRfid() && empleado.getRfid() > 0) ? 
	                            ModoVerificacion.HUELLA_CREDENCIAL : ModoVerificacion.HUELLA_PASSWORD;	                    
	                    maestro.setUserVerificationMode(empleadoBiometrico.getSerial(), modoMaestro.getCodigo());
	                    empleadoBiometricoDao.actualizarModoVerificacion(empleado, biometricoSeleccionado, modoMaestro.getCodigo());
	                    // Realizamos los cambios en todos los biométricos
	                    List<Biometrico> biometricos = biometricoDao.consultarBiometricos();
	                    for (InstanciaBiometrico dispositivo : dispositivos) {
	                        if (dispositivo == maestro) continue;
	                        Biometrico biometricoActual = FuncionesRepetidas.buscarBiometricoAsociado(dispositivo, biometricos);     
	                        if (biometricoActual != null) {
	                            // Cruzamos el empleado con su registro en el biométrico
	                            EmpleadoBiometrico empBio = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometricoActual);
	                            if (empBio != null) {
	                                boolean sincronizado = false;
	                                while (!sincronizado) {
	                                    try {
	                                        dispositivo.setFingerprintTemplate(empBio.getSerial(), indiceNuevo, templateNuevo);	                                        
	                                        ModoVerificacion modoDestino = (biometricoActual.getTieneRfid() && empleado.getRfid() > 0) ? 
	                                                ModoVerificacion.HUELLA_CREDENCIAL : ModoVerificacion.HUELLA_PASSWORD;	                                                
	                                        dispositivo.setUserVerificationMode(empBio.getSerial(), modoDestino.getCodigo());
	                                        empleadoBiometricoDao.actualizarModoVerificacion(empleado, biometricoActual, modoDestino.getCodigo());	                                        
	                                        sincronizado = true;
	                                        dispositivosAfectados.add(dispositivo);	                                        
	                                    } catch (Exception ex) {
	                                        int respuesta = JOptionPane.showConfirmDialog(vista, 
	                                            "Error de red al actualizar la huella en el dispositivo: " + biometricoActual.getNombre() + ".\n" +
	                                            "¿Desea reintentar la conexión?", 
	                                            "Fallo de Sincronización", 
	                                            JOptionPane.YES_NO_OPTION, 
	                                            JOptionPane.WARNING_MESSAGE);	                                        
	                                        if (respuesta != JOptionPane.YES_OPTION) {
	                                            revertirEdicionHuella(empleado, indiceNuevo, templateViejo, dispositivosAfectados, modoOriginal);
	                                            JOptionPane.showMessageDialog(vista, 
	                                                "Operación abortada. Se restauró la huella anterior en la base de datos y en los equipos.", 
	                                                "Rollback Completado", JOptionPane.INFORMATION_MESSAGE);
	                                            return null; 
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                    return huellaResultante;
	                } catch (Exception e) {
	                    System.err.println("Error crítico guardando en BD/Maestro: " + e.getMessage());
	                    revertirEdicionHuella(empleado, indiceNuevo, templateViejo, dispositivosAfectados, modoOriginal);
	                    JOptionPane.showMessageDialog(vista, 
	                        "Hubo un error de base de datos. No se guardaron los cambios.", 
	                        "Error", JOptionPane.ERROR_MESSAGE);
	                    return null;
	                }
	            } else {
	                revertirEdicionHuella(empleado, indiceNuevo, templateViejo, dispositivosAfectados, modoOriginal);
	                JOptionPane.showMessageDialog(vista, 
	                    "El enrolamiento falló o se agotó el tiempo. Se restauró su estado original.", 
	                    "Aviso", JOptionPane.WARNING_MESSAGE);
	                return null;
	            }
	        } catch (Exception e) {
	            System.err.println("Error al intentar comunicarse con el sensor: " + e.getMessage());
	            revertirEdicionHuella(empleado, indiceNuevo, templateViejo, dispositivosAfectados, modoOriginal);
	            JOptionPane.showMessageDialog(vista, 
	                "Hubo un error al preparar el dispositivo. No se guardaron los cambios.", 
	                "Error", JOptionPane.ERROR_MESSAGE);
	            return null;
	        }
	    }        
	    return null;
	}
	
	private int eliminarHuella(EmpleadoLocal empleado, List<HuellaLocal> huellasExistentes) {
	    HuellaLocal huellaEliminar = (HuellaLocal) JOptionPane.showInputDialog(
	            vista,
	            "Seleccione la huella que desea eliminar:",
	            "Eliminar Huella",
	            JOptionPane.QUESTION_MESSAGE,
	            null,
	            huellasExistentes.toArray(),
	            huellasExistentes.get(0)
	    );
	    if (huellaEliminar == null) return 0;
	    List<InstanciaBiometrico> dispositivosAfectados = new ArrayList<>();	    
	    try {
	        // Guardamos el template para el rollback
	        byte[] templateRespaldo = huellaEliminar.getTemplate();
	        int indiceBorrado = huellaEliminar.getIndice();	        
	        if (!huellaDao.eliminarHuellaEmpleado(huellaEliminar)) {
	            throw new Exception("No se pudo eliminar la huella de la Base de Datos.");
	        }
	        // Verificamos si el empleado queda con huellas registradas
	        List<HuellaLocal> huellasRestantes = huellaDao.consultarHuellasEmpleado(empleado);
	        boolean quedanHuellas = (huellasRestantes != null && !huellasRestantes.isEmpty());
	        // Sincronizamos a todos los dispositivos
	        List<Biometrico> biometricos = biometricoDao.consultarBiometricos();	        
	        for (InstanciaBiometrico dispositivo : dispositivos) {
	            Biometrico biometricoActual = FuncionesRepetidas.buscarBiometricoAsociado(dispositivo, biometricos);
	            if (biometricoActual == null) continue;
	            EmpleadoBiometrico empleadoBiometrico = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometricoActual);
	            if (empleadoBiometrico == null) continue;
	            // Asignamos la autenticación dependiendo del dispositivos y las huellas restantes
	            ModoVerificacion nuevoModo;
	            if (biometricoActual.getTieneRfid() && empleado.getRfid() > 0) {
	                nuevoModo = quedanHuellas ? ModoVerificacion.HUELLA_CREDENCIAL : ModoVerificacion.CREDENCIAL_PASSWORD;
	            } else {
	                nuevoModo = quedanHuellas ? ModoVerificacion.HUELLA_PASSWORD : ModoVerificacion.SOLO_PASSWORD;
	            }
	            boolean procesado = false;
	            while (!procesado) {
	            	// Ciclo para repetir proceso en caso de errores
	                try {
	                    dispositivo.deleteFingerprint(empleadoBiometrico.getIdGenerado(), indiceBorrado);
	                    dispositivo.setUserVerificationMode(empleadoBiometrico.getSerial(), nuevoModo.getCodigo());	                    
	                    empleadoBiometricoDao.actualizarModoVerificacion(empleado, biometricoActual, nuevoModo.getCodigo());	                    
	                    procesado = true;
	                    dispositivosAfectados.add(dispositivo);	                    
	                } catch (Exception ex) {
	                    int resp = JOptionPane.showConfirmDialog(vista, 
	                        "Error al borrar huella en: " + biometricoActual.getNombre() + "\n¿Reintentar?", 
	                        "Fallo de Red", JOptionPane.YES_NO_OPTION);	                    
	                    if (resp != JOptionPane.YES_OPTION) {
	                        revertirEliminacionHuella(empleado, huellaEliminar, templateRespaldo, dispositivosAfectados);
	                        return -1;
	                    }
	                }
	            }
	        }
	        return 1;
	    } catch (Exception e) {
	        System.err.println("Error crítico en eliminación: " + e.getMessage());
	        return -1;
	    }
	}
	
	private void revertirEdicionHuella(EmpleadoLocal empleado, int indice, byte[] templateViejo, List<InstanciaBiometrico> dispositivosAfectados, int modo) {
	    System.out.println("Iniciando ROLLBACK de edición de huella para: " + empleado.getNombre());
	    if (templateViejo != null) {
	        // Era una actualización, regresamos el template antiguo
	        HuellaLocal huella = huellaDao.consultarHuella(empleado, indice);
	        if (huella != null) {
	            huella.setTemplate(templateViejo);
	            huellaDao.modificarHuella(huella);
	        }
	    } else {
	        // Era una huella nueva, la borramos de la BD
	        HuellaLocal huella = huellaDao.consultarHuella(empleado, indice);
	        if (huella != null) {
	            huellaDao.eliminarHuellaEmpleado(huella);
	        }
	    }
	    // Ya que actualizamos las huellas en la BD las actualizamos en el dispositivo 
	    List<HuellaLocal> huellasLimpias = huellaDao.consultarHuellasEmpleado(empleado);	    
	    for (InstanciaBiometrico dispositivo : dispositivosAfectados) {
	        Biometrico biometricoAsociado = FuncionesRepetidas.buscarBiometricoAsociado(dispositivo, biometricoDao.consultarBiometricos());
	        if (biometricoAsociado != null) {
	            EmpleadoBiometrico empleadoBiometrico = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometricoAsociado);
	            if (empleadoBiometrico != null) {
	                try {
	                    User usuarioRestaurado = new User();
	                    usuarioRestaurado.id = empleadoBiometrico.getIdGenerado();
	                    usuarioRestaurado.serial = empleadoBiometrico.getSerial();
	                    usuarioRestaurado.name = empleado.getNombre();
	                    usuarioRestaurado.password = empleado.getPassword() != null ? empleado.getPassword() : "";
	                    usuarioRestaurado.cardNumber = empleado.getRfid();
	                    dispositivo.deleteUser(empleadoBiometrico.getSerial());
	                    ModoVerificacion modoOriginal = obtenerModoPorCodigo(modo);
	                    EmpleadoBiometricoControlador.sincronizarUsuarioADispositivo(dispositivo, usuarioRestaurado, huellasLimpias, modoOriginal);	                    
	                } catch (Exception e) {
	                    System.err.println("Error crítico al restaurar el dispositivo durante el rollback: " + dispositivo.obtenerIdentificador());
	                }
	            }
	        }
	    }
	    System.out.println("Rollback de huellas finalizado con éxito.");
	}
	
	private void revertirEliminacionHuella(EmpleadoLocal empleado, HuellaLocal huellaOriginal, byte[] template, List<InstanciaBiometrico> afectados) {
	    System.out.println("Iniciando ROLLBACK de eliminación de huella para: " + empleado.getNombre());	    
	    huellaDao.insertarHuella(new HuellaLocal(empleado, huellaOriginal.getIndice(), template));	    
	    List<Biometrico> biometricosBD = biometricoDao.consultarBiometricos();
	    // Restauramos la huella borrada en cada dispositivo
	    for (InstanciaBiometrico dispositivo : afectados) {
	        try {
	            Biometrico biometricoAsociado = FuncionesRepetidas.buscarBiometricoAsociado(dispositivo, biometricosBD);     
	            if (biometricoAsociado != null) {
	                EmpleadoBiometrico empleadoBiometrico = empleadoBiometricoDao.consultarEmpleadoBiometrico(empleado, biometricoAsociado);
	                if (empleadoBiometrico != null) {
	                	dispositivo.setFingerprintTemplate(empleadoBiometrico.getSerial(), huellaOriginal.getIndice(), template);	                    
	                    // Configuramos el modo que se tenía anterior a borrar la huella
	                    ModoVerificacion modoRestaurado;
	                    if (biometricoAsociado.getTieneRfid() && empleado.getRfid() > 0) {
	                        modoRestaurado = ModoVerificacion.HUELLA_CREDENCIAL;
	                    } else {
	                        modoRestaurado = ModoVerificacion.HUELLA_PASSWORD;
	                    }
	                    dispositivo.setUserVerificationMode(empleadoBiometrico.getSerial(), modoRestaurado.getCodigo());
	                    empleadoBiometricoDao.actualizarModoVerificacion(empleado, biometricoAsociado, modoRestaurado.getCodigo());
	                }
	            }
	        } catch (Exception e) {
	            System.err.println("No se pudo restaurar el dispositivo: " + dispositivo.obtenerIdentificador());
	        }
	    }
	    System.out.println("Rollback de eliminación finalizado con éxito.");
	}
	
	private ModoVerificacion obtenerModoPorCodigo(int codigo) {
	    for (ModoVerificacion modo : ModoVerificacion.values()) {
	        if (modo.getCodigo() == codigo) {
	            return modo;
	        }
	    }
	    return ModoVerificacion.SOLO_PASSWORD; // Valor seguro por defecto en caso de datos corruptos
	}
}
