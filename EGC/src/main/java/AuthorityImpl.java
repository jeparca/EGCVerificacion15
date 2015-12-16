package main.java;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class AuthorityImpl implements Authority{
	
	/**
	 * Esta función obtiene las claves pública y privada de la votación cuyo id es el pasado 
	 * como parámetro. Resaltar que hacemos uso del proyecto Elliptic_SDK, que  es una librería
	 * criptográfica elíptica bajo la licensia GPL v3. Más información en la clase CryptoEngine.java
	 * @param id. Corresponde al id de la votación
	 * @return res. Boolean que indica si la operación ha tenido éxito.
	 */
	public boolean postKey(String id, Integer token) {
		boolean res;
		BigInteger secretKey;
		String publicKey;
		String encodedPublicKey;
		res = false;
		
		if(Token.checkToken(new Integer(id), token)){
			try{
				
				Token.createToken(new Integer(id));				
				
				CryptoEngine cryptoEngine = new CryptoEngine(id);
				cryptoEngine.generateKeyPair();
		
				secretKey = cryptoEngine.getKeyPair().getSecretKey();
				publicKey = cryptoEngine.getKeyPair().getPublicKey().getX()+"++++"+cryptoEngine.getKeyPair().getPublicKey().getY();
				
				//encodedPublicKey = DatatypeConverter.printBase64Binary(publicKey.getBytes());
				encodedPublicKey = new String(Base64.encodeBase64(publicKey.getBytes()));
				
				RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
				 //Llamamos a la función que se encarga de guardar el par de claves asociadas
				 // a la votación cuya id se especifica como parámetro.

				if (rdbm.postKeys(id, encodedPublicKey, secretKey.toString())){
					res = true;
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
//			System.out.println("El token no coincide");
			throw new VerificationException("El token no coincide");
		}
		
		return res;
	}

	public String getPublicKey(String id, Integer token) {
		
		String result = "";
		
		if(Token.checkTokenDb(new Integer(id), token)){
			RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
			//Llamamos a la función que conecta con la base de datos remota y obtiene la clave pública.
			result = rdbm.getPublicKey(id);
		}else{
//			System.out.println("El token no coincide en getPublicKey");
			throw new VerificationException("El token no coincide en getPublicKey");
		}
		
		return result;
		
	}

	public String getPrivateKey(String id, Integer token) {
		
		String result = "";
		
		if(Token.checkTokenDb(new Integer(id), token)){
			RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
			//Llamamos a la función que conecta con la base de datos remota y obtiene la clave privada.
			result = rdbm.getSecretKey(id);
		}else{
//			System.out.println("El token no coincide en getPrivateKey");
			throw new VerificationException("El token no coincide en getPrivateKey");
		}		
		
		return result;
	}

	public boolean checkVote(byte[] votoCifrado, String id, Integer token) {
		String vote, text, sha11;
		boolean res = false;
		
		try{
		
			vote = decrypt(id, votoCifrado, token);
			
			text = new String(votoCifrado, "UTF-8");			
			sha11 = text.substring(0, text.indexOf("?"));
			
			if(sha11.equals(stringToSHA1(vote))){
				res = true;
			}
		
		}catch(Exception e){
			
		}
		
		return res;
	}

	public byte[] encrypt(String idVote, String textToEncypt, Integer token) {
		byte[] result = {};
		CryptoEngine ce = new CryptoEngine(idVote);
		WeierStrassCurve curve = ce.curve;
		String publicKeyBD = "";
		String text = "";
		PointGMP publicKey;
		String encryptText = "";
		BigInteger x;
		BigInteger y;
		
		if(Token.checkTokenDb(new Integer(idVote), token)){
			String[] cutText = cutVote(textToEncypt);
			
			//obtengo la clave publica con getKey y separa esto en x e y (que es la mitad y hacer un new PointGMP) acordarse de que  
			//en la bd se guarda en base64
			publicKeyBD = getPublicKey(idVote, token);
			//byte[] keyDecoded = Base64.getDecoder().decode(publicKeyBD.getBytes());
			byte[] keyDecoded = Base64.decodeBase64(publicKeyBD.getBytes());
			publicKeyBD = new String(keyDecoded);
					
			x = new BigInteger(publicKeyBD.substring(0, publicKeyBD.indexOf("+")).trim());
			y = new BigInteger(publicKeyBD.substring(publicKeyBD.lastIndexOf("+") + 1, publicKeyBD.length()).trim());
					
			publicKey = new PointGMP(x, y, curve);				
			
			for (String s: cutText){
				String encriptAux = "";
				
				encriptAux = ce.encodeString(s, publicKey);	

				int from = encriptAux.indexOf('/');
				int to = encriptAux.length();
				encriptAux = encriptAux.substring(from + 4,to);	

				//tamaño de encriptAux = 77
				if(!encryptText.equals("")){
					encryptText = encryptText + "|" + encriptAux;
				}else{
					encryptText = encriptAux;
				}
					
			}
			//quito los espacios delanteros y traseros
			encryptText = encryptText.trim();
			
			text = stringToSHA1(textToEncypt)+ "?" + encryptText;
			
			//convierto a byte[]
			result = text.getBytes();

		}else{
			//System.out.println("El token no coincide en encriptar");
			throw new VerificationException("El token no coincide en encriptar");
		}
				
		return result;
	}
	
	public String decrypt(String idVote, byte[] cipherText, Integer token) throws BadPaddingException, UnsupportedEncodingException {
		String result = "";
		CryptoEngine ce;
		String cipherTextString;
		String decoded;
		String secretKey;
		String publicKey;
		String text;
		
		if(Token.checkTokenDb(new Integer(idVote), token)){
			ce = new CryptoEngine(idVote);
			
			secretKey = getPrivateKey(idVote, token);
			
			publicKey = getPublicKey(idVote, token);
			//byte[] keyDecoded2 = Base64.getDecoder().decode(publicKey.getBytes());
			byte[] keyDecoded2 = Base64.decodeBase64(publicKey.getBytes());
			publicKey = new String(keyDecoded2);
			
			int longKey = (publicKey.length()-4)/2;

			ce.generateKeyPair(new PointGMP(new BigInteger(publicKey.substring(0, longKey)),
					new BigInteger(publicKey.substring(longKey+4, publicKey.length())), ce.curve), 
					new BigInteger(secretKey));

			text = new String(cipherText, "UTF-8");
			
			cipherTextString = text.substring(text.indexOf("?")+1, text.length());
			
			result = "";
			
			for (String s: cutCifVote(cipherTextString)){
				String s2;
				decoded = "";
				s2 = publicKey + "////" + s;
				
				decoded = ce.decodeString(s2, secretKey);
				result = result + decoded;
				
			}

		}else{
//			System.out.println("El token no coincide en desencriptar");
			throw new VerificationException("El token no coincide en desencriptar");
		}
				
		return result;
	}

	@Override
	public String[] cutVote(String votoEnClaro) {
		
		//Intervalo de corte del string 
		int intervalo;		
		int arrayLength;
	    String[] result;
	    
	    intervalo = 31;
	    arrayLength = (int) Math.ceil(((votoEnClaro.length() / (double)intervalo)));
	    result = new String[arrayLength];

	    int j = 0;
	    int lastIndex = result.length - 1;
	    for (int i = 0; i < lastIndex; i++) {
	        result[i] = votoEnClaro.substring(j, j + intervalo);
	        j += intervalo;
	    } //Añado el ultimo bloque
	    result[lastIndex] = votoEnClaro.substring(j);

	    return result;
	}
	
	@Override
	public String[] cutCifVote(String votoCifrado) {
		
		List<String>  res = new ArrayList<String>();
		List<Integer> indices = new ArrayList<Integer>();
		String[] result;
		
		int index = votoCifrado.indexOf("|");
		while(index >= 0) {
		    indices.add(index);
		    index = votoCifrado.indexOf("|", index+1);
		}
		
		if(indices.size() != 0){
		
			int to = 0;
			int from = 0;
			
			for(Integer p: indices){
				
				to = p;
				res.add(votoCifrado.substring(from, to));
				from = to + 1;
				
				if(p == indices.get(indices.size()-1)){
					res.add(votoCifrado.substring(from, votoCifrado.length()));
				}
				
			}

			result = new String[res.size()];
			result = res.toArray(result);
			
		}else{
			
			result = new String[1];
			result[0] = votoCifrado;
			
		}

		return result;
		
	}
	
	private String stringToSHA1(String text){
		String result = "";
		
		result = DigestUtils.sha1Hex(text);
		
		return result;
	}
	

}
