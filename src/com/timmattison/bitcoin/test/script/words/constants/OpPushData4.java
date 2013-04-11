package com.timmattison.bitcoin.test.script.words.constants;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.bitcoin.test.script.ByteConsumingWord;
import com.timmattison.bitcoin.test.script.StateMachine;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpPushData4 extends ByteConsumingWord {
    private static final String word = "OP_PUSHDATA4";
    private static final Byte opcode = (byte) 0x4e;

    public OpPushData4() {
        super(word, opcode);
    }

    @Override
    protected List<Byte> doAdditionalProcessing(List<Byte> input) {
        // TODO - This is the same in all three OpPushDataX classes, extract it
        validateFirstStageInput();

        // Our input now represents the number of bytes we are going to read
        int bytesToRead = input.get(0) + (input.get(1) << 8) + (input.get(2) << 16) + (input.get(3) << 24);
        int inputSize = input.size();

        // TODO - This is the same in all three OpPushDataX classes, extract it
        validateBytesToRead(bytesToRead, inputSize);

        // TODO - This is the same in all three OpPushDataX classes, extract it
        // Get the bytes we need
        this.input = ByteArrayHelper.grabHeadBytes(input, bytesToRead);
        return ByteArrayHelper.grabTailBytes(input, bytesToRead);
    }

    @Override
    protected boolean isAdditionalProcessingRequired() {
        return true;

    }

    @Override
    public int getInputBytesRequired() {
        return 4;
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}
