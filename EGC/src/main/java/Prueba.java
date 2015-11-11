package main.java;


public class Prueba {
	
	public static void main(String[] args) {
		try {
			
		//En esta clase se realiza un pequeño ejemplo de cifrado con los algorimos
		// RSA y AES.
		// En ambos casos se supone que existe una entrada en la base de datos remota
		// con id=1000 que ha sido almacenada previamente.
		
		//En primer lugar se realiza una prueba de cifrado y descifrado mediante
		// el algoritmo AES
		AuthorityImplAES authorityAES = new AuthorityImplAES();
		
		System.out.println(authorityAES.getSecretKey("1000"));
		
		String prueba = "Cifra y descifra";
		
		byte[] cifrado = authorityAES.encrypt("1000", prueba,true);
		
		System.out.println(authorityAES.checkVote(cifrado, "1000"));
		System.out.println(authorityAES.decrypt("1000", cifrado,true));
		
		//En segundo lugar, se realiza la misma prueba pero usando el algoritmo
		// RSA
		Authority authority = new AuthorityImpl();
		
		byte[] cifradoRSA = authority.encrypt("1000", prueba);
		System.out.println(authority.decrypt("1000", cifradoRSA));
		

		
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
	}
}
