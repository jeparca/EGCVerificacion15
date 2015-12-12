/*
This file is part of the project Elliptic_SDK, which is an elliptical cryptographic 
library under GPL license v3.
Copyright (C) 2013  Olivier Goutay

Elliptic_SDK is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Elliptic_SDK is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Elliptic_SDK.  If not, see <http://www.gnu.org/licenses/>.
 */

package main.java;

import java.math.BigInteger;
import java.util.Random;

public class CryptoEngine {

	protected WeierStrassCurve curve;
	private EllipticKeyPair keyPair;
	private PointGMP generateur;

	private static final String KEY_SEPARATOR = "++++";
	private static final String CHAR_KEY_SEPARATOR = "+";
	private static final String MESSAGE_SEPARATOR = "////";
	private static final String CHAR_MESSAGE_SEPARATOR = "/";

	public CryptoEngine(String id) {
		BigInteger seed1 = (new BigInteger(id)).add(new BigInteger("1"));
		BigInteger seed2 = (new BigInteger(id)).add(new BigInteger("-1"));
		BigInteger P = new BigInteger(253, new Random(new BigInteger(id).intValue()));
		BigInteger R = new BigInteger("8094458595770206542003150089514239385761983350496862878239630488323200271273");
		BigInteger N = new BigInteger("8884933102832021670310856601112383279454437918059397120004264665392731659049");
		BigInteger A4 = new BigInteger("2481513316835306518496091950488867366805208929993787063131352719741796616329");
		BigInteger A6 = new BigInteger("4387305958586347890529260320831286139799795892409507048422786783411496715073");
		BigInteger R4 = new BigInteger("5473953786136330929505372885864126123958065998198197694258492204115618878079");
		BigInteger R6 = new BigInteger("5831273952509092555776116225688691072512584265972424782073602066621365105518");
		BigInteger GX = new BigInteger(P.bitLength(), new Random(seed1.intValue()));
		BigInteger GY = new BigInteger(P.bitLength(), new Random(seed2.intValue()));
		curve = new WeierStrassCurve(P, R, N, A4, A6, R4, R6, GX, GY);
		generateur = new PointGMP(curve.getGx(), curve.getGy(), curve);
	}

	public void generateKeyPair() {
		Random rnd = new Random();

		BigInteger secretKey = BigInteger.probablePrime(63, rnd);
		PointGMP publicKey = generateur.mult(secretKey);

		keyPair = new EllipticKeyPair(secretKey, publicKey);
	}
	
	public void generateKeyPair(PointGMP publicKey, BigInteger secretKey) {

		keyPair = new EllipticKeyPair(secretKey, publicKey);
	}

	public EllipticKeyPair generateKeyPairWithSecret(BigInteger secretKey) {
		PointGMP publicKey = generateur.mult(secretKey);

		return new EllipticKeyPair(secretKey, publicKey);
	}

	public String encodeString(String string, PointGMP publicKey) {
		// BigInteger m = new BigInteger(Base64.decodeBase64(string));
		BigInteger m = new BigInteger(string.getBytes());

		Random rnd = new Random();
		BigInteger k = BigInteger.probablePrime(7, rnd);

		PointGMP C1 = generateur.mult(k);
		PointGMP tmp = publicKey.mult(k);
		BigInteger C2 = m.add(tmp.getX());

		//System.out.println("Message a retrouver " + m + " " + new String(m.toByteArray()) + "\n");

		return formatEncodedString(C1, C2);
	}

	public String decodeString(String encoded) {
		PointGMP C1 = readFormatedKey(encoded);
		BigInteger C2 = readFormatedMessage(encoded);

		PointGMP temp = C1.mult(keyPair.getSecretKey());
		BigInteger decoded = C2.subtract(temp.getX());
		decoded = decoded.mod(curve.getP());

		//System.out.println("Resultat El gamal " + decoded + " " + new String(decoded.toByteArray()) + "\n");

		return new String(decoded.toByteArray());
	}
	
	public String decodeString(String encoded, String secretKey) {
		PointGMP C1 = readFormatedKey(encoded);
		BigInteger C2 = readFormatedMessage(encoded);

		PointGMP temp = C1.mult(new BigInteger(secretKey));
		BigInteger decoded = C2.subtract(temp.getX());
		decoded = decoded.mod(curve.getP());

		//System.out.println("Resultat El gamal " + decoded + " " + new String(decoded.toByteArray()) + "\n");

		return new String(decoded.toByteArray());
	}

	public String formatPublicKey(PointGMP publicKey) {
		return publicKey.getX() + KEY_SEPARATOR + publicKey.getY();
	}

	public PointGMP readPublicKey(String publicKey) {
		if (publicKey.contains(KEY_SEPARATOR)) {
			BigInteger x = new BigInteger(publicKey.substring(0, publicKey.indexOf(CHAR_KEY_SEPARATOR)));
			BigInteger y = new BigInteger(publicKey.substring(publicKey.lastIndexOf(CHAR_KEY_SEPARATOR) + 1, publicKey.length()));

			return new PointGMP(x, y, curve);
		} else {
			throw new RuntimeException("The key format is not valid !");
		}
	}

	public String formatEncodedString(PointGMP C1, BigInteger C2) {
		return C1.getX() + KEY_SEPARATOR + C1.getY() + MESSAGE_SEPARATOR + C2;
	}

	public PointGMP readFormatedKey(String encoded) {
		if (encoded.contains(KEY_SEPARATOR)) {
			BigInteger x = new BigInteger(encoded.substring(0, encoded.indexOf(CHAR_KEY_SEPARATOR)));
			BigInteger y = new BigInteger(encoded.substring(encoded.lastIndexOf(CHAR_KEY_SEPARATOR) + 1,
					encoded.indexOf(CHAR_MESSAGE_SEPARATOR)));

			return new PointGMP(x, y, curve);
		} else {
			throw new RuntimeException("The key format is not valid !");
		}
	}

	public BigInteger readFormatedMessage(String encoded) {
		if (encoded.contains(MESSAGE_SEPARATOR)) {
			return new BigInteger(encoded.substring(encoded.lastIndexOf(CHAR_MESSAGE_SEPARATOR) + 1, encoded.length()));
		} else {
			throw new RuntimeException("The key format is not valid !");
		}
	}

	public EllipticKeyPair getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(EllipticKeyPair keyPair) {
		this.keyPair = keyPair;
	}

}
