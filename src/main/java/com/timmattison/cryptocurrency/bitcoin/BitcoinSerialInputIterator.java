package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import java.util.List;

/**
 * Created by timmattison on 9/29/14.
 */
public class BitcoinSerialInputIterator implements BitcoinInputIterator {
    @Override
    public void iterateOverInputs(Transaction transaction, BitcoinInputProcessor bitcoinInputProcessor) {
        // Is this the first transaction?
        if (transaction.getTransactionNumber() == 0) {
            // Yes, it is the coinbase.  It does not need validation.
            // TODO: Implement BIP-0034 (https://github.com/bitcoin/bips/blob/master/bip-0034.mediawiki)
            return;
        }

        // Get the transaction's inputs
        List<Input> inputs = transaction.getInputs();

        // Start the input counter
        int inputNumber = 0;

        // Loop through all of the inputs
        for (Input input : inputs) {
            bitcoinInputProcessor.process(transaction, inputNumber, input);

            // Increment the input number
            inputNumber++;
        }
    }
}
