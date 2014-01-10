package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinScriptingFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.standard.InputScript;

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
    public BitcoinInputScript(BitcoinScriptingFactory wordFactory, int transactionVersionNumber, long lengthInBytes, boolean coinbase) {
        this.scriptingFactory = wordFactory;
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
                // XXX - This is a problem in block 211914
                throw new UnsupportedOperationException("Version 1 scripts cannot be zero bytes long unless they are the coinbase");
            }
        }
    }

    @Override
    public byte[] dump() {
        return scriptBytes;
    }

    @Override
    public String prettyDump(int indentationLevel) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");

        StringBuilder indentation = new StringBuilder();

        for (int loop = 0; loop < indentationLevel; loop++) {
            indentation.append("\t");
        }

        stringBuilder.append(indentation);
        stringBuilder.append("Script bytes: ");
        stringBuilder.append(ByteArrayHelper.toHex(scriptBytes));

        for (Word word : dumpWords()) {
            stringBuilder.append(word.prettyDump(indentationLevel + 1));
        }

        return stringBuilder.toString();
    }

    private List<Word> dumpWords() {
        List<Word> words = new ArrayList<Word>();

        byte[] scriptBytesCopy = scriptBytes;

        while ((scriptBytesCopy != null) && (scriptBytesCopy.length > 0)) {
            // Build the next word
            byte currentByte = scriptBytesCopy[0];

            // Get the word that the next byte corresponds to
            Word currentWord = scriptingFactory.createWord(currentByte);
            scriptBytesCopy = currentWord.build(Arrays.copyOfRange(scriptBytesCopy, 1, scriptBytesCopy.length));
            words.add(currentWord);
        }

        return words;
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
