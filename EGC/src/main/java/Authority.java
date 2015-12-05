package main.java;

import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;

public interface Authority {

	//Recibe la id de la votación, crea las claves y las guarda en BD.
		boolean postKey(String id);
		
		//Recibe la id de la votación y devuelve su clave pública para poder cifrar.
		String getPublicKey(String id);
		
		//Recibe la id de la votación y devuelve su clave privada para poder descifrar.
		String getPrivateKey(String id);
		
		//Recibe un voto cifrado y un id de la votación, y comprueba si ese voto ha sido alterado.
		boolean checkVote(byte[] votoCifrado, String id);
		
		//Encripta el texto con la clave pública de la votación cuya id se pasa como parámetro.
		byte[] encrypt(String idVote,String textToEncypt);
		
		//Desencripta el texto con la clave privada de la votación cuya id se pasa como parámetro.	
		String decrypt(String idVote,byte[] cipherText) throws BadPaddingException, UnsupportedEncodingException;
		
		//El voto recibido lo corta en bloques de 31 caracteres
		String[] cutVote(String votoEnClaro);

		//El voto cifrado recibido lo corta en bloques para su posterior recorrido en el método de descifrar
		String[] cutCifVote(String votoEnClaro);
	
}
