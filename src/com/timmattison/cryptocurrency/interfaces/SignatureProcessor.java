package com.timmattison.cryptocurrency.interfaces;

/**
 * Returns an ECC signature object.  This is a convenience interface that knows what curve is being used.
 *
 * @param <T> the type of signature that is returned
 */
public interface SignatureProcessor<T> {
    T getSignature(byte[] signature, byte[] publicKey);

    T getSignature(byte[] sig_r, byte[] sig_s, byte[] publicKey);
}
