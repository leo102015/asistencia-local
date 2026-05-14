package mx.gob.isem.sistematizacion.biometrico.modelos;

import java.util.Date;

public class AsistenciaLocal {
	private EmpleadoLocal empleado;
	private Date tiempo;
	private boolean sincronizado;
	
	public AsistenciaLocal() {}
	
	public AsistenciaLocal(EmpleadoLocal empleado, Date tiempo) {
		this.empleado = empleado;
		this.tiempo = tiempo;
	}

	public EmpleadoLocal getEmpleado() {
		return empleado;
	}

	public void setEmpleado(EmpleadoLocal empleado) {
		this.empleado = empleado;
	}

	public Date getTiempo() {
		return tiempo;
	}

	public void setTiempo(Date tiempo) {
		this.tiempo = tiempo;
	}
	
	public boolean getSincronizado() {
		return sincronizado;
	}
	
	public void setSincronizado(boolean sincronizado) {
		this.sincronizado = sincronizado;
	}

	@Override
	public String toString() {
		return "Asistencia [idEmpleado=" + empleado.getId() + ", tiempo=" + tiempo + "]";
	}		
		
}
