package com.timmattison.bitcoin.old.ecc.fp;

import com.timmattison.bitcoin.old.BigIntegerHelper;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 6:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECFieldElementFp {
    private final BigInteger x;
    private final BigInteger q;

    public ECFieldElementFp(BigInteger q, BigInteger x) {
        this.x = x;
        // TODO if(x.compareTo(q) >= 0) error
        this.q = q;
    }

    public boolean equals(ECFieldElementFp other) {
        if (other == this) return true;
        return (this.q.equals(other.q) && this.x.equals(other.x));
    }

    public BigInteger toBigInteger() {
        return this.x;
    }

    public ECFieldElementFp negate() {
        return new ECFieldElementFp(this.q, this.x.negate().mod(this.q));
    }

    public ECFieldElementFp add(ECFieldElementFp b) {
        return new ECFieldElementFp(this.q, this.x.add(b.toBigInteger()).mod(this.q));
    }

    public ECFieldElementFp subtract(ECFieldElementFp b) {
        return new ECFieldElementFp(this.q, this.x.subtract(b.toBigInteger()).mod(this.q));
    }

    public ECFieldElementFp multiply(ECFieldElementFp b) {
        return new ECFieldElementFp(this.q, this.x.multiply(b.toBigInteger()).mod(this.q));
    }

    public ECFieldElementFp square() {
        return new ECFieldElementFp(this.q, BigIntegerHelper.squareBigInteger(x).mod(this.q));
    }

   public ECFieldElementFp divide(ECFieldElementFp b) {
        return new ECFieldElementFp(this.q, this.x.multiply(b.toBigInteger().modInverse(this.q)).mod(this.q));
    }
}
