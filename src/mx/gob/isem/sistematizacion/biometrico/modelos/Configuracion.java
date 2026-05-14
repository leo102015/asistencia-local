package mx.gob.isem.sistematizacion.biometrico.modelos;

public class Configuracion {
	private String centro;
	private String nombre;
	
	public Configuracion() {}
	
	public Configuracion(String centro, String nombre) {
		this.centro = centro;
		this.nombre = nombre;
	}

	public String getCentro() {
		return centro;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return "Configuracion [Centro=" + centro + ", nombre=" + nombre + "]";
	}	
		
}
