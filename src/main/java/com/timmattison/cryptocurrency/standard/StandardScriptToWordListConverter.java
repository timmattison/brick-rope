package com.timmattison.cryptocurrency.standard;

import com.google.inject.Inject;
import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.standard.interfaces.Script;
import com.timmattison.cryptocurrency.standard.interfaces.ScriptToWordListConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by timmattison on 9/26/14.
 */
public class StandardScriptToWordListConverter implements ScriptToWordListConverter {
    private final ScriptingFactory scriptingFactory;

    @Inject
    public StandardScriptToWordListConverter(ScriptingFactory scriptingFactory) {
        this.scriptingFactory = scriptingFactory;
    }

    @Override
    public List<Word> convert(Script script) {
        // Get a copy of the script
        byte[] scriptData = Arrays.copyOf(script.dump(), script.dump().length);

        // Is this script executable?
        if (!script.isExecutable()) {
            // No, just return
            return null;
        }

        List<Word> output = new ArrayList<Word>();

        do {
            // Build the next word
            byte currentByte = scriptData[0];

            // Get the word that the next byte corresponds to
            Word currentWord = scriptingFactory.createWord(currentByte);

            // Build the current word and get the remaining script data
            scriptData = currentWord.build(Arrays.copyOfRange(scriptData, 1, scriptData.length));

            // Add the current word to the output
            output.add(currentWord);
        } while ((scriptData != null) && (scriptData.length > 0));

        // Return the output
        return output;
    }
}
