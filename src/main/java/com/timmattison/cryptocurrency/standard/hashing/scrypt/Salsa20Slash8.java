package com.timmattison.cryptocurrency.standard.hashing.scrypt;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/26/13
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Salsa20Slash8 implements Salsa {
    private int R(int a, int b) {
        return (((a) << (b)) | ((a) >> (32 - (b))));
    }

    @Override
    public byte[] execute(byte[] input) {
        int[] x = new int[16];
        int[] out = new int[16];

        for (int i = 0; i < 16; ++i) {
            x[i] = getInt(input, i);
        }

        for (int i = 20; i > 0; i -= 2) {
            x[4] ^= R(x[0] + x[12], 7);
            x[8] ^= R(x[4] + x[0], 9);
            x[12] ^= R(x[8] + x[4], 13);
            x[0] ^= R(x[12] + x[8], 18);
            x[9] ^= R(x[5] + x[1], 7);
            x[13] ^= R(x[9] + x[5], 9);
            x[1] ^= R(x[13] + x[9], 13);
            x[5] ^= R(x[1] + x[13], 18);
            x[14] ^= R(x[10] + x[6], 7);
            x[2] ^= R(x[14] + x[10], 9);
            x[6] ^= R(x[2] + x[14], 13);
            x[10] ^= R(x[6] + x[2], 18);
            x[3] ^= R(x[15] + x[11], 7);
            x[7] ^= R(x[3] + x[15], 9);
            x[11] ^= R(x[7] + x[3], 13);
            x[15] ^= R(x[11] + x[7], 18);
            x[1] ^= R(x[0] + x[3], 7);
            x[2] ^= R(x[1] + x[0], 9);
            x[3] ^= R(x[2] + x[1], 13);
            x[0] ^= R(x[3] + x[2], 18);
            x[6] ^= R(x[5] + x[4], 7);
            x[7] ^= R(x[6] + x[5], 9);
            x[4] ^= R(x[7] + x[6], 13);
            x[5] ^= R(x[4] + x[7], 18);
            x[11] ^= R(x[10] + x[9], 7);
            x[8] ^= R(x[11] + x[10], 9);
            x[9] ^= R(x[8] + x[11], 13);
            x[10] ^= R(x[9] + x[8], 18);
            x[12] ^= R(x[15] + x[14], 7);
            x[13] ^= R(x[12] + x[15], 9);
            x[14] ^= R(x[13] + x[12], 13);
            x[15] ^= R(x[14] + x[13], 18);
        }

        for (int i = 0; i < 16; ++i) {
            out[i] = x[i] + getInt(input, i);
        }

        return getBytes(out);
    }

    private byte[] getBytes(int[] out) {
        byte[] returnValue = new byte[out.length * 4];

        for (int loop = 0; loop < out.length; loop++) {
            int returnValueIndex = loop * 4;

            returnValue[returnValueIndex++] = (byte) ((out[loop] >> 24) & 0xFF);
            returnValue[returnValueIndex++] = (byte) ((out[loop] >> 16) & 0xFF);
            returnValue[returnValueIndex++] = (byte) ((out[loop] >> 8) & 0xFF);
            returnValue[returnValueIndex++] = (byte) (out[loop] & 0xFF);
        }

        return returnValue;
    }

    private int getInt(byte[] input, int i) {
        return (input[i] << 24) | (input[i + 1] << 16) | (input[i + 2] << 8) | input[i + 3];
    }
}
