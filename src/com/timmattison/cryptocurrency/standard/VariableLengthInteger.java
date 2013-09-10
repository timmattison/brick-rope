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

    private static final long max8BitValue = 0xfd;
    private static final long max16BitValue = 0xffff;
    private static final long max32BitValue = 0xffffffff;

    /**
     * Value
     */
    Long value;
    private byte[] valueBytes;

    public VariableLengthInteger() {
    }

    public void setValue(Long value) {
        this.value = value;
        valueBytes = null;
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
            value = Long.valueOf(EndiannessHelper.BytesToShort(valueBytes));
        } else if (firstByte == (byte) header32BitInteger) {
            // 32-bit
            bytesToRead = 4;
            valueBytes = Arrays.copyOfRange(data, 0, bytesToRead);
            data = Arrays.copyOfRange(data, bytesToRead, data.length);
            value = Long.valueOf(EndiannessHelper.BytesToInt(valueBytes));
        } else if (firstByte == (byte) header64BitInteger) {
            // 64-bit
            bytesToRead = 8;
            valueBytes = Arrays.copyOfRange(data, 0, bytesToRead);
            data = Arrays.copyOfRange(data, bytesToRead, data.length);
            value = EndiannessHelper.BytesToLong(valueBytes);
        } else {
            // 8-bit (mask this so that we don't get negative values)
            valueBytes = new byte[1];
            value = Long.valueOf((firstByte & 0x0FF));
            valueBytes[0] = (byte) (value & 0xFF);
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
        return getValueBytes();
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
        throwExceptionIfNotSet();
        return value;
    }

    public byte[] getValueBytes() {
        throwExceptionIfNotSet();
        buildIfNecessary();
        return valueBytes;
    }

    private void buildIfNecessary() {
        // TODO - XXX - There need to be some tests for this because this could break everything
        // XXX - TODO - There need to be some tests for this because this could break everything

        // How big is the value?
        if (value < max8BitValue) {
            // It is a single byte value
            valueBytes = new byte[]{(byte) (value & 0xFF)};
        } else if (value < max16BitValue) {
            // It is a four byte value
            valueBytes = new byte[]{(byte) (value & 0xFF),
                    (byte) ((value >> 8) & 0xFF),
                    (byte) ((value >> 16) & 0xFF),
                    (byte) ((value >> 24) & 0xFF)};
        } else if (value < max32BitValue) {
            // It is an eight byte value
            valueBytes = new byte[]{(byte) (value & 0xFF),
                    (byte) ((value >> 8) & 0xFF),
                    (byte) ((value >> 16) & 0xFF),
                    (byte) ((value >> 24) & 0xFF),
                    (byte) ((value >> 32) & 0xFF),
                    (byte) ((value >> 40) & 0xFF),
                    (byte) ((value >> 48) & 0xFF),
                    (byte) ((value >> 56) & 0xFF)};
        } else {
            throw new UnsupportedOperationException("Value greater than 64-bit max");
        }
    }

    private void throwExceptionIfNotSet() {
        if (value == null) {
            throw new IllegalStateException("Variable length integer wasn't built");
        }
    }
}
