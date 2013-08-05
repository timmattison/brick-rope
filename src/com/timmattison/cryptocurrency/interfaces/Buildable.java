package com.timmattison.cryptocurrency.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/4/13
 * Time: 9:40 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Buildable {
    /**
     * Builds the object and returns a byte array containing any bytes that were left over from its input
     * @return
     */
    byte[] build(byte[] data);
}
