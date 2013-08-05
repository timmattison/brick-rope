package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinInput;
import com.timmattison.cryptocurrency.factories.InputFactory;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.factories.ScriptFactory;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/5/13
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinInputFactory implements InputFactory {
    private final ScriptFactory scriptFactory;

    @Inject
    public BitcoinInputFactory(ScriptFactory scriptFactory) {
        this.scriptFactory = scriptFactory;
    }

    @Override
    public Input createInput(boolean coinbase, int inputNumber) {
        return new BitcoinInput(scriptFactory, coinbase, inputNumber);
    }
}
