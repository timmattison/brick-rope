package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.interfaces.Hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HasherFactory {
    public Hash createHasher(byte[] input);
}
