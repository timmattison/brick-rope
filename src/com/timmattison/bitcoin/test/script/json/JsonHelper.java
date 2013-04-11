package com.timmattison.bitcoin.test.script.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/17/13
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsonHelper {

    public static Byte[] byteStringToForwardBytes(String byteString) {
        return byteStringToBytes(byteString, false);
    }

    public static Byte[] byteStringToReversedBytes(String byteString) {
        return byteStringToBytes(byteString, true);
    }

    public static Byte[] byteStringToBytes(String byteString, boolean reversed) {
        List<Byte> bytes = new ArrayList<Byte>();

        // Does the string look somewhat valid?
        if (byteString == null) {
            // No, it is NULL.  Throw an exception.
            throw new UnsupportedOperationException("Byte string cannot be NULL");
        } else if ((byteString.length() % 2 != 0)) {
            // No, it is an odd number of characters.  Throw an exception.
            throw new UnsupportedOperationException("Byte string's length must be divisible by 2, saw " + byteString.length());
        }

        int length = byteString.length();

        // Loop through each pair of bytes but do them in reverse (endianness!)
        for (int loop = 0; loop < (length / 2); loop++) {
            // Grab the next two characters
            String currentByteString;

            if (reversed) {
                currentByteString = byteString.substring(length - ((loop + 1) * 2), length - ((loop + 1) * 2) + 2);
            } else {
                currentByteString = byteString.substring((loop * 2), ((loop + 1) * 2));
            }
            byte currentByte = (byte) Integer.parseInt(currentByteString, 16);

            // Add the byte to the byte list
            bytes.add(currentByte);
        }

        // Return the list as an array of bytes
        return bytes.toArray(new Byte[bytes.size()]);
    }
}
