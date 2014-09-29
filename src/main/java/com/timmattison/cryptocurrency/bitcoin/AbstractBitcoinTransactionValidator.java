package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.standard.interfaces.Script;
import com.timmattison.cryptocurrency.standard.interfaces.ValidationScript;

import javax.inject.Inject;
import java.util.logging.Logger;

public abstract class AbstractBitcoinTransactionValidator implements TransactionValidator, BitcoinInputProcessor {
    private final Logger logger;
    private final TransactionLocator transactionLocator;
    private final ScriptingFactory scriptingFactory;
    private final StateMachineFactory stateMachineFactory;
    private final BitcoinScriptClassifier bitcoinScriptClassifier;

    @Inject
    public AbstractBitcoinTransactionValidator(Logger logger, TransactionLocator transactionLocator, ScriptingFactory scriptingFactory, StateMachineFactory stateMachineFactory, BitcoinScriptClassifier bitcoinScriptClassifier) {
        this.logger = logger;
        this.transactionLocator = transactionLocator;
        this.scriptingFactory = scriptingFactory;
        this.stateMachineFactory = stateMachineFactory;
        this.bitcoinScriptClassifier = bitcoinScriptClassifier;
    }

    @Override
    public void process(Transaction transaction, int inputNumber, Input input) {
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

        boolean isCoinbase = ((transaction.getTransactionNumber() == 0) && (inputNumber == 0));
        bitcoinScriptClassifier.determineScriptType(isCoinbase, inputScript, outputScript).getDescription();
    }
}
