package cifrado;

import javax.crypto.BadPaddingException;

public class AuthorityImpl implements Authority{
	
	public boolean postKey(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getPublicKey(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPrivateKey(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean checkVote(byte[] votoCifrado, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public byte[] encrypt(String idVote, String textToEncypt) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String decrypt(String idVote, byte[] cipherText) throws BadPaddingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] cutVote(String votoEnClaro) {
		int arrayLength = votoEnClaro.length() / 31; 
		String[] result = new String[arrayLength + 1];
		String corte = "";
		
		if(votoEnClaro.length() > 31){
			//corto
			for(int i = 0; i < arrayLength + 1; i++){
				int from = i * 31;
				int to = from + 32;
				if(i == 0){
					corte = votoEnClaro.substring(from,to);
					result[i] = corte;
				}else{
					
					//si el ultimo trozo es menor a 31				
					if(votoEnClaro.length() - from < 31){
						corte = votoEnClaro.substring(from + i, votoEnClaro.length());
					}else{
						corte = votoEnClaro.substring(from + i, from + i + 32);
					}
					result[i] = corte;
				}
				
			}
		}else{
			result[0] = votoEnClaro;
		}
		
		return result;
	}

}
