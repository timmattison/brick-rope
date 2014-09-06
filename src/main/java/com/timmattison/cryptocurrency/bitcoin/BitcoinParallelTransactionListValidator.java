package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.helpers.FutureHelper;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionListValidator;
import com.timmattison.cryptocurrency.interfaces.TransactionValidator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class BitcoinParallelTransactionListValidator implements TransactionListValidator {
    private final Logger logger;
    private final TransactionValidator transactionValidator;
    private final ExecutorService executorService;

    @Inject
    public BitcoinParallelTransactionListValidator(Logger logger, TransactionValidator transactionValidator, ExecutorService executorService) {
        this.logger = logger;
        this.transactionValidator = transactionValidator;
        this.executorService = executorService;
    }

    @Override
    public boolean isValid(final long blockNumber, List<Transaction> transactionList) {
        List<Future<Boolean>> results = new ArrayList<Future<Boolean>>();

        int size = transactionList.size();

        if (size == 1) {
            // Assume the coinbase is always valid
            // TODO: Make this compliant with new BIPs that require the coinbase to include certain data
            return true;
        }

        logger.info("Validating " + transactionList.size() + " transaction(s) in block number " + blockNumber);

        for (final Transaction currentTransaction : transactionList) {
            if (currentTransaction.getTransactionNumber() == 0) {
                continue;
            }

            final Callable<Boolean> worker = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        // Validate the transaction
                        if (!transactionValidator.isValid(currentTransaction)) {
                            logger.info("Transactions in block number " + blockNumber + " are not valid");
                            return false;
                        }
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
        }

        return FutureHelper.allTrue(results);
    }
}
