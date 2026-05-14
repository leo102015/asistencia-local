package mx.gob.isem.sistematizacion.biometrico.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mx.gob.isem.sistematizacion.biometrico.modelos.EmpleadoLocal;
import mx.gob.isem.sistematizacion.biometrico.modelos.HuellaLocal;

public class HuellaDAO {		
	public boolean insertarHuella(HuellaLocal huella) {
		boolean insertado = false;
		String sql = """
			INSERT INTO biometricos.huellas (
				IdEmpleado,
				Indice,
				Template
			) VALUES (?, ?, ?)
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, huella.getEmpleado().getId());
			ps.setInt(2, huella.getIndice());
			ps.setBytes(3, huella.getTemplate());
			int filas = ps.executeUpdate();
			insertado = filas > 0;
		} catch (Exception e) {
	        System.err.println("Error al insertar huella en la BD: " + e.getMessage());
	    }	
		return insertado;
	}
	
	public HuellaLocal consultarHuella(EmpleadoLocal empleado, int indice) {
		HuellaLocal huella = null;
		String sql = """
			SELECT Template
			  FROM biometricos.huellas
			 WHERE IdEmpleado = ?
			   AND Indice = ?
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empleado.getId());
			ps.setInt(2, indice);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					huella = new HuellaLocal(
						empleado,
						indice,
						rs.getBytes("Template")
					);
				}
			}
		} catch (Exception e) {
	        System.err.println("Error al consultar huella en la BD: " + e.getMessage());
	    }			
		return huella;
	}
	
	public List<HuellaLocal> consultarHuellasEmpleado(EmpleadoLocal empleado) {
		List<HuellaLocal> huellas = new ArrayList<>();
		String sql = """
			SELECT Indice, Template
			  FROM biometricos.huellas
			 WHERE IdEmpleado = ?
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empleado.getId());
			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					HuellaLocal huella = new HuellaLocal (
						empleado,
						rs.getInt("Indice"),
						rs.getBytes("Template")
					);
					huellas.add(huella);
				}
			}
		} catch (Exception e) {
	        System.err.println("Error al consultar huellas en la BD: " + e.getMessage());
	    }			
		return huellas;
	}
	
	public boolean modificarHuella(HuellaLocal huella) {
		boolean modificado = false;
		String sql = """
			UPDATE biometricos.huellas
			   SET Template = ?
			 WHERE IdEmpleado = ?
			   AND Indice = ? 
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setBytes(1, huella.getTemplate());
			ps.setString(2, huella.getEmpleado().getId());
			ps.setInt(3, huella.getIndice());
			int filas = ps.executeUpdate();
			modificado = filas > 0;
		} catch (Exception e) {
	        System.err.println("Error al modificar huella en la BD: " + e.getMessage());
	    }			
		return modificado;
	}
	
	public boolean eliminarHuellaEmpleado(HuellaLocal huella) {
		boolean eliminado = false;
		String sql = """
			DELETE
			  FROM biometricos.huellas
			 WHERE IdEmpleado = ?
			   AND Indice = ?
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, huella.getEmpleado().getId());
			ps.setInt(2, huella.getIndice());
			int filas = ps.executeUpdate();
			eliminado = filas > 0;
		} catch (Exception e) {
	        System.err.println("Error al eliminar huella en la BD: " + e.getMessage());
	    }	
		return eliminado;
	}
	
	public boolean eliminarHuellasEmpleado(EmpleadoLocal empleado) {
		boolean eliminado = false;
		String sql = """
			DELETE
			  FROM biometricos.huellas
			 WHERE IdEmpleado = ?
		""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empleado.getId());
			int filas = ps.executeUpdate();
			eliminado = filas > 0;
		} catch (Exception e) {
	        System.err.println("Error al eliminar huellas en la BD: " + e.getMessage());
	    }	
		
		return eliminado;
	}
}
