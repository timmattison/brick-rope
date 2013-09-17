package com.timmattison.bitcoin.test.tools;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.bitcoin.test.HashHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/21/13
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class Base58Encoder {
    private static final ArrayList<String> base58Alphabet = createMap();

    private static ArrayList<String> createMap() {
        ArrayList<String> result = new ArrayList<String>();

        result.add(0, "1"); result.add(1, "2"); result.add(2, "3"); result.add(3, "4");
        result.add(4, "5"); result.add(5, "6"); result.add(6, "7"); result.add(7, "8");
        result.add(8, "9"); result.add(9, "A"); result.add(10, "B"); result.add(11, "C");
        result.add(12, "D"); result.add(13, "E"); result.add(14, "F"); result.add(15, "G");
        result.add(16, "H"); result.add(17, "J"); result.add(18, "K"); result.add(19, "L");
        result.add(20, "M"); result.add(21, "N"); result.add(22, "P"); result.add(23, "Q");
        result.add(24, "R"); result.add(25, "S"); result.add(26, "T"); result.add(27, "U");
        result.add(28, "V"); result.add(29, "W"); result.add(30, "X"); result.add(31, "Y");
        result.add(32, "Z"); result.add(33, "a"); result.add(34, "b"); result.add(35, "c");
        result.add(36, "d"); result.add(37, "e"); result.add(38, "f"); result.add(39, "g");
        result.add(40, "h"); result.add(41, "i"); result.add(42, "j"); result.add(43, "k");
        result.add(44, "m"); result.add(45, "n"); result.add(46, "o"); result.add(47, "p");
        result.add(48, "q"); result.add(49, "r"); result.add(50, "s"); result.add(51, "t");
        result.add(52, "u"); result.add(53, "v"); result.add(54, "w"); result.add(55, "x");
        result.add(56, "y"); result.add(57, "z");

        return result;
    }

    public static String base58Encode(byte versionApplicationByte, byte[] payloadBytes) throws IOException, NoSuchAlgorithmException {
        // Step 1: Concatenate the payload bytes onto the version/application byte
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(versionApplicationByte);
        byteArrayOutputStream.write(payloadBytes);
        byte[] step1Result = byteArrayOutputStream.toByteArray();
        String check = ByteArrayHelper.toHex(step1Result);

        // Step 2: Take the first four bytes of the double-SHA256 hash of the step 1 result
        byte[] step2HashData = HashHelper.doubleSha256Hash(step1Result);

        byte[] step2Result = new byte[4];

        for(int loop = 0; loop < 4; loop++) {
            step2Result[loop] = step2HashData[loop];
        }
        check = ByteArrayHelper.toHex(step2Result);

        // Step 3: Concatenate step 2's result onto step 1's result
        byteArrayOutputStream = new ByteArrayOutputStream();

        // Is the high bit set?
        if((step1Result[0] & 0x80) == 0x80) {
            // Yes, add a leading 0x00 byte so the BigInteger isn't negative
            byteArrayOutputStream.write(0x00);
        }

        byteArrayOutputStream.write(step1Result);
        byteArrayOutputStream.write(step2Result);
        byte[] step3Result = byteArrayOutputStream.toByteArray();
        check = ByteArrayHelper.toHex(step3Result);

        // Step 4: Convert to base-58
        BigInteger base58BigInteger = new BigInteger(step3Result);
        StringBuilder stringBuilder = new StringBuilder();
        BigInteger base58ModDiv = new BigInteger("58", 10);

        while(base58BigInteger.compareTo(BigInteger.ZERO) != 0) {
            BigInteger currentValue = base58BigInteger.mod(base58ModDiv);
            base58BigInteger = base58BigInteger.divide(base58ModDiv);

            stringBuilder.append(base58Alphabet.get(currentValue.intValue()));
        }

        // Reverse the string since it has the least significant values first
        String step4ResultBeforePruning = stringBuilder.reverse().toString();

        // Remove all leading 1's
        String step4Result = step4ResultBeforePruning.replaceAll("^1", "");

        // Step 5: Count the number of leading zero bytes
        int leadingZeroBytes = 0;

        for(int loop = 0; loop < (step1Result.length + step2Result.length); loop++) {
            byte currentByte;

            // Are we in the first result?
            if(loop > step1Result.length) {
                // No, get the byte from the second result
                currentByte = step2Result[loop - step1Result.length];
            }
            else {
                // Yes, get the byte from the first result
                currentByte = step1Result[loop];
            }

            // Is the current byte zero?
            if(currentByte == 0x00) {
                // Yes, increment the count
                leadingZeroBytes++;
            }
            else {
                // No, done counting
                break;
            }
        }

        // Step 6: Add a "1" for each leading zero byte
        stringBuilder = new StringBuilder();

        for(int loop = 0; loop < leadingZeroBytes; loop++) {
            stringBuilder.append("1");
        }

        stringBuilder.append(step4Result);
        String step6Result = stringBuilder.toString();

        return step6Result;
    }
}
