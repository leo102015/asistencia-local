package mx.gob.isem.sistematizacion.biometrico.modelos;

public class EmpleadoLocal {
	private String id;
	private String nombre;
	private String primerApellido;
	private String segundoApellido;
	private String rfc;
	private String password;
	private int rfid;
	private boolean configurado;
	
	public EmpleadoLocal() {}
	
	public EmpleadoLocal(String id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}
	
	public EmpleadoLocal(String id, String nombre, String primerApellido, String segundoApellido, String rfc, String password, int rfid) {
		this.id = id;
		this.nombre = nombre;
		this.primerApellido = primerApellido;
		this.segundoApellido = segundoApellido;
		this.rfc = rfc;
		this.password = password;
		this.rfid = rfid;
	}
	
	public EmpleadoLocal(String id, String nombre, String primerApellido, String segundoApellido, String rfc, String password, int rfid, boolean configurado) {
		this.id = id;
		this.nombre = nombre;
		this.primerApellido = primerApellido;
		this.segundoApellido = segundoApellido;
		this.rfc = rfc;
		this.password = password;
		this.rfid = rfid;
		this.configurado = configurado;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}	
	
	public String getPrimerApellido() {
		return primerApellido;
	}

	public void setPrimerApellido(String primerApellido) {
		this.primerApellido = primerApellido;
	}	
	
	public String getSegundoApellido() {
		return segundoApellido;
	}
	
	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = segundoApellido;
	}
	
	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRfid() {
		return rfid;
	}

	public void setRfid(int rfid) {
		this.rfid = rfid;
	}
	
	public boolean getConfigurado() {
		return configurado;
	}
	
	public void setConfigurado(boolean configurado) {
		this.configurado = configurado;
	}

	@Override
	public String toString() {
		return "Empleado [idEmpleado=" + id + ", nombre=" + nombre + "]";
	}		
	
}
