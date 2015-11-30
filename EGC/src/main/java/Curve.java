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
import java.util.ArrayList;
import java.util.List;

public abstract class Curve {

	protected int id;
	protected BigInteger p;
	protected BigInteger r;
	protected BigInteger n;
	protected BigInteger gx;
	protected BigInteger gy;

	// ******* Abstract Functions ********//
	public abstract PointGMP oppositePoint(PointGMP m_P);

	public abstract PointGMP doublePoint(PointGMP m_P);

	public abstract PointGMP addDistinct(PointGMP m_P, PointGMP m_Q);

	public abstract PointGMP add(PointGMP m_P, PointGMP m_Q);

	public abstract PointGMP mult(PointGMP m_P, BigInteger k);

	public Curve(BigInteger p, BigInteger r, BigInteger n, BigInteger gx, BigInteger gy) {
		super();
		this.p = p;
		this.r = r;
		this.n = n;
		this.gx = gx;
		this.gy = gy;
		this.id = 1612671;
	}

	protected boolean checkPrime() {
		return n.isProbablePrime(15);
	}

	protected static BigInteger sqrt(BigInteger n) {
		BigInteger a = BigInteger.ONE;
		BigInteger b = new BigInteger(n.shiftRight(5).add(new BigInteger("8")).toString());
		while (b.compareTo(a) >= 0) {
			BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
			if (mid.multiply(mid).compareTo(n) > 0)
				b = mid.subtract(BigInteger.ONE);
			else
				a = mid.add(BigInteger.ONE);
		}
		return a.subtract(BigInteger.ONE);
	}

	public List<BigInteger> decompositionPremier() {
		List<BigInteger> decomposition = new ArrayList<BigInteger>();

		if (checkPrime()) {
			decomposition.add(n);
			return decomposition;
		}

		BigInteger max = sqrt(n);

		for (BigInteger i = new BigInteger("2"); i.compareTo(max) > 0;) {
			if (n.mod(i).equals(BigInteger.ZERO)) {
				decomposition.add(i);
				n = n.divide(i);
				max = n;
			} else {
				i = i.nextProbablePrime();
			}
		}

		return decomposition;
	}

	public BigInteger ordre(PointGMP p1) {
		List<BigInteger> decomposition = decompositionPremier();
		if (decomposition.size() == 1)
			return decomposition.get(0);

		BigInteger m = n;
		PointGMP Q;

		for (int i = 0; i < decomposition.size(); i++) {
			m = m.divide(decomposition.get(i));
			Q = mult(p1, m);

			if (Q.getX().equals(BigInteger.ZERO) && Q.getY().equals(BigInteger.ONE)) {
				Q = mult(Q, decomposition.get(i));
				m = m.multiply(decomposition.get(i));
			}
		}

		return m;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getP() {
		return p;
	}

	public void setP(BigInteger p) {
		this.p = p;
	}

	public BigInteger getR() {
		return r;
	}

	public void setR(BigInteger r) {
		this.r = r;
	}

	public BigInteger getN() {
		return n;
	}

	public void setN(BigInteger n) {
		this.n = n;
	}

	public BigInteger getGx() {
		return gx;
	}

	public void setGx(BigInteger gx) {
		this.gx = gx;
	}

	public BigInteger getGy() {
		return gy;
	}

	public void setGy(BigInteger gy) {
		this.gy = gy;
	}

}
