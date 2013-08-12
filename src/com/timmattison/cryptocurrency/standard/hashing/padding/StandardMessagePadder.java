package com.timmattison.cryptocurrency.standard.hashing.padding;

import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.standard.hashing.Endianness;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 6/23/13
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class StandardMessagePadder implements MessagePadder {
    private final int target = 448;
    private final int modulus = 512;
    private final int sizeOfLengthInBits = 64;
    private final Endianness endianness;

    private byte[] output = null;
    private int messageBitLengthWithPadding = -1;

    public StandardMessagePadder(Endianness endianness) {
        this.endianness = endianness;
    }

    public byte[] pad(byte[] input, int messageBitLength) {
        // Step 1: Append a 1 bit
        output = ByteArrayHelper.appendBit(input, messageBitLength, true);

        // Append 0 bits until the length mod 512 is equal to 448, if necessary
        int lengthToPad = messageBitLength + 1;
        int leftOverInChunk = (lengthToPad % modulus);

        int bitsToPad;

        if (leftOverInChunk > target) {
            bitsToPad = modulus - (leftOverInChunk - target);
        } else {
            bitsToPad = target - leftOverInChunk;
        }

        messageBitLengthWithPadding = lengthToPad + bitsToPad;
        output = ByteArrayHelper.padBits(output, lengthToPad, messageBitLengthWithPadding, false);

        // Append the length (before padding) as a 64-bit value
        output = ByteArrayHelper.append64Bits(endianness, output, messageBitLengthWithPadding, messageBitLength);
        messageBitLengthWithPadding += sizeOfLengthInBits;

        // Now the data length must be on a 512-bit boundary
        if ((messageBitLengthWithPadding % modulus) != 0) {
            // This should never happen
            throw new UnsupportedOperationException("Data is not on a " + modulus + "-bit boundary.  Actual length: " + messageBitLengthWithPadding);
        }

        return output;
    }

    @Override
    public int getBitLengthWithPadding() {
        return messageBitLengthWithPadding;
    }

    @Override
    public int getModulus() {
        return modulus;
    }
}
