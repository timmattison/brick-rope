package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinWordFactory;
import com.timmattison.cryptocurrency.standard.OutputScript;

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
    public BitcoinOutputScript(BitcoinWordFactory wordFactory, int transactionVersionNumber, long lengthInBytes) {
        this.wordFactory = wordFactory;
        this.transactionVersionNumber = transactionVersionNumber;

        this.lengthInBytes = lengthInBytes;
    }

    @Override
    public boolean isExecutable() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void validateLength() {
        if (lengthInBytes == 0) {
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
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}
