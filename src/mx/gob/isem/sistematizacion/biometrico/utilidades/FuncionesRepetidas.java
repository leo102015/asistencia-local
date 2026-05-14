package mx.gob.isem.sistematizacion.biometrico.utilidades;

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

}
