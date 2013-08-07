package com.timmattison.cryptocurrency.bitcoin.words.constants;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.bitcoin.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/5/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ConstantOp implements Word {
    @Override
    public final byte[] build(byte[] data) {
        // Constant operations manipulate the stack but do not consume any bytes
        return data;
    }

    @Override
    public final boolean isEnabled() {
        return true;
    }

    @Override
    public Object getOutput() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public byte[] dump() {
        byte[] dump = new byte[1];
        dump[0] = getOpcode();
        return dump;
    }
}
