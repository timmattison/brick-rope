package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.WordFactory;
import com.timmattison.cryptocurrency.standard.Script;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BitcoinScript implements Script {
    //private static final int MAX_WORD_LIST_LENGTH = 201;
    private static final int MAX_WORD_LIST_LENGTH = 9999;
    // These values are not in the script
    protected long lengthInBytes;
    protected WordFactory wordFactory;
    // Raw bytes, in order they were pulled from the block chain
    protected int transactionVersionNumber;
    /**
     * Script bytes
     */
    protected byte[] scriptBytes;

    @Override
    public byte[] build(byte[] data) {
        // Is there any data?
        if (data.length == 0) {
            // No, throw an exception
            throw new UnsupportedOperationException("The script is empty");
        }

        // Are there enough bytes to support that?
        if (data.length < lengthInBytes) {
            // No, throw an exception
            throw new UnsupportedOperationException("Script is supposed to have " + lengthInBytes + " byte(s) but only " + data.length + " byte(s) are left");
        }

        // Validate the length
        validateLength();

        // Is there anything to process?
        if (lengthInBytes == 0) {
            // No, just return the bytes we were given
            return data;
        }

        /**
         * Move the bytes we want into a new list.  This is so we can be sure that a misbehaving opcode doesn't try to
         * read past the end of the script.  NOTE: Technically we only support scripts up to 2GB!
         */
        scriptBytes = Arrays.copyOfRange(data, 0, (int) lengthInBytes);

        return Arrays.copyOfRange(data, (int) lengthInBytes, data.length);
    }

    protected abstract void validateLength();
}
