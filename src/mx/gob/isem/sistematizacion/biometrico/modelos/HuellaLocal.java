package mx.gob.isem.sistematizacion.biometrico.modelos;

public class HuellaLocal {
	private EmpleadoLocal empleado;
	private int indice;
	private byte[] template;
	
	public HuellaLocal() {}

	public HuellaLocal(EmpleadoLocal empleado, int indice, byte[] template) {
		this.empleado = empleado;
		this.indice = indice;
		this.template = template;
	}

	public EmpleadoLocal getEmpleado() {
		return empleado;
	}

	public void setEmpleado(EmpleadoLocal empleado) {
		this.empleado = empleado;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public byte[] getTemplate() {
		return template;
	}

	public void setTemplate(byte[] template) {
		this.template = template;
	}

	@Override
	public String toString() {
		return obtenerNombreDedo(this.indice) + " (" + this.indice + ")";
	}		
	
	private String obtenerNombreDedo(int indice) {
		switch (indice) {
			case 0: return "Meñique izquierdo";
			case 1: return "Anular izquierdo";
			case 2: return "Medio izquierdo";
			case 3: return "Índice izquierdo";
			case 4: return "Pulgar izquierdo";
			case 5: return "Pulgar derecho";
			case 6: return "Índice derecho";
			case 7: return "Medio derecho";
			case 8: return "Anular derecho";
			case 9: return "Meñique derecho";
		}
		return "";
	}
	
}
