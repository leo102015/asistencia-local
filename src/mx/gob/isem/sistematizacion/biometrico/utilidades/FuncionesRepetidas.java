package mx.gob.isem.sistematizacion.biometrico.utilidades;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import mx.gob.isem.sistematizacion.biometrico.InstanciaBiometrico;
import mx.gob.isem.sistematizacion.biometrico.modelos.Biometrico;

public class FuncionesRepetidas {
	
	public enum ModoVerificacion {
	    HUELLA_CREDENCIAL(138),
	    HUELLA_PASSWORD(137),
	    CREDENCIAL_PASSWORD(139),
	    SOLO_PASSWORD(131);

	    private final int codigo;
	    ModoVerificacion(int codigo) { this.codigo = codigo; }
	    public int getCodigo() { return codigo; }
	}		
	
	public static Biometrico buscarBiometricoAsociado(InstanciaBiometrico dispositivoLocal, List<Biometrico> biometricosBD) {
	    for (Biometrico biometrico : biometricosBD) {
	        if (dispositivoLocal.obtenerIdentificador().equals(biometrico.getNombre())) {
	            return biometrico;
	        }
	    }
	    return null;
	}
	
	public static String generarSHA256(String textoPlano) {
        if (textoPlano == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(textoPlano.getBytes(StandardCharsets.UTF_8));
            
            // Convertimos el arreglo de bytes a formato Hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error crítico al generar el hash hash SHA-256", e);
        }
    }

}
