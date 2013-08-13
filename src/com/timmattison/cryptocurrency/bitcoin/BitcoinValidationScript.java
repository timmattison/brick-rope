package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinWordFactory;
import com.timmattison.cryptocurrency.standard.OutputScript;
import com.timmattison.cryptocurrency.standard.ValidationScript;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinValidationScript extends BitcoinScript implements ValidationScript {
    /**
     * Create a validation script
     *
     * @param lengthInBytes
     */
    public BitcoinValidationScript(BitcoinWordFactory wordFactory, long lengthInBytes) {
        this.wordFactory = wordFactory;

        this.lengthInBytes = lengthInBytes;
    }

    @Override
    public boolean isExecutable() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void validateLength() {
        if(lengthInBytes == 0) {
            throw new UnsupportedOperationException("Validation scripts cannot be zero length");
        }
    }

    @Override
    public byte[] dump() {
        return scriptBytes;
    }
}
