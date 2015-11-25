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

package cifrado;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;

public class main {
	public static void main(String[] args) {
		System.out.println("Test begins");

		WeierStrassCurve curve;
		BigInteger P = new BigInteger("8884933102832021670310856601112383279507496491807071433260928721853918699951");
		BigInteger R = new BigInteger("8094458595770206542003150089514239385761983350496862878239630488323200271273");
		BigInteger N = new BigInteger("8884933102832021670310856601112383279454437918059397120004264665392731659049");
		BigInteger A4 = new BigInteger("2481513316835306518496091950488867366805208929993787063131352719741796616329");
		BigInteger A6 = new BigInteger("4387305958586347890529260320831286139799795892409507048422786783411496715073");
		BigInteger R4 = new BigInteger("5473953786136330929505372885864126123958065998198197694258492204115618878079");
		BigInteger R6 = new BigInteger("5831273952509092555776116225688691072512584265972424782073602066621365105518");
		BigInteger GX = new BigInteger("7638166354848741333090176068286311479365713946232310129943505521094105356372");
		BigInteger GY = new BigInteger("762687367051975977761089912701686274060655281117983501949286086861823169994");
		curve = new WeierStrassCurve(P, R, N, A4, A6, R4, R6, GX, GY);

		// testDiffieHelman(curve);
		//
		// testDiffieHelmanAndSign(curve);
		//
		// elGamalString(curve);

		testElGamal();
	}

	private static void testElGamal() {
		CryptoEngine cryptoEngine = new CryptoEngine();
		cryptoEngine.generateKeyPair();

		String encoded = cryptoEngine.encodeString("test test test", cryptoEngine.getKeyPair().getPublicKey());

		System.out.println(cryptoEngine.decodeString(encoded));
	}

	private static void testDiffieHelman(WeierStrassCurve curve) {
		PointGMP generateur = new PointGMP(curve.getGx(), curve.getGy(), curve);// le
																				// point
																				// générateur
																				// qui
																				// servira
																				// àcalculer
																				// notre
																				// clef
																				// secrète
		Random rnd = new Random();

		// calcul chez Alice
		BigInteger clefSecreteAlice = BigInteger.probablePrime(7, rnd);// la
																		// clef
																		// secrète
																		// n'appartient
																		// qu'à
																		// Alice
		PointGMP pA = generateur.mult(clefSecreteAlice);// pA est la clef
														// publique que l'on va
														// partager à Bob
		if (pA.isOnCurve()) {
			System.out.println("On Curve");
		} else {
			System.out.println("Not on curve");
		}

		// calcul chez Bob
		BigInteger clefSecreteBob = BigInteger.probablePrime(7, rnd);// la clef
																		// secrète
																		// n'appartient
																		// qu'à
																		// Bob
		PointGMP pB = generateur.mult(clefSecreteBob);// pB est la clef publique
														// que l'on va partager
														// à alice

		// on partage donc en clair pB et pA (pas sécurisé mais pas de de
		// serveur dans ce cas là)
		// calcul chez Alice
		BigInteger kA = BigInteger.probablePrime(7, rnd);// un chiffre random
															// pour Alice
		PointGMP middleA = generateur.mult(kA);// middleA est la clef
												// intermédiaire d'Alice

		// Calcul chez Bob
		BigInteger kB = BigInteger.probablePrime(7, rnd);// un chiffre random
															// pour Bob
		PointGMP middleB = generateur.mult(kB);// middleB est la clef
												// intermédiaire de Bob

		// tout est vérifié chacun calcule leur clef partagée finale
		PointGMP finBob = middleA.mult(kB);
		PointGMP finAlice = middleB.mult(kA);

		if (finBob.equal(finAlice))
			System.out.println("Diffie Hellman STS complet");
		else
			System.out.println("Probleme Diffie Hellman STS");

		System.out.println("fin Bob " + finBob.toString());
		System.out.println("fin Ali " + finAlice.toString());
	}

	private static void testDiffieHelmanAndSign(WeierStrassCurve curve) {
		PointGMP generateur = new PointGMP(curve.getGx(), curve.getGy(), curve);
		Random rnd = new Random();

		// calcul chez Alice
		BigInteger clefSecreteAlice = BigInteger.probablePrime(7, rnd);
		PointGMP pA = generateur.mult(clefSecreteAlice);
		if (pA.isOnCurve()) {
			System.out.println("On Curve");
		} else {
			System.out.println("Not on curve");
		}

		// on partage donc en clair pB et pA
		// calcul chez Alice
		BigInteger kA = BigInteger.probablePrime(7, rnd);// un chiffre random
															// pour Alice
		PointGMP middleA = generateur.mult(kA);// middleA est la clef
												// intermédiaire d'Alice

		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			// calcul chez Alice
			BigInteger hashA = new BigInteger(1, digest.digest(middleA.toString().getBytes("UTF-8")));

			// calcul chez Alice
			PointGMP signatureA = curve.sign_ECDSA(hashA, clefSecreteAlice);
			if (signatureA.isOnCurve()) {
				System.out.println("On Curve");
			} else {
				System.out.println("Not on curve");
			}
			// on envoie donc middleA et signature A � Bob

			// calcul chez bob ==> on v�rifie la signature avant de continuer
			boolean resA = curve.verif_ECDSA(signatureA.getX(), signatureA.getY(), pA, hashA);
			if (resA)
				System.out.println("Signature ok d'Alice");
			else
				System.out.println("Probleme de signature d'Alice");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
