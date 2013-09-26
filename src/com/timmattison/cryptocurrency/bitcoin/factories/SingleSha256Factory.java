package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.standard.hashing.sha.SingleSha256Hash;
import com.timmattison.cryptocurrency.interfaces.Hash;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleSha256Factory implements HasherFactory {
    public Hash createHasher(byte[] input) {
        return new SingleSha256Hash(input);
    }
}
