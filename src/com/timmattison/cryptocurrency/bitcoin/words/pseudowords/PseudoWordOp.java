package com.timmattison.cryptocurrency.bitcoin.words.pseudowords;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.bitcoin.Word;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/5/13
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PseudoWordOp implements Word {
    @Override
    public final boolean isEnabled() {
        return true;
    }

    @Override
    public final byte[] build(byte[] data) {
        // Pseudo-word operations manipulate the stack but do not consume any bytes
        return data;
    }

    @Override
    public Object getOutput() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}