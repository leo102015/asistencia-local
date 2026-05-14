package mx.gob.isem.sistematizacion.biometrico.controladores;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import mx.gob.isem.sistematizacion.biometrico.Attendance;
import mx.gob.isem.sistematizacion.biometrico.InstanciaBiometrico;
import mx.gob.isem.sistematizacion.biometrico.dao.AsistenciaDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.BiometricoDAO;
import mx.gob.isem.sistematizacion.biometrico.dao.EmpleadoBiometricoDAO;
import mx.gob.isem.sistematizacion.biometrico.modelos.AsistenciaLocal;
import mx.gob.isem.sistematizacion.biometrico.modelos.Biometrico;
import mx.gob.isem.sistematizacion.biometrico.modelos.EmpleadoLocal;
import mx.gob.isem.sistematizacion.biometrico.utilidades.FuncionesRepetidas;
import mx.gob.isem.sistematizacion.biometrico.vistas.VistaPrincipal;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.Asistencia;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.BiometricosPortType;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.ListaAsistencias;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.ObjectFactory;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.SincronizarAsistenciasRequest;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.SincronizarAsistenciasResponse;

public class AsistenciaControlador {
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private List<InstanciaBiometrico> dispositivos;
	private AsistenciaDAO asistenciaDao;
	private BiometricoDAO biometricoDao;
	private EmpleadoBiometricoDAO empleadoBiometricoDao;
	private BiometricosPortType servicio;
	private ObjectFactory factory;
	
	public AsistenciaControlador(VistaPrincipal vista, List<InstanciaBiometrico> dispositivos, BiometricosPortType servicio) {
		this.dispositivos = dispositivos;
		this.asistenciaDao = new AsistenciaDAO();
		this.biometricoDao = new BiometricoDAO();
		this.empleadoBiometricoDao = new EmpleadoBiometricoDAO();
		this.factory = new ObjectFactory();
		this.servicio = servicio;
		inicializarEventos();
	}
	
	private void inicializarEventos() {
		// Los parámetros es la hora en formato de 24 horas y los minutos
		programarRecoleccionDiaria(02, 30);		
	}
	
	private void programarRecoleccionDiaria(int hora, int minuto) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime proximaEjecucion = ahora.withHour(hora).withMinute(minuto).withSecond(0);
        // Programamos para el siguiente día
        if (ahora.isAfter(proximaEjecucion)) {
            proximaEjecucion = proximaEjecucion.plusDays(1);
        }
        // Ajuste para ejecutarse cada día
        long delayInicial = Duration.between(ahora, proximaEjecucion).toSeconds();
        scheduler.scheduleAtFixedRate(() -> {
            ejecutarProcesoConReintento();
        }, delayInicial, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
        System.out.println("Programador iniciado. Próxima recolección: " + proximaEjecucion);
    }
	
	private void ejecutarProcesoConReintento() {
		// Manejamos una cola para los dispositivos donde falle la extracción y reintentarlo
	    List<InstanciaBiometrico> dispositivosPendientes = new ArrayList<>(this.dispositivos);
	    int intentos = 0;	    
	    while (!dispositivosPendientes.isEmpty()) {
	        intentos++;
	        System.out.println("Intento de recolección #" + intentos + " | Dispositivos pendientes: " + dispositivosPendientes.size());	        
	        List<InstanciaBiometrico> exitosos = procesarLotePendientes(dispositivosPendientes);
	        dispositivosPendientes.removeAll(exitosos);
	        if (!dispositivosPendientes.isEmpty()) {
	            try {
	                // Dejamos una espera para volver a procesar a los dispositivos en los que no se pudo extraer
	                System.err.println("Fallo de red/BD en " + dispositivosPendientes.size() + " dispositivo(s). Reintentando en 5 minutos...");
	                Thread.sleep(TimeUnit.MINUTES.toMillis(5)); 
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	                break;
	            }
	        }
	    }
	    System.out.println("Recolección diaria finalizada con éxito en TODOS los dispositivos.");
	    // Si recolecta asistencias de los dispositivos las enviamos al servidor
	    sincronizarAsistenciasAlServidor();
	}
	
	private void sincronizarAsistenciasAlServidor() {
		DatatypeFactory df = null;
		try {
		    df = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
		    System.err.println("Error grave al inicializar DatatypeFactory: " + e.getMessage());
		}
        boolean sincronizacionExitosa = false;
        int intentosSync = 0;
        int maxIntentos = 3; 
        while (!sincronizacionExitosa && intentosSync < maxIntentos) {
            intentosSync++;
            try {
                List<AsistenciaLocal> asistencias = asistenciaDao.consultarAsistencias();
                if (asistencias == null || asistencias.isEmpty()) {
                    System.out.println("No hay asistencias nuevas para sincronizar.");
                    return;
                }
                // Preparamos la petición SOAP
                SincronizarAsistenciasRequest peticion = new SincronizarAsistenciasRequest();
                peticion.setCantidad(asistencias.size());
                ListaAsistencias asistenciasParametro = factory.createListaAsistencias();                
                for (AsistenciaLocal asistencia : asistencias) {
                	Asistencia asistenciaCentral = new Asistencia();
                	asistenciaCentral.setIdEmpleado(asistencia.getEmpleado().getId());
                	if (df != null && asistencia.getTiempo() != null) {
                        // Pasamos el Date local a un GregorianCalendar
                        GregorianCalendar calendario = new GregorianCalendar();
                        calendario.setTime(asistencia.getTiempo());
                        XMLGregorianCalendar fechaXML = df.newXMLGregorianCalendar(calendario);
                        asistenciaCentral.setTiempo(fechaXML);
                    }
					asistenciasParametro.getAsistencia().add(asistenciaCentral);
				}
                peticion.setAsistencias(asistenciasParametro);
                // Consumo del Web Service
                SincronizarAsistenciasResponse respuesta = servicio.sincronizarAsistencias(peticion);
                if (respuesta.isExito()) {                                      
                    // Actualizamos las asistencias como sincronizadas
                	boolean actualizadas = asistenciaDao.marcarSincronizadasLote(asistencias);                	
                	if (actualizadas) {
                        // Borramos las asistencias sincronizadas
                        asistenciaDao.borrarAsistencias();
                        sincronizacionExitosa = true;
                        System.out.println("Limpieza local finalizada. Sincronización 100% completada.");
                    } else {
                        System.err.println("Error al actualizar la BD local tras sincronizar.");
                        esperarParaReintento();
                    }
                } else {
                    System.err.println("El servidor central rechazó el lote. (Revisar logs en Toluca)");
                    esperarParaReintento();
                }
            } catch (Exception e) {
                System.err.println("Error de red al conectar con el Web Service: " + e.getMessage());
                esperarParaReintento();
            }
        }
        if (!sincronizacionExitosa) {
            System.err.println("Se alcanzó el límite máximo de intentos de sincronización. Quedarán pendientes para mañana.");
        }
    }

    private void esperarParaReintento() {
        try {
            System.out.println("Reintentando sincronización externa en 10 minutos...");
            Thread.sleep(TimeUnit.MINUTES.toMillis(10));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
	
	public List<InstanciaBiometrico> procesarLotePendientes(List<InstanciaBiometrico> pendientes) {
	    List<InstanciaBiometrico> dispositivosExitosos = new ArrayList<>();	    
	    List<Biometrico> biometricosBD = biometricoDao.consultarBiometricos();
	    // Recorremos los dispositivos, puede que sean los pendientes o sean todos
	    for (InstanciaBiometrico dispositivo : pendientes) {
	        Biometrico biometricoActual = FuncionesRepetidas.buscarBiometricoAsociado(dispositivo, biometricosBD);	        
	        if (biometricoActual == null) {
	            dispositivosExitosos.add(dispositivo);
	            continue; 
	        }
	        try {
	            Attendance[] attendances = dispositivo.beginAndGetAttendanceRecords();	            
	            if (attendances != null && attendances.length > 0) {
	            	// Solo agregamos a los registros que tengan un id coincidente con un empleado en la BD
	                List<AsistenciaLocal> asistenciasValidas = filtrarAsistencias(attendances, biometricoActual);
	                if (!asistenciasValidas.isEmpty()) {
	                    boolean guardadoEnBD = asistenciaDao.insertarAsistencias(asistenciasValidas);
	                    // En caso de poder guardarse en la BD eliminamos los registros del dispositivo, de lo contrario lo tratamos como excepción para volver a ejecutar el ciclo
	                    if (guardadoEnBD) {
	                    	int intentosLimpieza = 0;
	                        boolean limpiado = false;	                        
	                        while (intentosLimpieza < 3 && !limpiado) {
	                            intentosLimpieza++;
	                            if (dispositivo.endAndRemoveAttendanceRecords()) {
	                                limpiado = true;
	                                dispositivosExitosos.add(dispositivo);
	                                System.out.println("Reloj " + biometricoActual.getNombre() + " limpiado con éxito.");
	                            } else {
	                                System.err.println("Fallo intento #" + intentosLimpieza + " de limpieza en " + biometricoActual.getNombre());
	                                try { Thread.sleep(1000); } catch (InterruptedException ignore) {}
	                            }
	                        }
	                        if (!limpiado) {
	                            System.err.println("CRÍTICO: No se pudo limpiar el reloj " + biometricoActual.getNombre() + " tras 3 intentos.");
	                        }
	                    } else {
	                        throw new Exception("Error al insertar en Base de Datos Local. Abortando limpieza de dispositivo.");
	                    }
	                } else {
	                    // Eliminamos los registros en caso de haber de empleados que no están en la BD
	                    dispositivo.endAndRemoveAttendanceRecords(); 
	                    dispositivosExitosos.add(dispositivo);
	                }
	            } else {
	                // Manejamos el caso de que no haya registros guardados
	            	dispositivo.endAttendanceRecords();
	                dispositivosExitosos.add(dispositivo);
	            }	            
	        } catch (Exception e) {
	            System.err.println("Error aislado en " + biometricoActual.getNombre() + ": " + e.getMessage());
	        }
	    }	    
	    return dispositivosExitosos;
	}	

	private List<AsistenciaLocal> filtrarAsistencias(Attendance[] attendances, Biometrico biometricoActual) {
	    List<AsistenciaLocal> asistenciasValidas = new ArrayList<>();
	    
	    for (Attendance attendance : attendances) {
	        // Verificamos si el id del registro coincide con un empleado en la base de datos
	        EmpleadoLocal empleado = empleadoBiometricoDao.consultarEmpleadoPorId(attendance.id, biometricoActual); 	        
	        if (empleado != null) {
	            // Guardamos en la lista la asistencia que sí corresponde a un empleado
	            AsistenciaLocal asistencia = new AsistenciaLocal(empleado, attendance.date);
	            asistenciasValidas.add(asistencia);
	        } else {
	            // Ignoramos la checada porque no corresponde a ningún empleado de la BD
	            System.err.println("Checada ignorada: El id " + attendance.id + " en el biometrico no coincide con ningún empleado de la BD");                            
	        }
	    }	    
	    return asistenciasValidas;
	}
		
}
