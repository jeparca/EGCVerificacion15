package test.java;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import main.java.Authority;
import main.java.AuthorityImpl;
import main.java.VerificationException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VerificacionTest {
	
	private static Authority auth = new AuthorityImpl();
	
	private static List<String> idUtilizados = new ArrayList<String>();
	
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
	
	private static Connection getDatabaseConnection(){
		String USER = "jeparcac_egc";
	    String PASS = "kqPTE8dLz3GVtks";  
	    String DB_URL = "jdbc:mysql://egc.jeparca.com:3306/jeparcac_egc";
	    
	    Connection conn = null;
	    
	    try{
	    	Class.forName("com.mysql.jdbc.Driver").newInstance();
	    	conn = DriverManager.getConnection(DB_URL, USER, PASS);
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
	    
	    return conn;	    
	    
	}
	
	private static void checkId(Integer votationId) {
		assert votationId <= 999999998;
		
	}
	
	
	@Test
	public void test1PostKey1(){
		String votationId;
		Integer token;
		boolean res;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		
		token = calculateToken(new Integer(votationId));
		
		res = auth.postKey(votationId, token);
		
		idUtilizados.add(votationId);
		
		Assert.assertTrue(res == true);		
		
	}
	
	@Test(expected = VerificationException.class)
	public void test2PostKey2(){
		String votationId;
		Integer token;
		boolean res;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		
		token = 123456789;
		
		res = auth.postKey(votationId, token);
				
		
	}
	
	@Test
	public void test3EncryptDecryptTest1() throws BadPaddingException, UnsupportedEncodingException{
		String votationId;
		String encrypText;
		Integer token2;
		byte[] encriptado;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		token2 = calculateToken(new Integer(votationId));		
		
		idUtilizados.add(votationId);
		
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
	public void test4EncryptDecryptTest2() throws BadPaddingException, UnsupportedEncodingException{
		String votationId;
		String encrypText;
		Integer token2;
		byte[] encriptado;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		token2 = calculateToken(new Integer(votationId));		
		
		idUtilizados.add(votationId);
		
		auth.postKey(votationId, token2);
		
		encrypText = "prueba prueba";
		
		encriptado = auth.encrypt(votationId, encrypText, token2);
		
		//---------------------------------
		
		String desencriptado;
		
		Integer token3 = 111111111;
		
		desencriptado = auth.decrypt(votationId, encriptado, token3);
		
		
	}
	
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void test5EncryptDecryptTest3() throws BadPaddingException, UnsupportedEncodingException{
		String votationId;
		String encrypText;
		Integer token2;
		byte[] encriptado;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		token2 = calculateToken(new Integer(votationId));		
		
		idUtilizados.add(votationId);
		
		auth.postKey(votationId, token2);
		
		encrypText = "";
		
		encriptado = auth.encrypt(votationId, encrypText, token2);
		
		//---------------------------------
		
		String desencriptado;
		
		desencriptado = auth.decrypt(votationId, encriptado, token2);
		

	}
	
	@Test
	public void test6DeleteEntriesInDatabase(){
		Integer res = 0;
		Connection conn = null;
		Statement stmt = null;
	    
		try {	
		
			for(String id: idUtilizados){
				conn = getDatabaseConnection();
				
				stmt = conn.createStatement();

				String sql = "DELETE FROM keysvotes " +
		                "WHERE idvotation="+id;
				
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
		        int r = preparedStatement.executeUpdate();

		        res = res + r;
		        
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
		
		Assert.assertTrue(res == 4);

	}

}
