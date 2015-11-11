package main.java;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;




public class AuthorityImpl implements Authority{


	/**
	 * Esta función es usada para crear un par de claves de cifrado RSA y almacenarlas
	 * en una base de datos en Hostinger. La función creará el par de claves siempre
	 * que no exista ya una entrada en la base de datos para la misma votación.
	 * @param id. El parámetro id se refiere a la id de la votación a la que se le asociará
	 * el par de claves.
	 * @return success. Boolean que indica si la operación ha tenido éxito.
	 */
	public boolean postKey(String id) {
		boolean success = false;
		try {

			
			 KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			
			 SecureRandom random = SecureRandom.getInstance("SHA1PRNG","SUN");
			 keyGen.initialize(2048, random);
			
			 //Generamos el par de claves
			 KeyPair pair = keyGen.generateKeyPair();
			
			 //Convertimos las claves a un String en Base64 para almacenarlas en la BD
			 String publicKey = DatatypeConverter.printBase64Binary(pair.getPublic().getEncoded());
			 String privateKey= DatatypeConverter.printBase64Binary(pair.getPrivate().getEncoded());
			 
			 
			 RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
			 //Llamamos a la función que se encarga de guardar el par de claves asociadas
			 // a la votación cuya id se especifica como parámetro.
			 if (rdbm.postKeys(id, publicKey, privateKey)){
				 success = true;
			 }
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	
		
		return success;
	}

	/**
	 * Esta función obtiene la clave pública RSA de una votación que exista en la base de datos.
	 * @param id. Corresponde al id de la votación
	 * @return String que contiene la clave pública
	 */
	public String getPublicKey(String id) {
		RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
		//Llamamos a la función que conecta con la base de datos remota y obtiene la clave pública.
		return rdbm.getPublicKey(id);
	}

	/**
	 * Esta función obtiene la clave privada RSA de una votación que exista en la base de datos.
	 * @param id. Corresponde al id de la votación
	 * @return String que contiene la clave privada
	 */
	public String getPrivateKey(String id) {

		RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
		
		//Llamamos a la función que conecta con la base de datos remota y obtiene la clave privada.
		return rdbm.getPrivateKey(id);
	}

	/**
	 * Función que comprueba que un voto no ha sido modificado.
	 * @param votoCifrado. Corresponde con el voto cifrado mediante el algoritmo RSA
	 * @param id. Corresponde a la id de la votación.
	 * @return boolean que indica si la comprobación ha sido correcta o no. Si 
	 * ha sido correcta se devuelve true y en caso contrario false.
	 */
	public boolean checkVote(byte[] votoCifrado, String id) {
		
		boolean res = true;
		try {
			//Intentamos descifrar el voto y capturamos la excepción.
			decrypt(id, votoCifrado);
		} catch (BadPaddingException e) {
			res = false;
		}
		return res;

	}

	/**
	 * Función que cifra mediante RSA una cadena de texto con la clave pública de la votación 
	 * asociada a la id de votación enviada como parámetro.
	 * @param idVote. La id de la votación cuya clave pública queremos usar para cifrar.
	 * @param textToEncrypt. El texto que deseamos cifrar.
	 * @return array de byte que contiene el mensaje cifrado
	 */
	public byte[] encrypt(String idVote,String textToEncypt){
		
		byte[] res = null;
		try {
			Cipher rsa;
			Authority authority = new AuthorityImpl();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			
			//Construimos el objeto PublicKey a partir de la cadena de la base de datos
			KeySpec keySpec = new X509EncodedKeySpec(DatatypeConverter.parseBase64Binary(authority.getPublicKey(idVote)));
			PublicKey pubKeyFromBytes = keyFactory.generatePublic(keySpec);
			
			rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			rsa.init(Cipher.ENCRYPT_MODE, pubKeyFromBytes);
	    
			//Realizamos el cifrado
			res = rsa.doFinal(textToEncypt.getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			
			e.printStackTrace();
		} 
		
		return res;
	}
	
	/**
	 * Función que descifra mediante RSA un array de byte usando la clave privada asociada a la votación
	 * cuyo id se pasa como parámetro. Lanza una excepción que nos ayudará a comprobar si 
	 * el voto ha sido modificado.
	 * @param idVote. La id de la votación cuya clave privada queremos usar para descifrar.
	 * @param cipherText. El texto que deseamos descifrar.
	 * @return cadena de texto que contiene el mensaje original.
	 */
	public String decrypt(String idVote,byte[] cipherText) throws BadPaddingException{
		
		String res = null;
		try {
			Cipher rsa;
			Authority authority = new AuthorityImpl();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			//Construimos el objeto PrivateKey a partir de la cadena de la base de datos
			KeySpec keySpec = new PKCS8EncodedKeySpec(DatatypeConverter.parseBase64Binary(authority.getPrivateKey(idVote)));
			PrivateKey privKeyFromBytes = keyFactory.generatePrivate(keySpec);
			
			rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			rsa.init(Cipher.DECRYPT_MODE, privKeyFromBytes);
			
	    
			//Se procede a descifrar el texto cifrado
			byte[] bytesDesencriptados = rsa.doFinal(cipherText);
		    res = new String(bytesDesencriptados);
		} catch (IllegalBlockSizeException  | InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			
			e.printStackTrace();
		}

		
		return res;
	}
}
