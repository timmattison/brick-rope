package com.timmattison.cryptocurrency.bitcoin.factories;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MessageDigestFactory {
    public MessageDigest createMessageDigest();
}
