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

    long value;

    public VariableLengthInteger(InputStream inputStream, boolean debug) throws IOException {
        super(inputStream, debug);
    }

    @Override
    protected void initialize(Object[] objects) {
        throw new UnsupportedOperationException("Additional initialization not necessary");
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void innerShowDebugInfo() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void build() throws IOException {
        // Get the first byte
        long firstByte = pullBytes(1)[0];

        int bytesToRead = 0;

        // What size value is this?
        if (firstByte == header16BitInteger) {
            // 16-bit
            bytesToRead = 2;
            value = EndiannessHelper.BytesToShort(pullBytes(bytesToRead));
        } else if (firstByte == header32BitInteger) {
            // 32-bit
            bytesToRead = 4;
            value = EndiannessHelper.BytesToInt(pullBytes(bytesToRead));
        } else if (firstByte == header64BitInteger) {
            // 64-bit
            bytesToRead = 8;
            value = EndiannessHelper.BytesToLong(pullBytes(bytesToRead));
        } else {
            // 8-bit (mask this so that we don't get negative values)
            value = (firstByte & 0x0FF);
        }
    }

    public long getValue() {
        return value;
    }
}
