package mx.gob.isem.sistematizacion.biometrico.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import mx.gob.isem.sistematizacion.biometrico.modelos.Configuracion;

public class ConfiguracionDAO {
	public Configuracion consultarConfiguracion() {
		String sql = """
			SELECT * 
			  FROM biometricos.configuracion
			""";
		Configuracion configuracion = new Configuracion();
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while(rs.next()) {
				configuracion.setCentro(rs.getString("Centro"));
				configuracion.setNombre(rs.getString("Nombre"));
			}
		} catch (Exception e) {
			System.err.println("Error al consultar configuracion en la BD: "+e.getMessage());
		}
		return configuracion;
	}
}
