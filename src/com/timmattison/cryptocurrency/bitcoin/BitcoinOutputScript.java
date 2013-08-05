package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.exceptions.ScriptExecutionException;
import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinWordFactory;
import com.timmattison.cryptocurrency.factories.WordFactory;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.OutputScript;
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
public class BitcoinOutputScript extends BitcoinScript implements OutputScript {
    /**
     * Create an output script
     *
     * @param lengthInBytes
     */
    public BitcoinOutputScript(BitcoinWordFactory wordFactory, long lengthInBytes) {
        this.wordFactory = wordFactory;

        this.lengthInBytes = lengthInBytes;
        this.versionNumber = 1;
    }

    @Override
    protected boolean isExecutable() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void preprocessScriptBytes(byte[] scriptBytes) {
        // Nothing to do here
    }

    @Override
    protected void validateLength() {
        if(lengthInBytes > 0) {
            throw new UnsupportedOperationException("Output scripts cannot be zero length");
        }
    }
}
