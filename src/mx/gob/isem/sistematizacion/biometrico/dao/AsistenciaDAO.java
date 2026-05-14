package mx.gob.isem.sistematizacion.biometrico.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Timestamp;

import mx.gob.isem.sistematizacion.biometrico.modelos.AsistenciaLocal;
import mx.gob.isem.sistematizacion.biometrico.modelos.EmpleadoLocal;

public class AsistenciaDAO {	
	public boolean insertarAsistencias(List<AsistenciaLocal> asistencias) {
	    boolean insertado = false;
	    String sql = "INSERT IGNORE INTO biometricos.asistencias (IdEmpleado, Tiempo) VALUES (?, ?)";	    
	    Connection con = null;
	    try {
	        con = ConexionBD.obtenerConexion();
	        con.setAutoCommit(false);
	        try (PreparedStatement ps = con.prepareStatement(sql)) {
	            for (AsistenciaLocal asistencia : asistencias) {
	                ps.setString(1, asistencia.getEmpleado().getId());
	                ps.setTimestamp(2, new Timestamp(asistencia.getTiempo().getTime()));
	                ps.addBatch();
	            }
	            ps.executeBatch();
	            con.commit();
	            insertado = true;
	            System.out.println("Lote de " + asistencias.size() + " asistencias guardado con éxito.");
	        }
	    } catch (Exception e) {
	        if (con != null) {
	            try {
	                System.err.println("Error detectado. Iniciando Rollback de asistencias...");
	                con.rollback(); 
	            } catch (SQLException ex) {
	                System.err.println("Error fatal: No se pudo realizar el rollback " + ex.getMessage());
	            }
	        }
	        System.err.println("Error al guardar asistencias: " + e.getMessage());
	    } finally {
	        if (con != null) {
	            try { con.close(); } catch (SQLException e) {  }
	        }
	    }
	    return insertado;
	}
	
	public List<AsistenciaLocal> consultarAsistencias() {
		List<AsistenciaLocal> asistencias = new ArrayList<>();
		String sql = """
            SELECT A.Tiempo, E.Id, E.Nombre, E.PrimerApellido, E.SegundoApellido, E.RFC, E.Password, E.RFID
              FROM biometricos.asistencias A
             INNER JOIN biometricos.empleados E ON A.IdEmpleado = E.Id
             WHERE A.Sincronizado = 0
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
	                    rs.getInt("RFID")
	                );	                
	                Timestamp ts = rs.getTimestamp("Tiempo");
	                Date tiempoExacto = new Date(ts.getTime());
	                AsistenciaLocal asistencia = new AsistenciaLocal(empleado, tiempoExacto);
	                asistencias.add(asistencia);
			}
		} catch (Exception e) {
			System.err.println("Error al consultar asistencias en la BD: "+e.getMessage());
		}
		return asistencias;
	}
	
	public boolean marcarSincronizadasLote(List<AsistenciaLocal> asistencias) {
	    boolean actualizado = false;
	    String sql = """	    		
    		UPDATE biometricos.asistencias
	    	   SET Sincronizado = 1 
    		 WHERE IdEmpleado = ? 
    		   AND Tiempo = ?    		
    		""";	    
	    Connection con = null;
	    try {
	        con = ConexionBD.obtenerConexion();
	        con.setAutoCommit(false);	        
	        try (PreparedStatement ps = con.prepareStatement(sql)) {
	            for (AsistenciaLocal asistencia : asistencias) {
	                ps.setString(1, asistencia.getEmpleado().getId());
	                ps.setTimestamp(2, new Timestamp(asistencia.getTiempo().getTime()));
	                ps.addBatch();
	            }	            
	            ps.executeBatch();
	            con.commit();
	            actualizado = true;
	        }
	    } catch (Exception e) {
	        if (con != null) {
	            try { con.rollback(); } catch (SQLException ex) { }
	        }
	        System.err.println("Error al actualizar asistencias por lote: " + e.getMessage());
	    } finally {
	        if (con != null) {
	            try { con.close(); } catch (SQLException e) { }
	        }
	    }
	    return actualizado;
	}
		
	public boolean borrarAsistencias() {
		boolean borrado = false;
		String sql = """
			DELETE 
			  FROM biometricos.asistencias
			 WHERE Sincronizado = 1
			""";
		try (Connection con = ConexionBD.obtenerConexion();
			 Statement ps = con.createStatement()) {			
			ps.executeUpdate(sql);
			borrado = true;
		} catch (Exception e) {
			System.err.println("Error al borrar asistencias en la BD: " + e.getMessage());
			e.printStackTrace();
		}
		return borrado;
	}
		
}
