package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinScriptingFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.standard.interfaces.OutputScript;

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
public class BitcoinOutputScript extends BitcoinScript implements OutputScript {
    /**
     * Create an output script
     *
     * @param lengthInBytes
     */
    public BitcoinOutputScript(BitcoinScriptingFactory wordFactory, int transactionVersionNumber, long lengthInBytes) {
        this.scriptingFactory = wordFactory;
        this.transactionVersionNumber = transactionVersionNumber;

        this.lengthInBytes = lengthInBytes;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    protected void validateLength() {
        if (lengthInBytes == 0) {
            // XXX - This is a problem in block 211914
            throw new UnsupportedOperationException("Output scripts cannot be zero length");
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
}
