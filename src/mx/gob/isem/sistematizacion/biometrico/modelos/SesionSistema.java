package mx.gob.isem.sistematizacion.biometrico.modelos;

public class SesionSistema {
	private static SesionSistema instancia;    
    private String centro;
    private String hashPassword;

    // Constructor privado para impedir instanciación externa
    private SesionSistema() {}

    public static synchronized SesionSistema getInstancia() {
        if (instancia == null) {
            instancia = new SesionSistema();
        }
        return instancia;
    }

    // Getters y Setters
    public String getCentro() { return centro; }
    public void setCentro(String centro) { this.centro = centro; }

    public String getHashPassword() { return hashPassword; }
    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }

    // Limpieza de sesión al cerrar la app
    public void cerrarSesion() {
        this.centro = null;
        this.hashPassword = null;
    }

}
