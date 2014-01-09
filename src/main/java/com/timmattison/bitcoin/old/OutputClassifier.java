package com.timmattison.bitcoin.old;

import com.timmattison.bitcoin.old.script.Word;
import com.timmattison.bitcoin.old.script.words.bitwiselogic.OpEqual;
import com.timmattison.bitcoin.old.script.words.constants.OpTrue;
import com.timmattison.bitcoin.old.script.words.constants.VirtualOpPush;
import com.timmattison.bitcoin.old.script.words.crypto.OpCheckSig;
import com.timmattison.bitcoin.old.script.words.crypto.OpHash256;
import com.timmattison.bitcoin.old.script.words.flowcontrol.OpReturn;

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
        if (output == null) {
            throw new UnsupportedOperationException("The output to be classified cannot be NULL");
        }

        if (isSingleSignedOutput(output)) {
            return OutputType.SingleSigned;
        } else if (isProvablyUnspendable(output)) {
            return OutputType.ProvablyUnspendable;
        } else if (isAnyoneCanSpend(output)) {
            return OutputType.AnyoneCanSpend;
        } else if (isHashingPuzzle(output)) {
            return OutputType.HashingPuzzle;
        } else {
            return OutputType.Unknown;
        }
    }

    private static boolean basicNullCheck(Output output) {
        // Are there any words?
        if (output.getScriptWords() == null) {
            // No, this is not what we're looking for
            return false;
        }

        return true;
    }

    private static boolean basicSizeCheck(Output output, int size) {
        if (!basicNullCheck(output)) {
            return false;
        }

        // Are there exactly the number of words we need?
        if (output.getScriptWords().size() != size) {
            // No, this is not what we're looking for
            return false;
        }

        return true;
    }

    private static boolean isSingleSignedOutput(Output output) {
        // Return immediately if it doesn't pass the basic checks
        if(!basicSizeCheck(output, 2)) { return false; }

        List<Word> words = output.getScriptWords();

        Word firstWord = words.get(0);
        Word secondWord = words.get(1);

        // Is the first word a push?
        if (!(firstWord instanceof VirtualOpPush)) {
            // No, this is not what we're looking for
            return false;
        }

        // Is the push for 65 bytes?
        if (((VirtualOpPush) firstWord).getInputBytesRequired() != 65) {
            // No, this is not what we're looking for
            return false;
        }

        // Is the second word a checksig?
        if (secondWord instanceof OpCheckSig) {
            // Yes, this is what we want
            return true;
        }

        // No, this is not what we're looking for
        return false;
    }

    private static boolean isHashingPuzzle(Output output) {
        // Return immediately if it doesn't pass the basic checks
        if(!basicSizeCheck(output, 3)) { return false; }

        List<Word> words = output.getScriptWords();

        Word firstWord = words.get(0);
        Word secondWord = words.get(1);
        Word thirdWord = words.get(2);

        if(!(firstWord instanceof OpHash256)) {
            return false;
        }

        if(!(secondWord instanceof VirtualOpPush)) {
            return false;
        }

        if(!(thirdWord instanceof OpEqual)) {
            return false;
        }

        return true;
    }

    private static boolean isAnyoneCanSpend(Output output) {
        // Return immediately if it doesn't pass the basic checks
        if(!basicSizeCheck(output, 1)) { return false; }

        List<Word> words = output.getScriptWords();

        Word firstWord = words.get(0);

        if(firstWord instanceof OpTrue) {
            return true;
        }
        else {
            return false;
        }
    }

    private static boolean isProvablyUnspendable(Output output) {
        // Return immediately if it doesn't pass the basic checks
        if(!sizeGreaterThanOrEqualTo(output, 1)) { return false; }

        List<Word> words = output.getScriptWords();

        Word firstWord = words.get(0);

        if(firstWord instanceof OpReturn) {
            return true;
        }
        else {
            return false;
        }
    }

    private static boolean sizeGreaterThanOrEqualTo(Output output, int size) {
        // Are there at least the number of words we need?
        if (output.getScriptWords().size() < size) {
            // No, this is not what we're looking for
            return false;
        }

        return true;
    }
}
