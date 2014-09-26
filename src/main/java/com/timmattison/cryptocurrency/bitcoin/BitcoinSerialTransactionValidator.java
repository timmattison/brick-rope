package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

public class BitcoinSerialTransactionValidator extends AbstractBitcoinTransactionValidator {
    @Inject
    public BitcoinSerialTransactionValidator(Logger logger, TransactionLocator transactionLocator, ScriptingFactory scriptingFactory, StateMachineFactory stateMachineFactory, BitcoinScriptClassifier bitcoinScriptClassifier) {
        super(logger, transactionLocator, scriptingFactory, stateMachineFactory, bitcoinScriptClassifier);
    }

    @Override
    public boolean isValid(Transaction transaction) {
        // Is this the first transaction?
        if (transaction.getTransactionNumber() == 0) {
            // Yes, it is the coinbase.  It does not need validation.
            // TODO: Implement BIP-0034 (https://github.com/bitcoin/bips/blob/master/bip-0034.mediawiki)
            return true;
        }

        // Get the transaction's inputs
        List<Input> inputs = transaction.getInputs();

        // Start the input counter
        int inputNumber = 0;

        // Loop through all of the inputs
        for (Input input : inputs) {
            try {
                innerValidateTransactionInput(transaction, inputNumber, input);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            // Increment the input number
            inputNumber++;
        }

        return true;
    }
}
