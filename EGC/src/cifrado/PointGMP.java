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

public class PointGMP {
	private BigInteger x;
	private BigInteger y;
	private WeierStrassCurve op;

	public PointGMP(PointGMP point) {
		super();
		this.x = point.x;
		this.y = point.y;
		this.op = point.op;
	}

	public PointGMP(BigInteger x, BigInteger y, WeierStrassCurve op) {
		super();
		this.x = x;
		this.y = y;
		this.op = op;
	}

	public PointGMP(WeierStrassCurve op) {
		super();
		this.x = BigInteger.ZERO;
		this.y = BigInteger.ONE;
		this.op = op;
	}

	public boolean equal(PointGMP point) {
		if ((this.x.equals(point.x)) && (this.y.equals(point.y)))
			return true;
		else
			return false;
	}

	public PointGMP add(PointGMP p) {
		if (this.getOp().equal(p.getOp()))
			return this.getOp().add(this, p);
		else
			return new PointGMP(this);
	}

	public boolean isOnCurve() {
		// vérifier que le point appartient à la courbe ==> y^2 = x^3 + a4 * x
		// + a6
		BigInteger l = y.multiply(y);
		BigInteger r = x.multiply(x).multiply(x);

		WeierStrassCurve temp = (WeierStrassCurve) op;
		r = r.add((temp.getA4().multiply(x)).add(temp.getA6()));

		l = l.mod(op.getP());
		r = r.mod(op.getP());

		if (l.equals(r))
			return true;
		else
			return false;
	}

	public PointGMP mult(BigInteger k) {
		return this.getOp().mult(this, k);
	}

	public BigInteger getX() {
		return x;
	}

	public void setX(BigInteger x) {
		this.x = x;
	}

	public BigInteger getY() {
		return y;
	}

	public void setY(BigInteger y) {
		this.y = y;
	}

	public WeierStrassCurve getOp() {
		return op;
	}

	public void setOp(WeierStrassCurve op) {
		this.op = op;
	}

	@Override
	public String toString() {
		return "X: " + x + "   Y: " + y;
	}

}
