package com.timmattison.cryptocurrency.standard;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.bitcoin.test.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.Buildable;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/16/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class VariableLengthInteger implements Buildable {
    private static final int header16BitInteger = 0xfd;
    private static final int header32BitInteger = 0xfe;
    private static final int header64BitInteger = 0xff;

    /**
     * Value
     */
    long value;
    private byte[] valueBytes;

    public VariableLengthInteger() {
    }

    public byte[] build(byte[] data) {
        // Get the first byte
        byte firstByte = data[0];
        data = Arrays.copyOfRange(data, 1, data.length);

        int bytesToRead = 0;

        // What size value is this?
        if (firstByte == (byte) header16BitInteger) {
            // 16-bit
            bytesToRead = 2;
            valueBytes = Arrays.copyOfRange(data, 0, bytesToRead);
            data = Arrays.copyOfRange(data, bytesToRead, data.length);
            value = EndiannessHelper.BytesToShort(valueBytes);
        } else if (firstByte == (byte) header32BitInteger) {
            // 32-bit
            bytesToRead = 4;
            valueBytes = Arrays.copyOfRange(data, 0, bytesToRead);
            data = Arrays.copyOfRange(data, bytesToRead, data.length);
            value = EndiannessHelper.BytesToInt(valueBytes);
        } else if (firstByte == (byte) header64BitInteger) {
            // 64-bit
            bytesToRead = 8;
            valueBytes = Arrays.copyOfRange(data, 0, bytesToRead);
            data = Arrays.copyOfRange(data, bytesToRead, data.length);
            value = EndiannessHelper.BytesToLong(valueBytes);
        } else {
            // 8-bit (mask this so that we don't get negative values)
            valueBytes = new byte[1];
            value = (firstByte & 0x0FF);
            valueBytes[0] = (byte) value;
        }

        // Did we have a multi-byte value?
        if (valueBytes.length != 1) {
            // Yes, add the missing first byte
            byte[] tempValueBytes = new byte[valueBytes.length + 1];
            tempValueBytes[0] = (byte) firstByte;

            for (int loop = 0; loop < valueBytes.length; loop++) {
                tempValueBytes[loop + 1] = valueBytes[loop];
            }

            // Store the true bytes back in valueBytes
            valueBytes = tempValueBytes;
        }

        // Return what is left
        return data;
    }

    @Override
    public byte[] dump() {
        return valueBytes;
    }

    @Override
    public String prettyDump(int indentationLevel) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");

        for (int loop = 0; loop < indentationLevel; loop++) {
            stringBuilder.append("\t");
        }

        stringBuilder.append("Value bytes: ");
        stringBuilder.append(ByteArrayHelper.toHex(valueBytes));
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    public long getValue() {
        throwExceptionIfNotBuilt();
        return value;
    }

    public byte[] getValueBytes() {
        throwExceptionIfNotBuilt();
        return valueBytes;
    }

    private void throwExceptionIfNotBuilt() {
        if (valueBytes == null) {
            throw new IllegalStateException("Variable length integer wasn't built");
        }
    }
}
