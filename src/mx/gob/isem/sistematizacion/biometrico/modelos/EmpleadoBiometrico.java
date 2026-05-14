package mx.gob.isem.sistematizacion.biometrico.modelos;

public class EmpleadoBiometrico {    
    private EmpleadoLocal empleado;
    private Biometrico biometrico;
    private String idGenerado;
    private int serial;
    private int modoVerificacion;
    
    public EmpleadoBiometrico() {}

    public EmpleadoBiometrico(EmpleadoLocal empleado, Biometrico biometrico, String idGenerado, int serial, int modoVerificacion) {
        this.empleado = empleado;
        this.biometrico = biometrico;
        this.idGenerado = idGenerado;
        this.serial = serial;
        this.modoVerificacion = modoVerificacion;
    }

    public EmpleadoLocal getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoLocal empleado) {
        this.empleado = empleado;
    }

    public Biometrico getBiometrico() {
        return biometrico;
    }

    public void setBiometrico(Biometrico biometrico) {
        this.biometrico = biometrico;
    }

    public String getIdGenerado() {
        return idGenerado;
    }

    public void setIdGenerado(String idGenerado) {
        this.idGenerado = idGenerado;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }
    
    public int getModoVerificacion() {
    	return modoVerificacion;
    }
    
    public void setModoVerificacion(int modoVerificacion) {
    	this.modoVerificacion = modoVerificacion;
    }

	@Override
	public String toString() {
		return "EmpleadoBiometrico [idEmpleado=" + empleado.getId() + ", idBiometrico=" + biometrico.getId() + ", idGenerado="
				+ idGenerado + ", serial=" + serial + "]";
	}    
    
}