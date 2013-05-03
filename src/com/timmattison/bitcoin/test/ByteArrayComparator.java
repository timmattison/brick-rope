package com.timmattison.bitcoin.test;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/3/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ByteArrayComparator implements Comparator<byte[]> {
    @Override
    public int compare(byte[] o1, byte[] o2) {
        // Are they equal?
        if(Arrays.equals(o1, o2)) {
            // Sort order doesn't matter
            return 0;
        }

        // They are not equal which one is longer?
        if(o1.length < o2.length) {
            // o1 is shorter
            return 1;
        }
        else if(o1.length > o2.length) {
            // o1 is longer
            return -1;
        }

        // They are equal in length
        for(int loop = 0; loop < o1.length; loop++) {
            if(o1[loop] < o2[loop]) {
                // o1 is smaller
                return -1;
            }
            else if(o1[loop] > o2[loop]) {
                // o1 is larger
                return 1;
            }
            else {
                // They are equal, keep going
            }
        }

        // This should never happen.  This means they are the same length and equal and should have been handled above.
        throw new UnsupportedOperationException("ByteArrayComparator failed to compare the arrays");
    }
}
