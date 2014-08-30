package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;

import javax.inject.Inject;

public class BitcoinSerialTransactionValidator extends AbstractBitcoinTransactionValidator {
    @Inject
    public BitcoinSerialTransactionValidator(TransactionLocator transactionLocator, ScriptingFactory scriptingFactory, StateMachineFactory stateMachineFactory) {
        super(transactionLocator, scriptingFactory, stateMachineFactory);
    }

    @Override
    protected boolean allValid() {
        /*
         * Since innerValidate would throw an exception before we got here we can always return true.  If it wasn't
         * valid we'd never get here.
         */

        return true;
    }

    @Override
    protected void doValidation(Transaction transaction, int inputNumber, Input input) {
        innerValidate(transaction, inputNumber, input);
    }
}
