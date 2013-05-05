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
public class HashComparator implements Comparator<byte[]> {
    @Override
    public int compare(byte[] o1, byte[] o2) {
        // Are they equal?
        if(Arrays.equals(o1, o2)) {
            // Sort order doesn't matter
            return 0;
        }

        // Are they equal in length?
        if(o1.length != o2.length) {
            // No, this should never happen
            throw new UnsupportedOperationException("ByteArrayComparator failed to compare the arrays");
        }

        // They are equal in length
        for(int loop = (o1.length - 1); loop >= 0; loop--) {
            int o1Value = (o1[loop] & 0xFF);
            int o2Value = (o1[loop] & 0xFF);
            if(o1Value < o2Value) {
                // o1 is smaller
                return 1;
            }
            else if(o1[loop] > o2[loop]) {
                // o1 is larger
                return -1;
            }
            else {
                // They are equal, keep going
            }
        }

        // This should never happen.  This means they are the same length and equal and should have been handled above.
        throw new UnsupportedOperationException("ByteArrayComparator failed to compare the arrays");
    }
}
