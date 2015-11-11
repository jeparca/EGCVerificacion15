package test.java;


import java.util.Random;

import javax.crypto.BadPaddingException;

import junit.framework.Assert;

import main.java.Authority;
import main.java.AuthorityImpl;
import main.java.AuthorityImplAES;


import org.junit.Test;

public class VerificacionTest {
	
	Authority au = new AuthorityImpl();
	AuthorityImplAES auAes = new AuthorityImplAES();
	
	//Este primer test consiste en recuperar una clave publica de una votacion existente
	@Test
	public void test1() {
		String publicKey = au.getPublicKey(String.valueOf(1000));
		Assert.assertEquals(" MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA06b4WdqpKC77rx3xIkdPD0g4cJ5BixkdylE+0peLK65gOa994fYtP4+SGh1yMDFWmdHgQrn6v7qFCJhSsl6aF4dlOcJTomfeXxwYDH5Ofnwb4wvhGXcZxROP522EQWYaFY0aeQR3sjXkpOCt7espQL6HfVLIWNn/QopQxGMXdKnY7IUFuQzPKczfuBTxMmLK/8mfZ9ii19IvfnX7mFUbm6T/Sn8fmrVm757RONfBklSVF7IF+sCYOTocnmF91+erUWyCbD665f8M/xYzlcKy+ztQ3Gf/ntnkPa8v3F72r0rfSTaGZnPFKNB3MRzw/B8lLJ2jhwoZ044ItI/53mGuLQIDAQAB", publicKey);

	}

	//Este test consiste en recuperar una clave privada de una votacion existente
	@Test
	public void test2() {
		String privateKey = au.getPrivateKey(String.valueOf(1000));
		Assert.assertEquals(" MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDTpvhZ2qkoLvuvHfEiR08PSDhwnkGLGR3KUT7Sl4srrmA5r33h9i0/j5IaHXIwMVaZ0eBCufq/uoUImFKyXpoXh2U5wlOiZ95fHBgMfk5+fBvjC+EZdxnFE4/nbYRBZhoVjRp5BHeyNeSk4K3t6ylAvod9UshY2f9CilDEYxd0qdjshQW5DM8pzN+4FPEyYsr/yZ9n2KLX0i9+dfuYVRubpP9Kfx+atWbvntE418GSVJUXsgX6wJg5OhyeYX3X56tRbIJsPrrl/wz/FjOVwrL7O1DcZ/+e2eQ9ry/cXvavSt9JNoZmc8Uo0HcxHPD8HyUsnaOHChnTjgi0j/neYa4tAgMBAAECggEAbp35dpy9g+7qQ5/Wgx/ZxhbsyI8tmlFKLI9B0ja52d6NFBkQJaTQCUQ0C8oP//gJVuNF3hntX39QQCm8LtUam+l7kjBqHZtDsVmxh/YLeDfNK2DuCK0insmWkFHAqZ1/THwutckL9ewMz89Nl+cr7Fan4Wv+odFtg41eoQVZIPMgyCyG0j/gVKkQKrPnvfsau+cwOo2W1+r1ywOJhYyhyhpmVsObn771OBv4wT3L6tanII5n7qwUZmaZXOchQcHEBA3702knbpxYgUAwWGHkoGaZopVD/wuz2PaMWo9QgDSe8DxNvmTWT406EfXFExU4vy8fyBNPIwjS5roafSAB0QKBgQDo6K7GkQ7SplE6CLdrp8RCA1gafaOwWbHFtmOvQvnNYop4ycdh4r1jRcS0VYW2tkBz1Cs1OQS/RGboQvtmExS6Gig1HZmXrhkJHOpWnxcEKsYv7eyuRHA13K2AhCjjSJlHWWN800QcJB6SqWrtQHCRkfgbJXP64+JdKNCgNioSmwKBgQDoosjUtJpiIFZUqx6aIDKqLZur8jST+t6RvOFiW9P4lI3kGUNHL4D9qU4Svn/Vo9MuVZKH86j9cN8LjXiVFiYKQVOrYTQE/sTOqBP+gK8dBiByoPCFXrhYkn8edcBDLbu8EyMk4Tm63Qa5wT8Z8IVhjhda01kE6Lwog8mgGRYK1wKBgQDaFQ2Z5jd/7LV/5quAhT1SJnl3jI8Z0FKR838Rm1/1/T/1a9JiiIGXE7vks3OZqrtc+ZxfoH0ebvkAn79wS4uL2nQtkALCMQVGMCoIY7yUbUzXkiuc3X5IINLg1F7IurCzSzLJrbRHuVZbM4Fol38P/yLWoFIh6QGLA22dpjns4wKBgCTU4QcMMQUoKYLuNMIiJuNapTLr/fcP2P0p8mUoNL8+hdh/R2+Vzc5bZ8j/8C1a6qyDT9fGdBLsM+QYWecAwrUM8rpHgXzo+S/e3IuGxDLMLYb4hBwZiwY663VfHzmDnB2CyiWkowc6xz3RorPwqK1xWbE/lPB6CJBBjUKTtjGxAoGAJ/0NErmoqg9nFqKjaGX9QvRcze52kg9VJ4lJGO8z+r28+IPWKaUTH1oJ+damoXiZ4jOdlhiqNJ9StSVDxMnGP5CSYgYdbPzUhP8vNy3l98O7NlZcbEYvJHOmYw26aZzzcQFJg/twhYJFHTWbfNEycVxlkS5minL/Cr9WuBC3I3c=", privateKey);
	}
	
	//En este test se cifra una cadena de texto y se comprueba que no haya sido modificada
	@Test
	public void test3() {
		String texto = "Texto a cifrar";
		byte[] cifrado = au.encrypt("1000", texto);
		
		Assert.assertTrue(au.checkVote(cifrado, "1000"));
		
	}
	
	//En este test se cifra una cadena de texto y se modifica para que la comprobacion 
	// muestre el error
	@Test
	public void test4() {
		String texto = "Texto a cifrar";
		byte[] cifrado = au.encrypt("1000", texto);
		cifrado[50]=1;
		Assert.assertFalse(au.checkVote(cifrado, "1000"));
		
	}
	
	//En este test se crea un par de claves para una nueva votacion aleatoria
	@Test
	public void test5() {
		Random r = new Random();
		String id = String.valueOf(r.nextInt(1000000000)+ String.valueOf(r.nextInt(1000000000)));
		
		String publicKey = au.getPublicKey(id);
		
		while (!" ".equals(publicKey)){
			
			id = String.valueOf(r.nextInt(1000000000)+ String.valueOf(r.nextInt(1000000000)));
		}
		
		Assert.assertTrue(au.postKey(id));
		
	}
	//En este test se cifra y se descifra una cadena con formato voto
	@Test
	public void test6() {
		String texto = "{\"age\": \"24\",\"answers\":[{\"question\":\"Pregunta 1\",\"answer_question\":\"SI\"}],\"id\": 1,\"autonomous_community\":\"Andalucía\",\"genere\": \"Masculino\",\"id_poll\": 32778";
		byte[] cifrado = au.encrypt("1000", texto);
		String descifrado=null;
		try {
			descifrado = au.decrypt("1000", cifrado);
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(texto, descifrado);
		
	}
	
	//Este test detecta el error que se describe en el Issue#6 en el que se 
	// solicitaba eliminar el espacio en blanco de la primera posición 
	// obtenido al solicitar una clave AES de la base de datos.
	@Test
	public void test7() {
		String secretKey = auAes.getSecretKey("1000");
		Assert.assertNotSame(" ", secretKey.charAt(0));
		
	}
	
	//Este test consulta la clave secreta AES de la base de datos en la nube
	@Test
	public void test8() {
		String secretKey = auAes.getSecretKey(String.valueOf(1000));
		Assert.assertEquals("bmV2q1MarBMfNNvVq+AQZ1MhjRwaBHpVaMzAwBWMlB0=", secretKey);

	}

	//En este test se cifra una cadena de texto mediante el algoritmo AES
	// y se comprueba que no haya sido modificada
	@Test
	public void test9() {
		String texto = "Texto a cifrar";
		byte[] cifrado = auAes.encrypt("1000", texto, true);
		
		Assert.assertTrue(auAes.checkVote(cifrado, "1000"));
		
	}
	
	//En este test se cifra una cadena de texto mediante el algoritmo AES 
	// y se modifica para que la comprobacion muestre el error
	@Test
	public void test10() {
		String texto = "Texto a cifrar";
		byte[] cifrado = auAes.encrypt("1000", texto,true);
		cifrado[1]=1;
		Assert.assertFalse(auAes.checkVote(cifrado, "1000"));
		
	}
	
	//En este test se crea una clave AES para una nueva votacion aleatoria
	@Test
	public void test11() {
		Random r = new Random();
		String id = String.valueOf(r.nextInt(1000000000)+ String.valueOf(r.nextInt(1000000000)));
		
		String secretKey = auAes.getSecretKey(id);
		
		while (!"".equals(secretKey)){
			
			id = String.valueOf(r.nextInt(1000000000)+ String.valueOf(r.nextInt(1000000000)));
		}
		
		Assert.assertTrue(auAes.postKey(id));
		
	}
	//En este test se cifra y se descifra una cadena con formato voto mediante el algoritmo
	// AES sin añadir al final el hash
	@Test
	public void test12() {
		String texto = "{\"age\": \"24\",\"answers\":[{\"question\":\"Pregunta 1\",\"answer_question\":\"SI\"}],\"id\": 1,\"autonomous_community\":\"Madrid\",\"genere\": \"Femenino\",\"id_poll\": 32778";
		byte[] cifrado = auAes.encrypt("1000", texto,false);
		String descifrado=null;
		descifrado = auAes.decrypt("1000", cifrado,false);
		
		Assert.assertEquals(texto, descifrado);
		
	}
	
	//En este test se cifra y se descifra una cadena con formato voto mediante el algoritmo
	// AES añadiendo al final el hash
	@Test
	public void test13() {
		String texto = "{\"age\": \"24\",\"answers\":[{\"question\":\"Pregunta 1\",\"answer_question\":\"SI\"}],\"id\": 1,\"autonomous_community\":\"Madrid\",\"genere\": \"Femenino\",\"id_poll\": 32778";
		byte[] cifrado = auAes.encrypt("1000", texto,true);
		String descifrado=null;
		descifrado = auAes.decrypt("1000", cifrado,true);
		
		Assert.assertEquals(texto, descifrado);
		
	}

	
	

	
}
