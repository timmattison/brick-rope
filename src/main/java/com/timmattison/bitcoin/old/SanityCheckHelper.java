package com.timmattison.bitcoin.old;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/8/13
 * Time: 7:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class SanityCheckHelper {

    public static void sanityCheckNotNull(Object input, String name) {
        if (input == null) {
            throw new UnsupportedOperationException(name + " cannot be NULL");
        }
    }

    public static void sanityCheckByteArrayLengthEqual(byte[] input, int length, String name) {
        sanityCheckNotNull(input, name);

        if(input.length != length) {
            throw new UnsupportedOperationException(name + " must have exactly " + length + " elements, has " + input.length);
        }
    }

    public static void sanityCheckByteArrayLengthGreaterThan(byte[] input, int length, String name) {
        sanityCheckNotNull(input, name);

        if(input.length < length) {
            throw new UnsupportedOperationException(name + " must have at least " + length + " elements, has " + input.length);
        }
    }
}
