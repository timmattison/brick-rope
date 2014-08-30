package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionListValidator;
import com.timmattison.cryptocurrency.interfaces.TransactionValidator;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

public class BitcoinSerialTransactionListValidator implements TransactionListValidator {
    private final Logger logger;
    private final TransactionValidator transactionValidator;

    @Inject
    public BitcoinSerialTransactionListValidator(Logger logger, TransactionValidator transactionValidator) {
        this.logger = logger;
        this.transactionValidator = transactionValidator;
    }

    @Override
    public boolean isValid(long blockNumber, List<Transaction> transactionList) {
        for (Transaction currentTransaction : transactionList) {
            if (currentTransaction.getTransactionNumber() == 0) {
                continue;
            }

            // Validate the transaction
            if (!transactionValidator.isValid(currentTransaction)) {
                logger.info("Transactions in block number " + blockNumber + " are not valid");
                return false;
            }
        }

        return true;
    }
}
