package com.timmattison.crypto.ecc.factories;

import com.timmattison.crypto.ecc.interfaces.ECCMessageSignerDigestFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/23/13
 * Time: 7:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class SHA1MessageSignerDigestFactory implements ECCMessageSignerDigestFactory {
    @Override
    public MessageDigest create() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
