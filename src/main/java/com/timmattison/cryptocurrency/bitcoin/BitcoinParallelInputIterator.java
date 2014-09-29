package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.Inject;
import com.timmattison.cryptocurrency.helpers.FutureHelper;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by timmattison on 9/29/14.
 */
public class BitcoinParallelInputIterator implements BitcoinInputIterator {
    private final ExecutorService executorService;

    @Inject
    public BitcoinParallelInputIterator(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void iterateOverInputs(final Transaction transaction, final BitcoinInputProcessor bitcoinInputProcessor) {
        List<Future<Boolean>> results = new ArrayList<Future<Boolean>>();

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
        for (final Input input : inputs) {
            final int currentInputNumber = inputNumber;

            final Callable<Boolean> worker = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        bitcoinInputProcessor.process(transaction, currentInputNumber, input);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }

                    return true;
                }
            };

            try {
                Future<Boolean> submit = executorService.submit(worker);
                results.add(submit);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Increment the input number
            inputNumber++;
        }

        FutureHelper.allTrue(results);
    }
}
