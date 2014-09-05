package com.timmattison.cryptocurrency.bitcoin.words.stack;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractOpPushData extends StackOp {
    private byte[] valueToPush;

    @Override
    public final byte[] build(byte[] data) {
        int bytesToPush = (int) EndiannessHelper.ToUnsignedValue(Arrays.copyOfRange(data, 0, getValueLength()));
        valueToPush = Arrays.copyOfRange(data, getValueLength(), bytesToPush);
        return Arrays.copyOfRange(data, bytesToPush + getValueLength(), data.length);
    }

    /**
     * Returns the number of bytes used to calculate the length value
     *
     * @return
     */
    protected abstract int getValueLength();

    @Override
    public final void execute(StateMachine stateMachine) {
        stateMachine.push(valueToPush);
    }
}
