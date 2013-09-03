package com.timmattison.crypto.ecc.interfaces;

import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/22/13
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCMessageSignerDigestFactory {
    MessageDigest create();
}
