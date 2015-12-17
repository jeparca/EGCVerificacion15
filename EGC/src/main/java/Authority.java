package main.java;

import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;

public interface Authority {

	//Recibe la id de la votación, crea las claves y las guarda en BD.
		boolean postKey(String id, Integer token);
		
		//Recibe la id de la votación y devuelve su clave pública para poder cifrar.
		String getPublicKey(String id, Integer token);
		
		//Recibe la id de la votación y devuelve su clave privada para poder descifrar.
		String getPrivateKey(String id, Integer token);
		
		//Recibe un voto cifrado y un id de la votación, y comprueba si ese voto ha sido alterado.
		boolean checkVote(byte[] votoCifrado, String id, Integer token);
		
		//Encripta el texto con la clave pública de la votación cuya id se pasa como parámetro.
		byte[] encrypt(String idVote,String textToEncypt, Integer token);
		
		//Desencripta el texto con la clave privada de la votación cuya id se pasa como parámetro.	
		String decrypt(String idVote,byte[] cipherText, Integer token) throws BadPaddingException, UnsupportedEncodingException;
		
}
