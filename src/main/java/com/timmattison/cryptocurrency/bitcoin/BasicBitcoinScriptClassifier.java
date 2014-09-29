package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.Inject;
import com.timmattison.cryptocurrency.bitcoin.words.bitwiselogic.OpEqual;
import com.timmattison.cryptocurrency.bitcoin.words.bitwiselogic.OpEqualVerify;
import com.timmattison.cryptocurrency.bitcoin.words.constants.OpTrue;
import com.timmattison.cryptocurrency.bitcoin.words.crypto.OpCheckSig;
import com.timmattison.cryptocurrency.bitcoin.words.crypto.OpHash160;
import com.timmattison.cryptocurrency.bitcoin.words.crypto.OpHash256;
import com.timmattison.cryptocurrency.bitcoin.words.flowcontrol.OpReturn;
import com.timmattison.cryptocurrency.bitcoin.words.stack.*;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.standard.interfaces.Script;
import com.timmattison.cryptocurrency.standard.interfaces.ScriptToWordListConverter;
import com.timmattison.cryptocurrency.standard.interfaces.ValidationScript;

import java.util.List;

/**
 * Created by timmattison on 9/26/14.
 */
public class BasicBitcoinScriptClassifier implements BitcoinScriptClassifier {
    private final ScriptToWordListConverter scriptToWordListConverter;
    private final ScriptingFactory scriptingFactory;

    @Inject
    public BasicBitcoinScriptClassifier(ScriptToWordListConverter scriptToWordListConverter, ScriptingFactory scriptingFactory) {
        this.scriptToWordListConverter = scriptToWordListConverter;
        this.scriptingFactory = scriptingFactory;
    }

    @Override
    public BitcoinScriptType determineScriptType(boolean isCoinbase, Script inputScript, Script outputScript) {
        List<Word> outputWords = scriptToWordListConverter.convert(outputScript);

        if (outputWords != null) {
            if (isProvablyUnspendable(outputWords)) {
                return BitcoinScriptType.ProvablyUnspendable;
            }

            if (isTransactionPuzzle(outputWords)) {
                return BitcoinScriptType.TransactionPuzzle;
            }
        }

        List<Word> inputWords = scriptToWordListConverter.convert(inputScript);

        if (inputWords != null) {
            if (isAnyoneCanSpend(inputWords)) {
                return BitcoinScriptType.AnyoneCanSpend;
            }
        }

        ValidationScript validationScript = scriptingFactory.createValidationScript(inputScript, outputScript);

        List<Word> validationWords = scriptToWordListConverter.convert(validationScript);

        if (validationWords != null) {
            if (isPayToPubkey(validationWords)) {
                return BitcoinScriptType.PayToPubkey;
            }

            if (isPayToPubkeyHash(validationWords)) {
                return BitcoinScriptType.PayToPubkeyHash;
            }
        }

        // Didn't find a match
        return BitcoinScriptType.Unknown;
    }

    private boolean isPayToPubkey(List<Word> validationWords) {
        if (!hasEnoughWords(3, validationWords)) return false;

        Word firstWord = validationWords.get(0);
        Word secondWord = validationWords.get(1);
        Word thirdWord = validationWords.get(2);

        if (!isPushOperation(firstWord)) {
            return false;
        }

        if (!isPushOperation(secondWord)) {
            return false;
        }

        if (!(thirdWord instanceof OpCheckSig)) {
            return false;
        }

        return true;
    }

    private boolean isPayToPubkeyHash(List<Word> validationWords) {
        if (!hasEnoughWords(7, validationWords)) return false;

        Word firstWord = validationWords.get(0);
        Word secondWord = validationWords.get(1);
        Word thirdWord = validationWords.get(2);
        Word fourthWord = validationWords.get(3);
        Word fifthWord = validationWords.get(4);
        Word sixthWord = validationWords.get(5);
        Word seventhWord = validationWords.get(6);

        if (!isPushOperation(firstWord)) {
            return false;
        }

        if (!isPushOperation(secondWord)) {
            return false;
        }

        if (!(thirdWord instanceof OpDup)) {
            return false;
        }

        if (!(fourthWord instanceof OpHash160)) {
            return false;
        }

        if (!isPushOperation(fifthWord)) {
            return false;
        }

        if (!(sixthWord instanceof OpEqualVerify)) {
            return false;
        }

        if (!(seventhWord instanceof OpCheckSig)) {
            return false;
        }

        return true;
    }

    private boolean isAnyoneCanSpend(List<Word> inputWords) {
        if (!hasEnoughWords(1, inputWords)) return false;

        if (!(inputWords.get(0) instanceof OpTrue)) {
            return false;
        }

        return true;
    }

    private boolean isTransactionPuzzle(List<Word> outputWords) {
        if (!hasEnoughWords(3, outputWords)) return false;

        Word firstWord = outputWords.get(0);
        Word secondWord = outputWords.get(1);
        Word thirdWord = outputWords.get(2);

        if (!(firstWord instanceof OpHash256)) {
            return false;
        }

        if (!isPushOperation(secondWord)) {
            return false;
        }

        if (!(thirdWord instanceof OpEqual)) {
            return false;
        }

        return true;
    }

    private boolean isProvablyUnspendable(List<Word> outputWords) {
        if (!hasEnoughWords(1, outputWords)) return false;

        if (!(outputWords.get(0) instanceof OpReturn)) {
            return false;
        }

        return true;
    }

    private boolean hasEnoughWords(int requiredWords, List<Word> outputWords) {
        if (outputWords.size() < requiredWords) {
            return false;
        }

        return true;
    }

    private boolean isPushOperation(Word word) {
        if (!(word instanceof VirtualOpPush) &&
                !(word instanceof OpPushData1) &&
                !(word instanceof OpPushData2) &&
                !(word instanceof OpPushData4)) {
            return false;
        }

        return true;
    }
}
