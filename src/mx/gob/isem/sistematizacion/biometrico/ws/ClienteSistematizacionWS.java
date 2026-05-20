package mx.gob.isem.sistematizacion.biometrico.ws;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import mx.gob.isem.sistematizacion.biometrico.dao.ConfiguracionDAO;
import mx.gob.isem.sistematizacion.biometrico.modelos.Configuracion;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.BiometricosPortType;
import mx.gob.isem.sistematizacion.biometrico.ws.cliente.BiometricosService;

public class ClienteSistematizacionWS {

	private BiometricosPortType puerto;

    public ClienteSistematizacionWS() {
        try {
            // Apuntamos al servidor central
            URL urlServidor = new URL("http://localhost:8080/ws/biometricos?wsdl");
            BiometricosService servicio = new BiometricosService(urlServidor);
            this.puerto = servicio.getBiometricosPort();                        
        } catch (Exception e) {
            System.err.println("Error al conectar con el Servidor Central: " + e.getMessage());
        }
    }

    // Exponemos el puerto para que lo usen los controladores
    public BiometricosPortType obtenerPuerto() {
        try {
            // Consultamos la base de datos
            ConfiguracionDAO configuracionDao = new ConfiguracionDAO();
            Configuracion configuracion = configuracionDao.consultarConfiguracion();
            // Inyectamos las credenciales actualizadas al puerto
            Map<String, Object> reqContext = ((BindingProvider) puerto).getRequestContext();
            Map<String, List<String>> headers = new HashMap<>();
            
            if (configuracion != null && configuracion.getUsuario() != null) {
                headers.put("Usuario", Collections.singletonList(configuracion.getUsuario()));
                headers.put("Password", Collections.singletonList(configuracion.getPassword()));
            }       
            reqContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        } catch (Exception e) {
            System.err.println("Error al refrescar credenciales de red: " + e.getMessage());
        }        
        return puerto;
    }
}
