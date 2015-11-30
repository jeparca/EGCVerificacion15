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

import java.io.Serializable;
import java.math.BigInteger;

public class EllipticKeyPair implements Serializable {

	private static final long serialVersionUID = 5769426917702072052L;
	private BigInteger secretKey;
	private PointGMP publicKey;

	public EllipticKeyPair(BigInteger secretKey, PointGMP publicKey) {
		super();
		this.secretKey = secretKey;
		this.publicKey = publicKey;
	}

	public BigInteger getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(BigInteger secretKey) {
		this.secretKey = secretKey;
	}

	public PointGMP getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PointGMP publicKey) {
		this.publicKey = publicKey;
	}

}
