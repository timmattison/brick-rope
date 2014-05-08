package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinInput;
import com.timmattison.cryptocurrency.factories.InputFactory;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.VariableLengthIntegerFactory;
import com.timmattison.cryptocurrency.interfaces.Input;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/5/13
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinInputFactory implements InputFactory {
    private final ScriptingFactory scriptingFactory;
    private final VariableLengthIntegerFactory variableLengthIntegerFactory;

    @Inject
    public BitcoinInputFactory(ScriptingFactory scriptingFactory, VariableLengthIntegerFactory variableLengthIntegerFactory) {
        this.scriptingFactory = scriptingFactory;
        this.variableLengthIntegerFactory = variableLengthIntegerFactory;
    }

    @Override
    public Input createInput(int transactionVersionNumber, int inputNumber) {
        return new BitcoinInput(scriptingFactory, variableLengthIntegerFactory, transactionVersionNumber, inputNumber);
    }
}
