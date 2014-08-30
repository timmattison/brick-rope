package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.standard.interfaces.Script;
import com.timmattison.cryptocurrency.standard.interfaces.ValidationScript;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public abstract class AbstractBitcoinTransactionValidator implements TransactionValidator {
    private final TransactionLocator transactionLocator;
    private final ScriptingFactory scriptingFactory;
    private final StateMachineFactory stateMachineFactory;

    @Inject
    public AbstractBitcoinTransactionValidator(TransactionLocator transactionLocator, ScriptingFactory scriptingFactory, StateMachineFactory stateMachineFactory) {
        this.transactionLocator = transactionLocator;
        this.scriptingFactory = scriptingFactory;
        this.stateMachineFactory = stateMachineFactory;
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
            doValidation(transaction, inputNumber, input);

            // Increment the input number
            inputNumber++;
        }

        return allValid();
    }

    protected abstract boolean allValid();

    protected abstract void doValidation(Transaction transaction, int inputNumber, Input input);

    protected void innerValidate(Transaction transaction, int inputNumber, Input input) {
        // Get the index of the previous output that this input is taken from
        long previousOutputIndex = input.getPreviousOutputIndex();

        // Get the previous transaction
        Transaction previousTransaction = transactionLocator.findTransaction(input.getPreviousTransactionId());

        // Get the previous output
        Output previousOutput = previousTransaction.getOutputs().get((int) previousOutputIndex);

        // Get the input script
        Script inputScript = input.getScript();

        // Get the previous output script
        Script outputScript = previousOutput.getScript();

        // Create a validation script from the input script and the output script
        ValidationScript validationScript = scriptingFactory.createValidationScript(inputScript, outputScript);

        // Create a state machine
        StateMachine stateMachine = stateMachineFactory.createStateMachine();

        // Set the previous transaction hash, the previous output index, the current transaction hash, and the input number
        stateMachine.setPreviousTransactionHash(input.getPreviousTransactionId());
        stateMachine.setPreviousOutputIndex((int) previousOutputIndex);
        stateMachine.setCurrentTransactionHash(transaction.getHash());
        stateMachine.setInputNumber(inputNumber);

        // Execute the validation script
        stateMachine.execute(validationScript);
    }
}
