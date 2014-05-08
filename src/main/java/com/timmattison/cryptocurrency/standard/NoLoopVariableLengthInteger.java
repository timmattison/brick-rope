package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.standard.interfaces.VariableLengthInteger;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/16/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class NoLoopVariableLengthInteger implements VariableLengthInteger {
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

    public NoLoopVariableLengthInteger() {
    }

    @Override
    public void setValue(Long value) {
        this.value = value;
        valueBytes = null;
    }

    @Override
    public byte[] build(byte[] data) {
        // Get the first byte
        byte firstByte = data[0];
        data = Arrays.copyOfRange(data, 1, data.length);

        int bytesToRead = 0;

        // What size value is this?
        if (firstByte == (byte) header16BitInteger) {
            // 16-bit
            bytesToRead = 2;
            data = generateValueBytes(data, firstByte, bytesToRead);
            value = Long.valueOf(EndiannessHelper.BytesToShort(valueBytes, 1));
        } else if (firstByte == (byte) header32BitInteger) {
            // 32-bit
            bytesToRead = 4;
            data = generateValueBytes(data, firstByte, bytesToRead);
            value = Long.valueOf(EndiannessHelper.BytesToInt(valueBytes, 1));
        } else if (firstByte == (byte) header64BitInteger) {
            // 64-bit
            bytesToRead = 8;
            data = generateValueBytes(data, firstByte, bytesToRead);
            value = EndiannessHelper.BytesToLong(valueBytes, 1);
        } else {
            // 8-bit (mask this so that we don't get negative values)
            valueBytes = new byte[1];
            value = Long.valueOf((firstByte & 0x0FF));
            valueBytes[0] = (byte) (value & 0xFF);
        }

        // Return what is left
        return data;
    }

    private byte[] generateValueBytes(byte[] data, byte firstByte, int bytesToRead) {
        valueBytes = new byte[bytesToRead + 1];
        valueBytes[0] = firstByte;
        data = Arrays.copyOfRange(data, bytesToRead, data.length);
        System.arraycopy(data, 0, valueBytes, 1, data.length);
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

    @Override
    public long getValue() {
        throwExceptionIfNotSet();
        return value;
    }

    @Override
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
            // It is a two byte value
            valueBytes = new byte[]{
                    (byte) header16BitInteger,
                    (byte) (value & 0xFF),
                    (byte) ((value >> 8) & 0xFF)};
        } else if (value < max32BitValue) {
            // It is a four byte value
            valueBytes = new byte[]{
                    (byte) header32BitInteger,
                    (byte) (value & 0xFF),
                    (byte) ((value >> 8) & 0xFF),
                    (byte) ((value >> 16) & 0xFF),
                    (byte) ((value >> 24) & 0xFF)};
        } else {
            // It is an eight byte value
            valueBytes = new byte[]{
                    (byte) header64BitInteger,
                    (byte) (value & 0xFF),
                    (byte) ((value >> 8) & 0xFF),
                    (byte) ((value >> 16) & 0xFF),
                    (byte) ((value >> 24) & 0xFF),
                    (byte) ((value >> 32) & 0xFF),
                    (byte) ((value >> 40) & 0xFF),
                    (byte) ((value >> 48) & 0xFF),
                    (byte) ((value >> 56) & 0xFF)};
        }
    }

    private void throwExceptionIfNotSet() {
        if (value == null) {
            throw new IllegalStateException("Variable length integer wasn't built");
        }
    }
}
