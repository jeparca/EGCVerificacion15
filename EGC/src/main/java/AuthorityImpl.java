package main.java;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.xml.bind.DatatypeConverter;

public class AuthorityImpl implements Authority{
	
	/**
	 * Esta función obtiene las claves pública y privada de la votación cuyo id es el pasado 
	 * como parámetro. Resaltar que hacemos uso del proyecto Elliptic_SDK, que  es una librería
	 * criptográfica elíptica bajo la licensia GPL v3. Más información en la clase CryptoEngine.java
	 * @param id. Corresponde al id de la votación
	 * @return res. Boolean que indica si la operación ha tenido éxito.
	 */
	public boolean postKey(String id) {
		boolean res;
		BigInteger secretKey;
		String publicKey;
		String encodedSecretKey, encodedPublicKey;
		res = false;
		
		try{
		
			CryptoEngine cryptoEngine = new CryptoEngine(id);
			cryptoEngine.generateKeyPair();
	
			secretKey = cryptoEngine.getKeyPair().getSecretKey();
			publicKey = cryptoEngine.getKeyPair().getPublicKey().getX()+"++++"+cryptoEngine.getKeyPair().getPublicKey().getY();
			
			encodedPublicKey = DatatypeConverter.printBase64Binary(publicKey.getBytes());
			
			RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
			 //Llamamos a la función que se encarga de guardar el par de claves asociadas
			 // a la votación cuya id se especifica como parámetro.

			if (rdbm.postKeys(id, encodedPublicKey, secretKey.toString())){
				res = true;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return res;
	}

	public String getPublicKey(String id) {
		RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
		//Llamamos a la función que conecta con la base de datos remota y obtiene la clave pública.
		return rdbm.getPublicKey(id);
	}

	public String getPrivateKey(String id) {
		RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
		//Llamamos a la función que conecta con la base de datos remota y obtiene la clave privada.
		return rdbm.getSecretKey(id);
	}

	public boolean checkVote(byte[] votoCifrado, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public byte[] encrypt(String idVote, String textToEncypt) {
		byte[] result;
		CryptoEngine ce = new CryptoEngine(idVote);
		WeierStrassCurve curve = ce.curve;
		String publicKeyBD = "";
		PointGMP publicKey;
		String encryptText = "";
		BigInteger x;
		BigInteger y;
		
		
		String[] cutText = cutVote(textToEncypt);
		
		//obtengo la clave publica con getKey y separa esto en x e y (que es la mitad y hacer un new PointGMP) acordarse de que  
		//en la bd se guarda en base64
		publicKeyBD = getPublicKey(idVote);
		byte[] keyDecoded = Base64.getDecoder().decode(publicKeyBD.getBytes());
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
		
		//convierto a byte[]
		result = encryptText.getBytes();
		
		return result;
	}
	
	public String decrypt(String idVote, byte[] cipherText) throws BadPaddingException, UnsupportedEncodingException {
		String result;
		CryptoEngine ce;
		String cipherTextString;
		String decoded;
		String secretKey;
		String publicKey;
		
		ce = new CryptoEngine(idVote);
		
		secretKey = getPrivateKey(idVote);
		
		publicKey = getPublicKey(idVote);
		byte[] keyDecoded2 = Base64.getDecoder().decode(publicKey.getBytes());
		publicKey = new String(keyDecoded2);
		
		int longKey = (publicKey.length()-4)/2;

		ce.generateKeyPair(new PointGMP(new BigInteger(publicKey.substring(0, longKey)),
				new BigInteger(publicKey.substring(longKey+4, publicKey.length())), ce.curve), 
				new BigInteger(secretKey));

		cipherTextString = new String(cipherText, "UTF-8");
		result = "";
		
		for (String s: cutCifVote(cipherTextString)){
			String s2;
			decoded = "";
			s2 = publicKey + "////" + s;
			
			decoded = ce.decodeString(s2, secretKey);
			result = result + decoded;
			
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
		
		int index = votoCifrado.indexOf("|");
		while(index >= 0) {
		    indices.add(index);
		    index = votoCifrado.indexOf("|", index+1);
		}
		
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
		
		String[] result = new String[res.size()];
		result = res.toArray(result);
		
		return result;
		
	}
	
	private String formatToDecode(String cipherText, String idVote){
		String result;
		result = "";
		String publicKeys;
				
		publicKeys = getPublicKey(idVote);
		
		byte[] keyDecoded = Base64.getDecoder().decode(publicKeys.getBytes());
		String publicKeyBD = new String(keyDecoded);
		
		result = publicKeyBD+"////"+cipherText;
		
		return result;
	}

}
