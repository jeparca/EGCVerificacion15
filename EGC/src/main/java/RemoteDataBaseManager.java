package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RemoteDataBaseManager {
	
	/**
	 * Función que almacena en la base de datos remota un par de claves de cifrado RSA
	 * asociadas a una votación
	 * @param id La ide de la votación.
	 * @param publicKey La clave pública de cifrado asociada a la votación
	 * @param privateKey La clave privada de cifrado asociada a la votación
	 * @return una variable booleana que será cierta si el guardado se realiza con éxito.
	 */
	public boolean postKeys(String id, String publicKey,String privateKey ){
		boolean success = false;
		Connection conn = null;
		Statement stmt = null;
	    String USER = "jeparcac_egc";
	    String PASS = "kqPTE8dLz3GVtks";  
	    String DB_URL = "jdbc:mysql://egc.jeparca.com:3306/jeparcac_egc";
		
		try {	
		
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		
		stmt = conn.createStatement();
		
		String sql = "INSERT INTO keysvotes (idvotation, publicKey, privateKey)" +
                "VALUES (?, ?, ?)";
		
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, new Integer(id));
        preparedStatement.setString(2, publicKey);
        preparedStatement.setString(3, privateKey);
        int r = preparedStatement.executeUpdate(); 
        
        if(r == 1){
        	success = true;
        }else if(r == 0){
        	success = false;
        }
	    
		} catch(SQLException se) {
	        se.printStackTrace();
	    } catch(Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if(stmt != null)
	                conn.close();
	        } catch(SQLException se) {
	        }
	        try {
	            if(conn != null)
	                conn.close();
	        } catch(SQLException se) {
	            se.printStackTrace();
	        }
	    }
		
		return success;
	}
	
	/**
	 * Función usada para obtener la clave de cifrado AES asociada a una votación.
	 * @param id La id de la votación cuya clave de cifrado AES queremos conocer
	 * @return La clave de cifrado AES asociada a una votación
	 */
	public String getSecretKey(String id){
		String res = "";
		Connection conn = null;
		String url = "jdbc:mysql://egc.jeparca.com:3306/jeparcac_egc";
		String USER = "jeparcac_egc";
	    String PASS = "kqPTE8dLz3GVtks";
	    
	    try {			
		  conn = DriverManager.getConnection(url, USER, PASS);
	      Statement select = conn.createStatement();
	      ResultSet result = select
	          .executeQuery("SELECT privateKey FROM keysvotes where idvotation="+id);
	      
	      result.next();
	      res = result.getString(1);
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	      if (conn != null) {
	        try {
	          conn.close();
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    }
	    
	    return res;
	}
	
	/**
	 * Función usada para obtener la clave pública RSA asociada a una votación.
	 * @param id La id de la votación cuya clave pública RSA queremos conocer
	 * @return La clave pública asociada a una votación
	 */
	public String getPublicKey(String id){
		String res = "";
		Connection conn = null;
		String url = "jdbc:mysql://egc.jeparca.com:3306/jeparcac_egc";
		String USER = "jeparcac_egc";
	    String PASS = "kqPTE8dLz3GVtks";
	    
	    try {			
		  conn = DriverManager.getConnection(url, USER, PASS);
	      Statement select = conn.createStatement();
	      ResultSet result = select
	          .executeQuery("SELECT publicKey FROM keysvotes where idvotation="+id);
	      
	      result.next();
	      res = result.getString(1);
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	      if (conn != null) {
	        try {
	          conn.close();
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    }
	    
	    return res;
	}

	public static boolean sendGeneratedToken(Integer votationId, Integer token){
		boolean success = false;
		Connection conn = null;
		Statement stmt = null;
	    String USER = "jeparcac_egc";
	    String PASS = "kqPTE8dLz3GVtks";  
	    String DB_URL = "jdbc:mysql://egc.jeparca.com:3306/jeparcac_egc";
		
		try {	
		
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		
		stmt = conn.createStatement();
		
		String sql = "INSERT INTO token (idvotation, accesstoken)" +
                "VALUES (?, ?)";
		
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, votationId);
        preparedStatement.setInt(2, token);
        int r = preparedStatement.executeUpdate(); 
        
        if(r == 1){
        	success = true;
        }else if(r == 0){
        	success = false;
        }
	    
		} catch(SQLException se) {
	        System.out.println("Ooops! We have detected an error. Please, check that your votationID hasn't a generated token.");
	    }catch(Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if(stmt != null)
	                conn.close();
	        } catch(SQLException se) {
	        }
	        try {
	            if(conn != null)
	                conn.close();
	        } catch(SQLException se) {
	            se.printStackTrace();
	        }
	    }
		
		return success;
	}

	public static Integer getAccessToken(Integer votationId){
		Integer result = -1;
		Connection conn = null;
		Statement stmt = null;
	    String USER = "jeparcac_egc";
	    String PASS = "kqPTE8dLz3GVtks";  
	    String DB_URL = "jdbc:mysql://egc.jeparca.com:3306/jeparcac_egc";
		
		try {	
		
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		
		stmt = conn.createStatement();
		
		String sql = "SELECT * FROM token WHERE idvotation = ?";
		
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, votationId);
        ResultSet r = preparedStatement.executeQuery();
        
        while(r.next()){
        	result = r.getInt("accesstoken");
        }
        
		} catch(SQLException se) {
	        se.printStackTrace();
	    } catch(Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if(stmt != null)
	                conn.close();
	        } catch(SQLException se) {
	        }
	        try {
	            if(conn != null)
	                conn.close();
	        } catch(SQLException se) {
	            se.printStackTrace();
	        }
	    }
		
		return result;
	}
	
}
