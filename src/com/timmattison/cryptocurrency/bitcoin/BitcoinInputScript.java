package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.exceptions.ScriptExecutionException;
import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinWordFactory;
import com.timmattison.cryptocurrency.factories.WordFactory;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.standard.Script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinInputScript extends BitcoinScript implements InputScript {
    private final boolean coinbase;
    private int blockHeight;

    /**
     * Create an input script
     *
     * @param lengthInBytes
     * @param coinbase
     */
    public BitcoinInputScript(BitcoinWordFactory wordFactory, int transactionVersionNumber, long lengthInBytes, boolean coinbase) {
        this.wordFactory = wordFactory;
        this.transactionVersionNumber = transactionVersionNumber;

        this.lengthInBytes = lengthInBytes;
        this.coinbase = coinbase;
    }

    @Override
    public boolean isExecutable() {
        // XXX - This may depend on the version number
        return !coinbase;
    }

    @Override
    protected void validateLength() {
        // Is the length valid?
        if (lengthInBytes == 0) {
            // Possibly not

            // Is this a version 2 script?
            if (transactionVersionNumber == 2) {
                // Yes, no version 2 script can be zero bytes
                throw new UnsupportedOperationException("Version 2 scripts cannot be zero bytes long, [coinbase? " + (coinbase ? "Yes" : "No") + "] [version: " + transactionVersionNumber + "]");
            }
            // Is this a version 1 coinbase?
            else if ((transactionVersionNumber == 1) && (!coinbase)) {
                // No, no version 1 non-coinbase script can be zero bytes
                throw new UnsupportedOperationException("Version 1 scripts cannot be zero bytes long unless they are the coinbase");
            }
        }
    }

    /*
    @Override
    protected void preprocessScriptBytes(byte[] scriptBytes) {
        // Is this the coinbase?
        if (coinbase) {
            // Yes, is this a version 2 block?
            if (transactionVersionNumber == 2) {
                // Yes, process the additional fields

                int scriptBytesPosition = 0;

                // Get the length of the block height value that is coming up
                int lengthOfBlockHeight = (int) EndiannessHelper.ToRealByte(scriptBytes[scriptBytesPosition]);
                scriptBytesPosition++;

                // Read the block height
                byte[] blockHeightBytes = Arrays.copyOfRange(scriptBytes, scriptBytesPosition, lengthOfBlockHeight);
                scriptBytesPosition += lengthOfBlockHeight;

                // Convert the block height bytes into a number
                blockHeight = (int) EndiannessHelper.BytesToValue(blockHeightBytes);
            } else if (transactionVersionNumber == 1) {
                // No, this is a version 1 block, do nothing
            } else {
                // Unknown version number
                throw new UnsupportedOperationException("Expected version 1 or version 2, saw " + transactionVersionNumber);
            }
        }
    }
    */
}
