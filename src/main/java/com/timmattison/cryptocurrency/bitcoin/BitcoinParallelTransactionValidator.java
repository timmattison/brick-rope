package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BitcoinParallelTransactionValidator extends AbstractBitcoinTransactionValidator {
    private static final int threads = 10;
    private ExecutorService executor = Executors.newFixedThreadPool(threads);
    private List<Future<Boolean>> results;

    @Inject
    public BitcoinParallelTransactionValidator(TransactionLocator transactionLocator, ScriptingFactory scriptingFactory, StateMachineFactory stateMachineFactory) {
        super(transactionLocator, scriptingFactory, stateMachineFactory);
    }

    @Override
    protected boolean allValid() {
        boolean returnValue = true;

        for (Future<Boolean> future : results) {
            try {
                if (future.get().equals(Boolean.FALSE)) {
                    returnValue = false;
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                returnValue = false;
                break;
            } catch (ExecutionException e) {
                e.printStackTrace();
                returnValue = false;
                break;
            }
        }

        executor.shutdown();

        results = null;
        executor = null;

        return returnValue;
    }

    @Override
    protected void doValidation(final Transaction transaction, final int inputNumber, final Input input) {
        if (results == null) {
            results = new ArrayList<Future<Boolean>>();
        }

        if (executor == null) {
            executor = Executors.newFixedThreadPool(threads);
        }

        final Callable<Boolean> worker = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    innerValidate(transaction, inputNumber, input);
                } catch (Exception e) {
                    return false;
                }

                return true;
            }
        };

        Future<Boolean> submit = executor.submit(worker);
        results.add(submit);
    }
}
