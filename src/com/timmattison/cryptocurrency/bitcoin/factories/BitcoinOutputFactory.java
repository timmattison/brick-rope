package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinOutput;
import com.timmattison.cryptocurrency.factories.OutputFactory;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.interfaces.Output;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/5/13
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinOutputFactory implements OutputFactory {
    private final ScriptingFactory scriptingFactory;

    @Inject
    public BitcoinOutputFactory(ScriptingFactory scriptingFactory) {
        this.scriptingFactory = scriptingFactory;
    }

    @Override
    public Output createOutput(int transactionVersionNumber, int outputNumber) {
        return new BitcoinOutput(scriptingFactory, transactionVersionNumber, outputNumber);
    }
}
