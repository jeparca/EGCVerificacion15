package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
/**
 * Esta clase es que se uso para guardar las claves de cifrado en una base de datos local.
 * La forma de almacenamiento fue sustituida por un alamcenamiento en la nube, con lo que 
 * esta clase está en deshuso.
 */
public class DataBaseManager {

    private Statement st = null;
    private ResultSet rs = null;
    
	public Collection<String> getVoteFromDataBase(String id) {
		Connection conection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String url = "jdbc:mysql://localhost:3306/keysvotes";
        String user = "admin";
        String password = "admin";
        String id2="a1";
        
        Collection<String> result = null;
        
        try {
        	conection = DriverManager.getConnection(url, user, password);
            st = conection.createStatement();
            rs = st.executeQuery("SELECT publicKey, privateKey FROM KeysVotes  WHERE idVotation = '"+id2+"'");

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

            conection.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

        } 
        
        return result;
    }

	/**
	 * Función que crea el objeto Connection con los parámetros de conexión adecuados.
	 * @return objeto Connection preparado para conectar con la base de datos local.
	 */
	public Connection getConnection() {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		
			//Establecemos la URL de la base de datos, el nombre de usuario 
			// y la contraseña
			String url = "jdbc:mysql://localhost:3306/keysvotes";
	        String user = "admin";
	        String password = "admin";
	        con = DriverManager.getConnection(url, user, password);
			
		} catch (ClassNotFoundException|SQLException e) {
			
			e.printStackTrace();
		}
		
		return con;
	}
	/**
	 * Función que obtiene la clave pública de una votación desde la base de datos local.
	 * @param id La id de la votación de la cual se desea obtener la clave pública
	 * @return la clave pública asociada a la id.
	 */
	public String getPublicKey(String id) {

		String res = null;
        try {
        	Connection con = getConnection();
            //Creamos y ejecutamos la consulta SQL adecuada para obtener la clave pública
        	st = con.createStatement();
            rs = st.executeQuery("SELECT publicKey FROM KeysVotes  WHERE idVotation = '"+id+"'");

            //Obtenemos la clave pública. Solo se espera que el resultSet devuelva un valor.
            if (rs.next()) {
                res = rs.getString(1);
            }

            //Cerramos la conexión
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

        } 
        
        return res;
    }
	
	/**
	 * Función que obtiene la clave privada de una votación desde la base de datos local.
	 * @param id La id de la votación de la cual se desea obtener la clave privada
	 * @return la clave privada asociada a la id.
	 */
	public String getPrivateKey(String id) {

		String res = null;
        try {
        	Connection con = getConnection();
        	
        	//Creamos y ejecutamos la consulta SQL adecuada para obtener la clave privada
        	st = con.createStatement();
            rs = st.executeQuery("SELECT privateKey FROM KeysVotes  WHERE idVotation = '"+id+"'");

            //Obtenemos la clave privada.
            if (rs.next()) {
                res = rs.getString(1);
            }
            
            //Cerramos la conexión
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

        } 
        
        return res;
    }
	
	/**
	 * Función usada para guardar un par de claves asocidadas a una id en la base de datos local
	 * @param id La id de la votación.
	 * @param publicKey Clave pública que desea guardarse asociada a la votación.
	 * @param privateKey Clave privada que desea guardarse asociada a la votación.
	 */
	public String saveKeys(String id, String publicKey, String privateKey) {

		String res = null;
        try {
        	Connection con = getConnection();
            //Creamos la sentencia SQL para guardar las claves en la base de datos
        	st = con.createStatement();
            st.execute("Insert into keysVotes Values('"+id+"', '" + publicKey + "', '" + privateKey + "' ) ");
          
            //Cerramos la conexión
            con.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

        } 
        
        return res;
    }
	
}