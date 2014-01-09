package com.timmattison.bitcoin.old;

import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/16/13
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImportFromText {
    public static byte[] Import(String data) {
        if (data == null) {
            throw new UnsupportedOperationException("Data cannot be NULL");
        }

        ByteArrayOutputStream returnValue = new ByteArrayOutputStream();

        StringBuilder stringBuilder = new StringBuilder();
        int position = 0;

        while (position < data.length()) {
            char currentChar = data.charAt(position++);

            if ((currentChar >= '0') && (currentChar <= '9')) {
                stringBuilder.append(currentChar);
            } else if ((currentChar >= 'A') && (currentChar <= 'F')) {
                stringBuilder.append(currentChar);
            } else {
                // Ignore it
            }

            if (stringBuilder.length() == 2) {
                String currentByteString = stringBuilder.toString();
                Byte currentByte = (byte) Integer.parseInt(currentByteString, 16);
                returnValue.write(currentByte);

                stringBuilder = new StringBuilder();
            }
        }

        return returnValue.toByteArray();
    }
}
