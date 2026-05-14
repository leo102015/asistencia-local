package mx.gob.isem.sistematizacion.biometrico.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mx.gob.isem.sistematizacion.biometrico.modelos.EmpleadoLocal;

public class EmpleadoDAO {
	public boolean insertarEmpleado(EmpleadoLocal empleado) {
		boolean insertado = false;
		String sql = """
			INSERT INTO biometricos.empleados (
				Id,
				Nombre,
				PrimerApellido,
				SegundoApellido,
				Rfc,
				Password,
				RFID
			) VALUES (?, ?, ?, ?, ?, ?, ?)
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, empleado.getId());
			ps.setString(2, empleado.getNombre());
			ps.setString(3, empleado.getPrimerApellido());
			ps.setString(4, empleado.getSegundoApellido());
			ps.setString(5, empleado.getRfc());
			if (empleado.getPassword() == null || empleado.getPassword().trim().isEmpty()) {
	            ps.setNull(6, java.sql.Types.VARCHAR);
	        } else {
	            ps.setString(6, empleado.getPassword());
	        }
			if (empleado.getRfid() <= 0) {
	            ps.setNull(7, java.sql.Types.INTEGER);
	        } else {
	            ps.setInt(7, empleado.getRfid());
	        }
			int filas = ps.executeUpdate();
			insertado = filas > 0;
		} catch (Exception e) {
			System.err.println("Error al insertar empleado en la BD: "+e.getMessage());
		}				
		return insertado;
	}
	
	public EmpleadoLocal consultarEmpleado(String idEmpleado) {
		EmpleadoLocal empleado = null;
		String sql = """
			SELECT *
		  	  FROM biometricos.empleados 
		 	 WHERE Id = ?	
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, idEmpleado);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					empleado = new EmpleadoLocal(
						rs.getString("Id"),
						rs.getString("Nombre"),
						rs.getString("PrimerApellido"),
						rs.getString("SegundoApellido"),
						rs.getString("RFC"),
						rs.getString("Password"),
						rs.getInt("RFID"),
						rs.getBoolean("Configurado")
					);
				}
			}			
		} catch (Exception e) {
			System.err.println("Error al consultar empleado en la BD: "+e.getMessage());
		}
		return empleado;
	}
	
	public List<EmpleadoLocal> consultarEmpleados() {
		List<EmpleadoLocal> empleados = new ArrayList<>();
		String sql = """ 
			SELECT * 
		  	  FROM biometricos.empleados
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				EmpleadoLocal empleado = new EmpleadoLocal(
					rs.getString("Id"),
					rs.getString("Nombre"),
					rs.getString("PrimerApellido"),
					rs.getString("SegundoApellido"),
					rs.getString("RFC"),
					rs.getString("Password"),
					rs.getInt("RFID"),
					rs.getBoolean("Configurado")
					);
				empleados.add(empleado);
			}
		} catch (Exception e) {
			System.err.println("Error al consultar empleados locales en la BD: "+e.getMessage());
		}		
		return empleados;
	}	
	
	public boolean modificarEmpleado(EmpleadoLocal empleado) {
		boolean modificado = false;
		String sql = """
			UPDATE biometricos.empleados
			   SET Nombre = ?,
				   PrimerApellido = ?,
				   SegundoApellido = ?,
				   RFC = ?,
				   Password = ?,
				   RFID = ?
			 WHERE Id = ?
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empleado.getNombre());
			ps.setString(2, empleado.getPrimerApellido());
			ps.setString(3, empleado.getSegundoApellido());
			ps.setString(4, empleado.getRfc());
			ps.setString(5, empleado.getPassword());
			ps.setInt(6, empleado.getRfid());
			ps.setString(7, empleado.getId());
			int filas = ps.executeUpdate();
			modificado = filas > 0;
		} catch (Exception e) {
			System.err.println("Error al actualizar empleado en la BD: "+e.getMessage());
		}
		return modificado;
	}
	
	public boolean configurarEmpleado(EmpleadoLocal empleado) {
		boolean modificado = false;
		String sql = """
			UPDATE biometricos.empleados
			   SET Configurado = TRUE
			 WHERE Id = ?		
		""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empleado.getId());
			int filas = ps.executeUpdate();
			modificado = filas > 0;
		} catch (Exception e) {
			System.err.println("Error al actualizar empleado en la BD: "+e.getMessage());
		}
		return modificado;
	}
	
	public boolean eliminarEmpleado(String idEmpleado) {
		boolean eliminado = false;
		String sql = """
			DELETE
			  FROM biometricos.empleados
			 WHERE Id = ?
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, idEmpleado);
			int filas = ps.executeUpdate();
			eliminado = filas > 0;
		} catch (Exception e) {
			System.err.println("Error al eliminar empleado en la BD: "+e.getMessage());
		}
		return eliminado;
	}
		
}
