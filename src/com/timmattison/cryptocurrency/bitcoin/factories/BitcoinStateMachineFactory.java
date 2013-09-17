package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinStateMachine;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/5/13
 * Time: 7:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinStateMachineFactory implements StateMachineFactory {
    private final ScriptingFactory scriptingFactory;

    @Inject
    BitcoinStateMachineFactory(ScriptingFactory scriptingFactory) {
        this.scriptingFactory = scriptingFactory;
    }

    @Override
    public StateMachine createStateMachine() {
        return new BitcoinStateMachine(scriptingFactory);
    }
}
