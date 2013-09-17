package com.timmattison.crypto.ecc.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/22/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCMessageSignatureVerifier {
    boolean signatureValid(byte[] messageBytes, ECCSignature eccSignature);
}
