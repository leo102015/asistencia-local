package mx.gob.isem.sistematizacion.biometrico.ws;

import java.net.URL;
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
    public BiometricosPortType getPuerto() {
        return puerto;
    }
}
