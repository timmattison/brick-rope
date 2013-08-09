package com.timmattison.cryptocurrency.standard;

import com.timmattison.bitcoin.test.ByteArrayHelper;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/3/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class HashComparator implements Comparator<byte[]> {
    private int compareReverse(byte[] left, byte[] right) {
        // From: http://stackoverflow.com/questions/5108091/java-comparator-for-byte-array-lexicographic
        // Modified to sort from last byte to first since we're dealing with different endianness
        for (int i = (left.length - 1), j = (right.length - 1); i >= 0 && j >= 0; i--, j--) {
            int a = (left[i] & 0xff);
            int b = (right[j] & 0xff);
            if (a != b) {
                return a - b;
            }
        }
        return left.length - right.length;
    }

    @Override
    public int compare(byte[] left, byte[] right) {
        return compareReverse(left, right);
    }
}
