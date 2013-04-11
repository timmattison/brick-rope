package com.timmattison.bitcoin.test.script.words.constants;

import com.timmattison.bitcoin.test.script.ByteConsumingWord;
import com.timmattison.bitcoin.test.script.StateMachine;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualOpPush extends ByteConsumingWord {
    private static final String word = "VIRTUAL_OP_PUSH";
    private static final int MIN_OPCODE = 0x01;
    private static final int MAX_OPCODE = 0x4B;

    public VirtualOpPush(Byte opcode) {
        super(word, opcode);

        // Is the opcode valid?
        if ((opcode < MIN_OPCODE) || (opcode > MAX_OPCODE)) {
            // No, throw an exception
            throw new UnsupportedOperationException("Opcode must be between " + MIN_OPCODE + " and " + MAX_OPCODE + ", saw " + opcode);
        }
    }

    @Override
    public String getWord() {
        return word + " " + opcode;
    }

    @Override
    protected List<Byte> doAdditionalProcessing(List<Byte> input) {
        return null;
    }

    @Override
    protected boolean isAdditionalProcessingRequired() {
        return false;
    }

    @Override
    public int getInputBytesRequired() {
        return opcode;
    }

    @Override
    public void execute(StateMachine stateMachine) {
        Byte[] data = ((List<Byte>) input).toArray(new Byte[input.size()]);
        stateMachine.stack.push(data);
    }
}
