package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinOutput;
import com.timmattison.cryptocurrency.factories.OutputFactory;
import com.timmattison.cryptocurrency.interfaces.Output;
import com.timmattison.cryptocurrency.factories.ScriptFactory;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/5/13
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinOutputFactory implements OutputFactory {
    private final ScriptFactory scriptFactory;

    @Inject
    public BitcoinOutputFactory(ScriptFactory scriptFactory) {
        this.scriptFactory = scriptFactory;
    }

    @Override
    public Output createOutput(int transactionVersionNumber, int outputNumber) {
        return new BitcoinOutput(scriptFactory, transactionVersionNumber, outputNumber);
    }
}
