package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinInputScript;
import com.timmattison.cryptocurrency.bitcoin.BitcoinOutputScript;
import com.timmattison.cryptocurrency.bitcoin.BitcoinValidationScript;
import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.bitcoin.words.stack.VirtualOpPush;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.standard.interfaces.InputScript;
import com.timmattison.cryptocurrency.standard.interfaces.OutputScript;
import com.timmattison.cryptocurrency.standard.interfaces.Script;
import com.timmattison.cryptocurrency.standard.interfaces.ValidationScript;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 8:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinScriptingFactory implements ScriptingFactory {
    /**
     * These are the words mapped by their opcode
     */
    private static Map<Byte, Word> wordsByOpcode;
    /**
     * These are the words mapped by their name
     */
    private static HashMap<String, Word> wordsByName;

    private final Logger logger;
    private final Set<Word> words;

    @Inject
    public BitcoinScriptingFactory(Logger logger, Set<Word> words) throws InstantiationException, IllegalAccessException {
        this.logger = logger;
        this.words = words;

        if (wordsByOpcode == null) {
            createOpcodeLookupTable();
        }

        if (wordsByName == null) {
            createNameLookupTable();
        }
    }

    private void createOpcodeLookupTable() throws IllegalAccessException, InstantiationException {
        wordsByOpcode = new HashMap<Byte, Word>();

        for (Word word : words) {
            wordsByOpcode.put(word.getOpcode(), word);
        }
    }

    private void createNameLookupTable() throws IllegalAccessException, InstantiationException {
        wordsByName = new HashMap<String, Word>();

        for (Word word : words) {
            wordsByName.put(word.getName(), word);
        }
    }

    @Override
    public Word createWord(byte opcode) {
        return getWordByOpcode(opcode);
    }

    private Word getWordByOpcode(byte opcode) {
        if ((opcode >= 0x01) && (opcode <= 0x4B)) {
            return new VirtualOpPush(opcode);
        }

        // See if this class is a no argument class
        Word word = wordsByOpcode.get(opcode);

        // Did we find it?
        if (word != null) {
            // Yes, return it
            return word;
        }

        // Didn't find it, throw an exception
        throw new UnsupportedOperationException("No word found for opcode " + opcode + " [" + ByteArrayHelper.toHex(opcode) + "]");
    }

    @Override
    public InputScript createInputScript(int transactionVersionNumber, long scriptLength, boolean coinbase) {
        return new BitcoinInputScript(this, transactionVersionNumber, scriptLength, coinbase);
    }

    @Override
    public OutputScript createOutputScript(int transactionVersionNumber, long scriptLength) {
        return new BitcoinOutputScript(this, transactionVersionNumber, scriptLength);
    }

    @Override
    public ValidationScript createValidationScript(Script inputScript, Script outputScript) {
        ValidationScript validationScript = new BitcoinValidationScript(this, inputScript.dump().length + outputScript.dump().length);
        validationScript.build(ByteArrayHelper.concatenate(inputScript.dump(), outputScript.dump()));

        return validationScript;
    }
}
