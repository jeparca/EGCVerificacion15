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

public class main {
	public static void main(String[] args) {
		System.out.println("Test begins");

		testElGamal();
	}

	private static void testElGamal() {
		CryptoEngine cryptoEngine = new CryptoEngine("15");
		cryptoEngine.generateKeyPair();

		String encoded = cryptoEngine.encodeString("test test test", cryptoEngine.getKeyPair().getPublicKey());

		System.out.println(cryptoEngine.decodeString(encoded));
	}

}
