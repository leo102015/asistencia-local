package mx.gob.isem.sistematizacion.biometrico.modelos;

public class Biometrico {
	private int id;
	private String nombre;
	private String ip;
	private int tipo;
	private int puerto;
	private int commKey;
	private int capacidad;
	private boolean tieneRfid;
	
	public Biometrico() {}
	
	public Biometrico(int id, String nombre, String ip, int tipo, int puerto, int commKey, int capacidad, boolean tieneRfid) {
		this.id = id;
		this.nombre = nombre;
		this.ip = ip;
		this.tipo = tipo;
		this.puerto = puerto;
		this.commKey = commKey;
		this.capacidad = capacidad;
		this.tieneRfid = tieneRfid;
	}	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getTipo() { 
		return tipo; 
	}
	
	public void setTipo(int tipo) { 
		this.tipo = tipo; 
	}

	public int getPuerto() { 
		return puerto; 
	}
	
	public void setPuerto(int puerto) { 
		this.puerto = puerto; 
	}

	public int getCommKey() { 
		return commKey; 
	}
	
	public void setCommKey(int commKey) { 
		this.commKey = commKey; 
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}
	
	public boolean getTieneRfid() {
		return tieneRfid;
	}
	
	public void setTieneRfid(boolean tieneRfid) {
		this.tieneRfid = tieneRfid;
	}
	
	@Override
	public String toString() {
		return "Biometrico " + nombre + " con IP " + ip;
	}
}
