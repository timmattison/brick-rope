package com.timmattison.cryptocurrency.standard.hashing.scrypt;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/26/13
 * Time: 6:33 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Salsa {
    public byte[] execute(byte[] input);
}
