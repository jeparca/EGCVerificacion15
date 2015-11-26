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

}
