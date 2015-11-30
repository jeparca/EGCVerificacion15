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

public class WeierStrassCurve extends Curve {

	private BigInteger a1;
	private BigInteger a2;
	private BigInteger a3;
	private BigInteger a4;
	private BigInteger a6;
	private BigInteger r4;
	private BigInteger r6;

	public WeierStrassCurve(BigInteger p, BigInteger r, BigInteger n, BigInteger gx, BigInteger gy) {
		super(p, r, n, gx, gy);
	}

	public WeierStrassCurve(BigInteger p, BigInteger r, BigInteger n, BigInteger gx, BigInteger gy, BigInteger a1, BigInteger a2,
			BigInteger a3, BigInteger a4, BigInteger a6, BigInteger r4, BigInteger r6) {
		super(p, r, n, gx, gy);
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
		this.a6 = a6;
		this.r4 = r4;
		this.r6 = r6;
	}

	public WeierStrassCurve(BigInteger _p, BigInteger _r, BigInteger _n, BigInteger _a4, BigInteger _a6, BigInteger _r4, BigInteger _r6,
			BigInteger _gx, BigInteger _gy) {
		super(_p, _r, _n, _gx, _gy);
		this.a4 = _a4;
		this.a6 = _a6;
		this.r4 = _r4;
		this.r6 = _r6;
		this.a1 = BigInteger.ZERO;
		this.a2 = BigInteger.ZERO;
		this.a3 = BigInteger.ZERO;
	}

	@Override
	public PointGMP oppositePoint(PointGMP m_P) {
		BigInteger yR;
		yR = BigInteger.ZERO.subtract(m_P.getY());

		// yR = yR % p;
		yR = yR.mod(p);

		return new PointGMP(m_P.getX(), yR, m_P.getOp());
	}

	@Override
	public PointGMP doublePoint(PointGMP m_P) {
		BigInteger lambda, xR, yR;

		BigInteger denom = m_P.getY().multiply(new BigInteger("2"));
		denom = denom.modInverse(p);
		denom = denom.mod(p);
		lambda = ((new BigInteger("3").multiply(m_P.getX()).multiply(m_P.getX())).add(a4)).multiply(denom);
		xR = (lambda.multiply(lambda)).subtract(m_P.getX().multiply(new BigInteger("2")));
		yR = m_P.getY().add((lambda.multiply((xR.subtract(m_P.getX())))));
		yR = BigInteger.ZERO.subtract(yR);
		xR = xR.mod(p);
		yR = yR.mod(p);
		return new PointGMP(xR, yR, m_P.getOp());
	}

	@Override
	public PointGMP addDistinct(PointGMP m_P, PointGMP m_Q) {
		BigInteger lambda, xR, yR;

		BigInteger denom = (m_P.getX().subtract(m_Q.getX()));
		denom = denom.modInverse(p);
		denom = denom.mod(p);
		lambda = (m_P.getY().subtract(m_Q.getY())).multiply(denom);

		xR = (lambda.multiply(lambda)).subtract(m_P.getX()).subtract(m_Q.getX());
		yR = m_P.getY().add(lambda.multiply((xR.subtract(m_P.getX()))));
		yR = BigInteger.ZERO.subtract(yR);
		xR = xR.mod(p);
		yR = yR.mod(p);
		return new PointGMP(xR, yR, m_P.getOp());
	}

	@Override
	public PointGMP add(PointGMP m_P, PointGMP m_Q) {
		if (m_P.getX().equals(BigInteger.ZERO) && m_P.getY().equals(BigInteger.ONE))
			return new PointGMP(m_Q);
		if (m_Q.getX().equals(BigInteger.ZERO) && m_Q.getY().equals(BigInteger.ONE))
			return new PointGMP(m_P);

		if (m_P.equal(oppositePoint(m_Q)))
			return new PointGMP(m_P.getOp());

		if (m_P.equal(m_Q))
			return doublePoint(m_P);
		else
			return addDistinct(m_P, m_Q);
	}

	@Override
	public PointGMP mult(PointGMP m_P, BigInteger k) {
		if (k.equals(BigInteger.ZERO))// si k=0 on retourne le point neutre
		{
			return new PointGMP(BigInteger.ZERO, BigInteger.ONE, m_P.getOp());
		}

		else if (k.equals(BigInteger.ONE))// si k=1 on retourne le point lui même
		{
			return new PointGMP(m_P);
		}

		PointGMP res = new PointGMP(m_P);
		BigInteger i = k;

		while (i.equals(BigInteger.ONE)) {
			res = res.add(res);
			if (i.mod(new BigInteger("2")).equals(BigInteger.ZERO))
				return res.mult((i.divide(new BigInteger("2"))));
			else
				return res.mult(((i.subtract(BigInteger.ONE)).divide(new BigInteger("2")))).add(m_P);
		}

		return res;
	}

	/**
	 * Fonction qui permet de calculer une signature Prend en param�tre le hash que l'on veut signer ainsi que la clef secr�te de
	 * l'utilisateur Retourne un point qui aura comme coordon�e x le r, et comme coordon�e y le s (les deux chiffres de la signature)
	 **/
	public PointGMP sign_ECDSA(BigInteger hash, BigInteger clefSecrete) {
		// partie signature
		PointGMP generateur = new PointGMP(gx, gy, this);// le point g�n�rateur qui servira � calculer notre clef secr�te
		Random rnd = new Random();

		BigInteger k = BigInteger.probablePrime(7, rnd);// on choisit un k random

		PointGMP rPoint = generateur.mult(k);// on multiplie le g�n�rateur par k
		BigInteger r = rPoint.getX();

		k = k.modInverse(n);
		BigInteger parenthese = (hash.add((r.multiply(clefSecrete)))).mod(n);
		BigInteger s = (k.multiply(parenthese)).mod(n);

		return new PointGMP(r, s, this);
	}

	/**
	 * Fonction qui permet de v�rifier une signature Prend en param�tre le r, le s (les deux chiffres de la signature), puis la clef
	 * publique de celui qui a sign�, et pour finir le hash de la cha�ne de caract�re qui a �t� sign�e Retourne un bool�en qui indique si
	 * oui ou non la signature est v�rifi�e
	 **/
	public boolean verif_ECDSA(BigInteger r, BigInteger s, PointGMP qA, BigInteger hash) {
		PointGMP generateur = new PointGMP(gx, gy, this);// le point g�n�rateur qui servira � calculer notre clef secr�te

		BigInteger w;
		w = s.modInverse(n);

		BigInteger u1 = (hash.multiply(w)).mod(n);
		BigInteger u2 = (r.multiply(w)).mod(n);

		PointGMP res = (generateur.mult(u1)).add((qA.mult(u2)));

		if (r.equals(res.getX()))
			return true;
		else
			return false;
	}

	/**
	 * Fonction qui vérifie que les points sont sur la même courbe (surcharge) Prend en paramètre le point en question que l'on veut tester.
	 * Retourne le booléen qui indique si oui ou non les points sont égaux
	 **/
	public boolean equal(Curve curve) {
		if ((id == curve.getId()) && (p.equals(curve.getP())) && (r.equals(curve.getR())) && (n.equals(curve.getN())))
			return (this == (WeierStrassCurve) curve);
		else
			return false;
	}

	/**
	 * Fonction qui vérifie que les points sont égaux en testant toutes leurs coordonnées (surcharge) Prend en paramètre le point en
	 * question que l'on veut tester. Retourne le booléen qui indique si oui ou non les points sont égaux
	 **/
	public boolean equal(WeierStrassCurve curve) {
		if ((a1.equals(curve.a1)) && (a2.equals(curve.a2)) && (a3.equals(curve.a3)) && (a4.equals(curve.a4)) && (a6.equals(curve.a6))
				&& (r4.equals(curve.r4)) && (r6.equals(curve.r6)) && (gx.equals(curve.gx)) && (gy.equals(curve.gy)))
			return true;
		else
			return false;
	}

	public BigInteger getA1() {
		return a1;
	}

	public void setA1(BigInteger a1) {
		this.a1 = a1;
	}

	public BigInteger getA2() {
		return a2;
	}

	public void setA2(BigInteger a2) {
		this.a2 = a2;
	}

	public BigInteger getA3() {
		return a3;
	}

	public void setA3(BigInteger a3) {
		this.a3 = a3;
	}

	public BigInteger getA4() {
		return a4;
	}

	public void setA4(BigInteger a4) {
		this.a4 = a4;
	}

	public BigInteger getA6() {
		return a6;
	}

	public void setA6(BigInteger a6) {
		this.a6 = a6;
	}

	public BigInteger getR4() {
		return r4;
	}

	public void setR4(BigInteger r4) {
		this.r4 = r4;
	}

	public BigInteger getR6() {
		return r6;
	}

	public void setR6(BigInteger r6) {
		this.r6 = r6;
	}

}
