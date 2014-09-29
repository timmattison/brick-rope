package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;

import javax.inject.Inject;
import java.util.logging.Logger;

public class BitcoinSerialTransactionValidator extends AbstractBitcoinTransactionValidator implements BitcoinInputProcessor {
    private final BitcoinInputIterator bitcoinInputIterator;

    @Inject
    public BitcoinSerialTransactionValidator(Logger logger, TransactionLocator transactionLocator, ScriptingFactory scriptingFactory, StateMachineFactory stateMachineFactory, BitcoinScriptClassifier bitcoinScriptClassifier, BitcoinInputIterator bitcoinInputIterator) {
        super(logger, transactionLocator, scriptingFactory, stateMachineFactory, bitcoinScriptClassifier);
        this.bitcoinInputIterator = bitcoinInputIterator;
    }

    @Override
    public boolean isValid(Transaction transaction) {
        try {
            bitcoinInputIterator.iterateOverInputs(transaction, this);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
