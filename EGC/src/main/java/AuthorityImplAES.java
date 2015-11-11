package main.java;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class AuthorityImplAES{

	/**
	 * Esta función es usada para crear una clave de cifrado AES de 256 bits y almacenarla
	 * en una base de datos en Hostinger. La función creará la clave siempre
	 * que no exista ya una entrada en la base de datos para la misma votación.
	 * @param id. El parámetro id se refiere a la id de la votación a la que se le asociará
	 * la clave.
	 * @return success. Boolean que indica si la operación ha tenido éxito.
	 */
	public boolean postKey(String id) {
		boolean success = false;
		try {

			
			KeyGenerator key = KeyGenerator.getInstance("AES"); 
			key.init(256); 
			//Generamos la clave de 256 bits
			SecretKey secretKey = key.generateKey(); 
			
			//Convertimos la clave a Base64. 
			String secretKeyString = DatatypeConverter.printBase64Binary(secretKey.getEncoded());
			 
			//Llamamos a la función para almacenar la clave de cifrado en la base de datos
			// de hostinger.
			RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
			if (rdbm.postAESKey(id, secretKeyString)){
				success = true;
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	
		
		return success;
	}
	
	/**
	 * Función que obtiene la clave de cifrado AES asociada a una votación
	 * @param id La id de la votación cuya clave desea conocerse
	 * @return La clave de cifrado asociada a la votación
	 */
	public String getSecretKey(String id) {
		RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
		
		//Llamamos a la función que conecta con la base de datos remota y devuelve
		// el valor de la clave asociada a la votación.
		return rdbm.getSecretKey(id);
	}

	/**
	 * Función que comprueba que un voto cifrado mediante AES no ha sido modificado.
	 * Para ello, es necesario que antes de cifrar dicho voto, se le añada al final del voto
	 * el resultado de calcula su código Hash mediante el algoritmo md5.
	 * @param votoCifrado El voto que se quiere comprobar si ha sido modificado o no. Debe incluir
	 * el su código hash al final calculado mediante el algoritmo md5.
	 * @param id El id de la votación para obtener la clave para descifrar
	 * @return
	 */
	public boolean checkVote(byte[] votoCifrado, String id) {
		AuthorityImplAES authority = new AuthorityImplAES();
		//Obtenemos la clave de la base de datos
		String secretKey = authority.getSecretKey(id);
		Boolean res = null;
		   
		SecretKeySpec key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(secretKey), "AES");
	  	Cipher cipher;
	  	try {
	  		cipher = Cipher.getInstance("AES");
		    
	  		cipher.init(Cipher.DECRYPT_MODE, key);
	  		//Obtenemos el resultado de descifrar el array de byte que nos llega como parámetro
	  		byte[] resByte = cipher.doFinal(votoCifrado);
	  		
	  		//Creamos dos nuevos arrays, uno para almacenar el hash y otro para el mensaje original
	  		byte[] hash = new byte[16];
	  		byte[] originalText = new byte[resByte.length - hash.length];
	  		
	  		//Copiamos en la variable 'originalText' el mensaje original y en la 
	  		// variable 'hash' el codigo hash del mensaje, que corresponde a los 
	  		// últimos 16 bytes
	  		System.arraycopy(resByte, 0, originalText, 0, originalText.length);
	  		System.arraycopy(resByte, originalText.length, hash, 0, hash.length);
	  		
	  		//Obtenemos el código hash de la variable 'originalText'
	  		MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] newHash = md.digest(originalText);
			
			//Comparamos que el hash recién calculado coincida con el 
			// que teníamos previamente
			res = Arrays.equals(hash, newHash);
		 

	  	}catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Ffunción que cifra un texto usando el algoritmo AES y la clave de cifrado asociada a 
	 * la votación cuyo parámetro se recibe. También presenta la opción de añadir
	 * el código hash del texto justo detrás antes de cifrar.
	 * @param idVote La id de la votación cuya clave se quiere usar para el cifrado
	 * @param textToEncypt Texto a cifrar
	 * @param addHashCode Si toma el valor 'True' se añadirá tras el texto original
	 * su código hash calculado mediante el algoritmo md5
	 * @return
	 */
	public byte[] encrypt(String idVote, String textToEncypt,Boolean addHashCode) {
		AuthorityImplAES authority = new AuthorityImplAES();
		String secretKey = authority.getSecretKey(idVote);
		byte[] res = null;
		byte[] bytesToEcrypt = null;
		byte[] bytesOfMessage = null;
		SecretKeySpec key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(secretKey), "AES");
	  	Cipher cipher;
	  	try {
	  		
	  		bytesOfMessage = textToEncypt.getBytes("UTF-8");
	  		//Si está activada la opción de añadir hash al final, calculamos el hash 
	  		//para añadirlo 
	  		if(addHashCode){ 
				MessageDigest md;
				md = MessageDigest.getInstance("MD5");
				byte[] theDigest = md.digest(bytesOfMessage);
				
				//Añadimos al final del texto el hash creado
				bytesToEcrypt = new byte[bytesOfMessage.length+theDigest.length];
		  		System.arraycopy(bytesOfMessage, 0, bytesToEcrypt, 0, bytesOfMessage.length);
		  		System.arraycopy(theDigest, 0, bytesToEcrypt, bytesOfMessage.length, theDigest.length);
		  		
		  	//Si no está activada la opctión de añadir el hash, ciframos el texto
		  	// original tal y como se recibe.	
	  		}else{
	  			bytesToEcrypt = bytesOfMessage;
	  		}
	  		cipher = Cipher.getInstance("AES");
	  		
		    
	  		//Se realiza el cifrado
	  		cipher.init(Cipher.ENCRYPT_MODE, key);
	  		res = cipher.doFinal(bytesToEcrypt);
		 

	  	}catch (Exception e) {
	  		e.printStackTrace();
		}
		
		return res;
	}

	/**
	 * Función que descifra un texto usando el algoritmo AES y la clave de cifrado asociada
	 * a la votación que se recibe como parámetro. También retirá el código hash de la parte
	 * final del mensaje en el caso de que se le indique.
	 * @param idVote La id de la votación cuya clave desea usarse para descifrar.
	 * @param cipherText El texto que se desea descifrar
	 * @param hashAdded Si toma el valor 'True', indica que el mensaje tiene en la parte final
	 * su código hash de 16 bytes, el cual deberá ser eliminado
	 * @return El texto descifrado
	 */
	public String decrypt(String idVote, byte[] cipherText, Boolean hashAdded){
		
		AuthorityImplAES authority = new AuthorityImplAES();
		String secretKey = authority.getSecretKey(idVote);
		String res = null;
		   
		SecretKeySpec key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(secretKey), "AES");
	  	Cipher cipher;
	  	try {
	  		cipher = Cipher.getInstance("AES");
		    
	  		cipher.init(Cipher.DECRYPT_MODE, key);
	  		byte[] resByte = cipher.doFinal(cipherText);
	  		
	  		//Si se añadió un codigo hash para comprobaciones lo descartamos
	  		if (hashAdded){
	  			byte[] originalText = new byte[resByte.length - 16];
	  			System.arraycopy(resByte, 0, originalText, 0, originalText.length);
	  			res = new String (originalText);
	  		}else{
	  			res = new String(resByte);
	  		}
	  		 
		 

	  	}catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
		  		  
	}

}
