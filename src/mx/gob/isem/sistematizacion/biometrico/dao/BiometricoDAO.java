package mx.gob.isem.sistematizacion.biometrico.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mx.gob.isem.sistematizacion.biometrico.modelos.Biometrico;

public class BiometricoDAO {
	public List<Biometrico> consultarBiometricos() {
		List<Biometrico> biometricos = new ArrayList<>();
		String sql = """
			SELECT *
			  FROM biometricos.biometricos
			""";
		try (Connection con = ConexionBD.obtenerConexion();
		     PreparedStatement ps = con.prepareStatement(sql);
		     ResultSet rs = ps.executeQuery()) {	
			while (rs.next()) {
				Biometrico biometrico = new Biometrico(
					rs.getInt("Id"),
					rs.getString("Nombre"),
					rs.getString("Ip"),
					rs.getInt("Tipo"),
					rs.getInt("Puerto"),
					rs.getInt("CommKey"),
					rs.getInt("Capacidad"),
					rs.getBoolean("TieneRfid")
				);
				biometricos.add(biometrico);
			}
		} catch (Exception e) {
			System.err.println("Error al consultar biometricos en la BD: "+e.getMessage());
		}
		return biometricos;
	}
}
