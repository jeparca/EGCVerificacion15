package test;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;

import javax.crypto.BadPaddingException;

import org.junit.Assert;
import org.junit.Test;

import exceptions.VerificationException;
import main.java.Authority;
import main.java.AuthorityImpl;


public class VerificacionTest {
	
	private static Authority auth = new AuthorityImpl();
	
	private static final Integer[] tokenAuxList = {120338, 127508, 219240, 231958, 
			264907, 301200, 301415, 318851, 328237, 333555, 366710, 376217, 382413, 
			406463, 409921, 436780, 458841, 461513, 530897, 589116, 590265, 590815, 
			593252, 656720, 746976, 830375, 865247, 869061, 885540, 907197, 909246, 
			961864, 976931, 982612};
	    
		private static Integer calculateToken(Integer votationId){
		
			Integer token = 0;
			
			checkId(votationId);
			
			String binaryInteger = Integer.toBinaryString(votationId);
			char[] numberByNumber = binaryInteger.toCharArray();
			
			int j = 0;
			for(int i=numberByNumber.length-1; 0 <= i; i--){
				String binDigit = Character.toString(numberByNumber[i]);
				Integer digit = new Integer(binDigit);
				if(digit > 0){
					token += digit*tokenAuxList[tokenAuxList.length-1-j];
					
				}
				j++;
			}
		
		return token*17;
		
		}  
		
		private static void checkId(Integer votationId) {
			assert votationId <= 999999998;
			
		}
	
	
	@Test 
	public void testPostKey1(){
		String votationId;
		Integer token;
		boolean res;
		
		votationId = (new BigInteger(31, new Random())).toString();
		
		token = calculateToken(new Integer(votationId));
		
		res = auth.postKey(votationId, token);
		
		
		Assert.assertTrue(res == true);		
		
	}
	
	@Test(expected = VerificationException.class)
	public void testPostKey2(){
		String votationId;
		Integer token;
		boolean res;
		
		votationId = (new BigInteger(31, new Random())).toString();
		
		token = 123456789;
		
		res = auth.postKey(votationId, token);
		
		
		Assert.assertTrue(res == true);		
		
	}
	
	@Test
	public void encryptDecryptTest1() throws BadPaddingException, UnsupportedEncodingException{
		String votationId;
		String encrypText;
		Integer token2;
		byte[] encriptado;
		
		votationId = (new BigInteger(31, new Random())).toString();
		token2 = calculateToken(new Integer(votationId));		
		
		auth.postKey(votationId, token2);
		
		encrypText = "prueba prueba";
		
		encriptado = auth.encrypt(votationId, encrypText, token2);
		
		//---------------------------------
		
		String desencriptado;
		
		desencriptado = auth.decrypt(votationId, encriptado, token2);
		//System.out.println(desencriptado);
		Assert.assertTrue(encrypText.equals(desencriptado));
	}
	
	@Test(expected = VerificationException.class)
	public void encryptDecryptTest2() throws BadPaddingException, UnsupportedEncodingException{
		String votationId;
		String encrypText;
		Integer token2;
		byte[] encriptado;
		
		votationId = (new BigInteger(31, new Random())).toString();
		token2 = calculateToken(new Integer(votationId));		
		
		auth.postKey(votationId, token2);
		
		encrypText = "prueba prueba";
		
		encriptado = auth.encrypt(votationId, encrypText, token2);
		
		//---------------------------------
		
		String desencriptado;
		
		Integer token3 = 111111111;
		
		desencriptado = auth.decrypt(votationId, encriptado, token3);
		//System.out.println(desencriptado);
		Assert.assertTrue(encrypText.equals(desencriptado));
	}
	
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void encryptDecryptTest3() throws BadPaddingException, UnsupportedEncodingException{
		String votationId;
		String encrypText;
		Integer token2;
		byte[] encriptado;
		
		votationId = (new BigInteger(31, new Random())).toString();
		token2 = calculateToken(new Integer(votationId));		
		
		auth.postKey(votationId, token2);
		
		encrypText = "";
		
		encriptado = auth.encrypt(votationId, encrypText, token2);
		
		//---------------------------------
		
		String desencriptado;
		
		desencriptado = auth.decrypt(votationId, encriptado, token2);
		
		Assert.assertTrue(encrypText.equals(desencriptado));
	}
	
		

}
