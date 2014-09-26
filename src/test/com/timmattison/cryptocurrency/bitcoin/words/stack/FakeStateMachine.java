package com.timmattison.cryptocurrency.bitcoin.words.stack;

import com.timmattison.cryptocurrency.bitcoin.BitcoinStateMachine;
import com.timmattison.cryptocurrency.standard.interfaces.Script;

/**
 * Created by timmattison on 9/5/14.
 */
public class FakeStateMachine extends BitcoinStateMachine {
    public FakeStateMachine() {
        super(null, null, null);
    }

    @Override
    public void execute(Script script) {
        throw new UnsupportedOperationException();
    }
}
