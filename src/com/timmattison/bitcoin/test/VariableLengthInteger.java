package com.timmattison.bitcoin.test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/16/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class VariableLengthInteger extends ByteConsumer {
    private static final String name = "VAR INT";

    private static final int header16BitInteger = 0xfd;
    private static final int header32BitInteger = 0xfe;
    private static final int header64BitInteger = 0xff;

    /**
     * Value
     */
    long value;
    private byte[] valueBytes;

    public VariableLengthInteger(InputStream inputStream, boolean debug) throws IOException {
        super(inputStream, debug);

        build();
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void build() throws IOException {
        // Get the first byte
        long firstByte = pullBytes(1, "Variable length integer, reading first byte")[0];

        int bytesToRead = 0;

        // What size value is this?
        if (firstByte == (byte) header16BitInteger) {
            // 16-bit
            bytesToRead = 2;
            valueBytes = pullBytes(bytesToRead, "Variable length integer, reading short");
            value = EndiannessHelper.BytesToShort(valueBytes);
        } else if (firstByte == (byte) header32BitInteger) {
            // 32-bit
            bytesToRead = 4;
            valueBytes = pullBytes(bytesToRead, "Variable length integer, reading int");
            value = EndiannessHelper.BytesToInt(valueBytes);
        } else if (firstByte == (byte) header64BitInteger) {
            // 64-bit
            bytesToRead = 8;
            valueBytes = pullBytes(bytesToRead, "Variable length integer, reading long");
            value = EndiannessHelper.BytesToLong(valueBytes);
        } else {
            // 8-bit (mask this so that we don't get negative values)
            valueBytes = new byte[1];
            value = (firstByte & 0x0FF);
            valueBytes[0] = (byte) value;
        }

        // Did we have a multi-byte value?
        if(valueBytes.length != 1) {
            // Yes, add the missing first byte
            byte[] tempValueBytes = new byte[valueBytes.length + 1];
            tempValueBytes[0] = (byte) firstByte;

            for(int loop = 0; loop < valueBytes.length; loop++) {
                tempValueBytes[loop + 1] = valueBytes[loop];
            }

            // Store the true bytes back in valueBytes
            valueBytes = tempValueBytes;
        }
    }

    @Override
    protected String dump(boolean pretty) {
        StringBuilder stringBuilder = new StringBuilder();

        if (pretty) {
            stringBuilder.append("Variable length integer data:\n");
        }

        DumpHelper.dump(stringBuilder, pretty, "\tValue: ", "\n", valueBytes);

        return stringBuilder.toString();
    }

    @Override
    protected byte[] dumpBytes() throws IOException {
        return getValueBytes();
    }

    public long getValue() {
        return value;
    }

    public byte[] getValueBytes() {
        return valueBytes;
    }
}
