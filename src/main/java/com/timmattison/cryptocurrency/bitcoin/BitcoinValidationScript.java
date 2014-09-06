package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinScriptingFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.standard.interfaces.ValidationScript;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinValidationScript extends BitcoinScript implements ValidationScript {
    private int previousOutputIndex;

    /**
     * Create a validation script
     *
     * @param lengthInBytes
     */
    public BitcoinValidationScript(BitcoinScriptingFactory wordFactory, long lengthInBytes) {
        this.scriptingFactory = wordFactory;

        this.lengthInBytes = lengthInBytes;
    }

    @Override
    public boolean isExecutable() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void validateLength() {
        if (lengthInBytes == 0) {
            throw new UnsupportedOperationException("Validation scripts cannot be zero length");
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

        for (int loop = 0; loop < indentationLevel; loop++) {
            stringBuilder.append("\t");
        }

        stringBuilder.append("Script bytes: ");
        stringBuilder.append(ByteArrayHelper.toHex(scriptBytes));
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}
