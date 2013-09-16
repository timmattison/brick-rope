package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinInputScript;
import com.timmattison.cryptocurrency.bitcoin.BitcoinOutputScript;
import com.timmattison.cryptocurrency.bitcoin.BitcoinScript;
import com.timmattison.cryptocurrency.bitcoin.BitcoinValidationScript;
import com.timmattison.cryptocurrency.factories.ScriptFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.standard.OutputScript;
import com.timmattison.cryptocurrency.standard.Script;
import com.timmattison.cryptocurrency.standard.ValidationScript;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/5/13
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinScriptFactory implements ScriptFactory {
    private final BitcoinWordFactory bitcoinWordFactory;

    @Inject
    public BitcoinScriptFactory(BitcoinWordFactory bitcoinWordFactory) {
        this.bitcoinWordFactory = bitcoinWordFactory;
    }

    @Override
    public InputScript createInputScript(int transactionVersionNumber, long scriptLength, boolean coinbase) {
        return (InputScript) new BitcoinInputScript(bitcoinWordFactory, transactionVersionNumber, scriptLength, coinbase);
    }

    @Override
    public OutputScript createOutputScript(int transactionVersionNumber, long scriptLength) {
        return (OutputScript) new BitcoinOutputScript(bitcoinWordFactory, transactionVersionNumber, scriptLength);
    }

    @Override
    public ValidationScript createValidationScript(Script inputScript, Script outputScript) {
        ValidationScript validationScript = new BitcoinValidationScript(bitcoinWordFactory, inputScript.dump().length + outputScript.dump().length);
        validationScript.build(ByteArrayHelper.concatenate(inputScript.dump(), outputScript.dump()));

        return validationScript;
    }
}
