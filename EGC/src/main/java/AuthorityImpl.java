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
	 * como parámetro. Resaltar que hacemos uso del proyecto Elliptic_SDK, que es una librería
	 * criptográfica el�ptica bajo la licensia GPL v3. Más información en la clase CryptoEngine.java.
	 * Se crean las claves mediante el m�todo generateKeyPair() de la clase CryptoEngine.
	 * La clave pública es de la forma xxxxxxx++++yyyyyyy, siendo el + un separador.
	 * Se cifra en base64 solo la clave pública y se guarda la clave pública y privada en la base de
	 * datos. Finalmente se comprueba que se haya guardado correctamente.
	 * @param id. Corresponde al id de la votación
	 * @param token. Corresponde al token que se comprobará si es el adecuado para seguir la operación.
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
			throw new VerificationException("El voto ha sido modificado durante la votaci�n");
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
			
			//Obtengo la clave pública con getKey y separa esto en x e y (que es la mitad y hacer un new PointGMP). Acordarse de que  
			//en la base de datos se guarda en base64
			publicKeyBD = getPublicKey(idVote, token);

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

				//Tamaño de encriptAux = 77
				if(!encryptText.equals("")){
					encryptText = encryptText + "|" + encriptAux;
				}else{
					encryptText = encriptAux;
				}
					
			}
			//Quito los espacios delanteros y traseros
			encryptText = encryptText.trim();
			
			text = stringToSHA1(textToEncypt)+ "?" + encryptText;
			
			//Convierto a byte[]
			result = text.getBytes();

		}else{
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
			throw new VerificationException("El token no coincide en desencriptar");
		}
				
		return result;
	}

	/**
	 * Esta función corta un voto en claro en partes de longitud 31 para que el método de cifrar
	 * funcione correctamente.
	 * @param votoEnClaro. String que representa el voto en claro para cortar.
	 * @return result. String[] que indica los distintos trozos en los que ha sido cortado el voto en claro.
	 */
	private String[] cutVote(String votoEnClaro) {
		
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
	    } //Añado el último bloque
	    result[lastIndex] = votoEnClaro.substring(j);

	    return result;
	}
	
	/**
	 * Esta función corta un voto cifrado en partes situadas entre el símbolo "|", de esa forma en el
	 * método decrypt descifrará las distintas partes cifradas que han resultado de cifrar los trozos
	 * que devuelve el método cutVote.
	 * @param votoCifrado. String que representa el voto cifrado para cortar.
	 * @return result. String[] que indica los distintos trozos en los que ha sido cortado el voto cifrado.
	 */
	private String[] cutCifVote(String votoCifrado) {
		
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
