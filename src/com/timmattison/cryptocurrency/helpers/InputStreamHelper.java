package com.timmattison.cryptocurrency.helpers;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/1/13
 * Time: 6:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class InputStreamHelper {
    public static byte[] pullBytes(InputStream inputStream, int count) throws IOException {
        byte[] bytes = new byte[count];
        inputStream.read(bytes, 0, count);

        return bytes;
    }

    public static long getAvailableBytes(InputStream inputStream) {
        try {
            return inputStream.available() & 0xFFFFFFFFL;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
