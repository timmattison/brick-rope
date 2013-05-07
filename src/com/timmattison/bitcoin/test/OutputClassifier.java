package com.timmattison.bitcoin.test;

import com.timmattison.bitcoin.test.script.Word;
import com.timmattison.bitcoin.test.script.words.constants.VirtualOpPush;
import com.timmattison.bitcoin.test.script.words.crypto.OpCheckSig;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/7/13
 * Time: 7:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class OutputClassifier {
    public static OutputType getOutputType(Output output) {
        if(output == null) {
            throw new UnsupportedOperationException("The output to be classified cannot be NULL");
        }

        if(isSingleSignedOutput(output)) {
            return OutputType.SingleSignedOutput;
        }
        else {
            return OutputType.Unknown;
        }
    }

    private static boolean isSingleSignedOutput(Output output) {
        List<Word> words = output.getScriptWords();

        // Are there any words?
        if(words == null) {
            // No, this is not what we're looking for
            return false;
        }

        // Are there exactly two words?
        if(words.size() != 2) {
            // No, this is not what we're looking for
            return false;
        }

        Word firstWord = words.get(0);
        Word secondWord = words.get(1);

        // Is the first word a push?
        if(!(firstWord instanceof VirtualOpPush)) {
            // No, this is not what we're looking for
            return false;
        }

        // Is the push for 65 bytes?
        if(((VirtualOpPush) firstWord).getInputBytesRequired() != 65) {
            // No, this is not what we're looking for
            return false;
        }

        // Is the second word a checksig?
        if(secondWord instanceof OpCheckSig) {
            // Yes, this is what we want
            return true;
        }

        // No, this is not what we're looking for
        return false;
    }
}
