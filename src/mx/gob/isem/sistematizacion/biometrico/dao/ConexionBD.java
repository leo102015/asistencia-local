package mx.gob.isem.sistematizacion.biometrico.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
	private static final String URL = "jdbc:mysql:/***/";
    private static final String USER = "***";
    private static final String PASSWORD = "***";
    
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
