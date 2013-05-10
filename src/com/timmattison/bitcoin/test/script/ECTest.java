package com.timmattison.bitcoin.test.script;

import sun.security.ec.ECKeyFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/10/13
 * Time: 7:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECTest {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException {
        ECKeyFactory a = new ECKeyFactory();
        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA");
        Cipher cipher = Cipher.getInstance("ECIES");
    }
}
