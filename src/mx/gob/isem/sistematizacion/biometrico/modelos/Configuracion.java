package mx.gob.isem.sistematizacion.biometrico.modelos;

public class Configuracion {
	private String centro;
	private String nombre;
	private String usuario;
	private String password;
	
	public Configuracion() {}
	
	public Configuracion(String centro, String nombre, String usuario, String password) {
		this.centro = centro;
		this.nombre = nombre;
		this.usuario = usuario;
		this.password = password;
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
	
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Configuracion [Centro=" + centro + ", nombre=" + nombre + "]";
	}	
		
}
