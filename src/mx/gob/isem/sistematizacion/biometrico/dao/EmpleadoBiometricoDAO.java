package mx.gob.isem.sistematizacion.biometrico.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.gob.isem.sistematizacion.biometrico.modelos.Biometrico;
import mx.gob.isem.sistematizacion.biometrico.modelos.EmpleadoLocal;
import mx.gob.isem.sistematizacion.biometrico.modelos.EmpleadoBiometrico;

public class EmpleadoBiometricoDAO {
	public boolean insertarEmpleadoBiometrico(EmpleadoBiometrico empleadoBiometrico) {
		boolean insertado = false;
		String sql = """
			INSERT INTO biometricos.empleadosbiometricos (
				IdEmpleado,
				IdBiometrico,
				IdGenerado,
				Serial,
				ModoVerificacion
			) VALUES (?, ?, ?, ?, ?)			
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empleadoBiometrico.getEmpleado().getId());
			ps.setInt(2, empleadoBiometrico.getBiometrico().getId());
			ps.setString(3, empleadoBiometrico.getIdGenerado());
			ps.setInt(4, empleadoBiometrico.getSerial());
			ps.setInt(5, empleadoBiometrico.getModoVerificacion());
			int filas = ps.executeUpdate();
			insertado = filas > 0;
		} catch (Exception e) {
			System.err.println("Error al insertar empleadoBiometrico en la BD: "+e.getMessage());
		}
		return insertado;
	}
	
	public EmpleadoBiometrico consultarEmpleadoBiometrico(EmpleadoLocal empleado, Biometrico biometrico) {
		EmpleadoBiometrico empleadoBiometrico = null;
		String sql = """
			SELECT IdGenerado, Serial, ModoVerificacion
			  FROM biometricos.empleadosbiometricos
			 WHERE IdEmpleado = ?
			   AND IdBiometrico = ? 
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empleado.getId());
			ps.setInt(2, biometrico.getId());
			try (ResultSet rs = ps.executeQuery()){
				if (rs.next()) {
					empleadoBiometrico = new EmpleadoBiometrico(
						empleado,
						biometrico,
						rs.getString("IdGenerado"),
						rs.getInt("Serial"),
						rs.getInt("ModoVerificacion")
					);
				}
			}
		} catch (Exception e) {
			System.err.println("Error al consultar empleadoBiometrico en la BD: "+e.getMessage());
		}
		return empleadoBiometrico;
	}
	
	public EmpleadoLocal consultarEmpleadoPorId(String id, Biometrico biometrico) {
		EmpleadoLocal empleado = null;
		String sql = """
			SELECT EB.IdEmpleado,
				   E.Id AS IdEmp, E.Nombre, E.PrimerApellido, E.SegundoApellido, E.RFC, E.Password, E.RFID
			  FROM biometricos.empleadosbiometricos EB
			 INNER JOIN biometricos.empleados E ON EB.IdEmpleado = E.Id
			 WHERE EB.IdBiometrico = ? 
			   AND EB.IdGenerado = ?
		""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, biometrico.getId());
			ps.setString(2, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					empleado = new EmpleadoLocal(
							rs.getString("Id"),
		                    rs.getString("Nombre"),
							rs.getString("PrimerApellido"),
							rs.getString("SegundoApellido"),
							rs.getString("RFC"),
		                    rs.getString("Password"),
		                    rs.getInt("RFID")
					);
				}				
			}
		} catch (Exception e) {
	        System.err.println("Error al consultar empleadosBiometrico en la BD: " + e.getMessage());
	    }
		
		return empleado;
	}
	
	public EmpleadoBiometrico consultarPorId(String id, Biometrico biometrico) {
		EmpleadoBiometrico empleadoBiometrico = null;
		String sql = """
			SELECT EB.IdBiometrico, EB.IdGenerado, EB.Serial, EB.ModoVerificacion
	               E.Id AS IdEmp, E.Nombre, E.PrimerApellido, E.SegundoApellido, E.RFC, E.Password, E.RFID
	          FROM biometricos.empleadosbiometricos EB
	         INNER JOIN biometricos.empleados E ON EB.IdEmpleado = E.Id
	         WHERE EB.IdGenerado LIKE ?
	           AND EB.IdBiometrico = ?
		""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, id);
			ps.setInt(2, biometrico.getId());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					EmpleadoLocal empleado = new EmpleadoLocal(
							rs.getString("Id"),
		                    rs.getString("Nombre"),
							rs.getString("PrimerApellido"),
							rs.getString("SegundoApellido"),
							rs.getString("RFC"),
		                    rs.getString("Password"),
		                    rs.getInt("RFID")
					);
					empleadoBiometrico = new EmpleadoBiometrico(
						empleado,
						biometrico,
						rs.getString("IdGenerado"),
						rs.getInt("Serial"),
						rs.getInt("ModoVerificacion")
					);
				}
			}
		} catch (Exception e) {
	        System.err.println("Error al consultar empleadosBiometrico en la BD: " + e.getMessage());
	    }
		return empleadoBiometrico;
	}
	
	public List<EmpleadoBiometrico> consultarEmpleadosBiometricos() {
	    List<EmpleadoBiometrico> empleadosBiometricos = new ArrayList<>();	    
	    String sql = """
	        SELECT EB.IdGenerado, EB.Serial, EB.ModoVerificacion,
	               E.Id AS IdEmp, E.Nombre AS NombreEmp, E.PrimerApellido, E.SegundoApellido, E.RFC, E.Password, E.RFID,
	               B.Id AS IdBio, B.Nombre AS NombreBio, B.Ip, B.Tipo, B.Puerto, B.CommKey, B.Capacidad, B.TieneRfid
	          FROM biometricos.empleadosbiometricos EB
	         INNER JOIN biometricos.empleados E ON EB.IdEmpleado = E.Id
	         INNER JOIN biometricos.biometricos B ON EB.IdBiometrico = B.Id
	        """;
	    try (Connection con = ConexionBD.obtenerConexion();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {	         
	         while (rs.next()) {
	             EmpleadoLocal empleado = new EmpleadoLocal(
	                 rs.getString("IdEmp"),
	                 rs.getString("NombreEmp"),
	                 rs.getString("PrimerApellido"),
	                 rs.getString("SegundoApellido"),
	                 rs.getString("RFC"),
	                 rs.getString("Password"),
	                 rs.getInt("RFID")
	             );	            
	             Biometrico biometrico = new Biometrico(
	                 rs.getInt("IdBio"),
	                 rs.getString("NombreBio"),
	                 rs.getString("Ip"),
	                 rs.getInt("Tipo"),
	                 rs.getInt("Puerto"),
	                 rs.getInt("CommKey"),
	                 rs.getInt("CapacidadBio"),
	                 rs.getBoolean("TieneRfid")
	             );
	             EmpleadoBiometrico eb = new EmpleadoBiometrico(
	                 empleado,
	                 biometrico,
	                 rs.getString("IdGenerado"),
	                 rs.getInt("Serial"),
	                 rs.getInt("ModoVerificacion")
	             );
	             empleadosBiometricos.add(eb);
	         }
	    } catch (Exception e) {
	        System.err.println("Error al consultar empleadosBiometricos en la BD: " + e.getMessage());
	    }	    
	    return empleadosBiometricos;
	}
	
	public boolean eliminarEmpleadoBiometrico(EmpleadoLocal empleado, Biometrico biometrico) {
		boolean eliminado = false;
		String sql = """
			DELETE
			  FROM biometricos.empleadosbiometricos
			 WHERE IdEmpleado = ?
			   AND IdBiometrico = ?
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empleado.getId());
			ps.setInt(2, biometrico.getId());
			int filas = ps.executeUpdate();
			eliminado = filas > 0;
		} catch (Exception e) {
	        System.err.println("Error al eliminar empleadoBiometrico en la BD: " + e.getMessage());
	    }	
		return eliminado;
	}
	
	public int generarIdEmpleado(EmpleadoLocal empleado, Biometrico biometrico) {
		int idDisponible = 0;
		int capacidad = biometrico.getCapacidad();
		String sql = """
	        SELECT IdEmpleado, Serial
	          FROM biometricos.empleadosbiometricos
	         WHERE IdBiometrico = ?
	        """;
		Set<Integer> serialesOcupados = new HashSet<>();
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, biometrico.getId());
			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					String idEmpleado = rs.getString("idEmpleado");
					int serialActual = rs.getInt("Serial");
					if (idEmpleado.equals(empleado.getId())) {
						return serialActual;
					}
					serialesOcupados.add(serialActual);
				}
				for (int i = 2; i <= capacidad; i++) {
					if (!serialesOcupados.contains(i)) {
						idDisponible = i;
						break;
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error al generar ID para el biométrico: " + e.getMessage());
		}
		
		return idDisponible;
	}
	
	public boolean actualizarModoVerificacion(EmpleadoLocal empleado, Biometrico biometrico, int nuevoModo) {
		boolean actualizado = false;
		String sql = """
			UPDATE biometricos.empleadosbiometricos
			   SET ModoVerificacion = ?
			 WHERE IdEmpleado = ?
			   AND IdBiometrico = ?
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, nuevoModo);
			ps.setString(2, empleado.getId());
			ps.setInt(3, biometrico.getId());
			int filas = ps.executeUpdate();
			actualizado = filas > 0;
		} catch (Exception e) {
	        System.err.println("Error al actualizar el Modo de Verificacion en la BD: " + e.getMessage());
	    }
		
		return actualizado;
	}
	
}
