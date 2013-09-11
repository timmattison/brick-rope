package com.timmattison.cryptocurrency.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/15/13
 * Time: 7:15 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SignatureProcessor<T> {
    T getSignature(byte[] signature, byte[] publicKey);

    T getSignature(byte[] sig_r, byte[] sig_s, byte[] publicKey);
}
