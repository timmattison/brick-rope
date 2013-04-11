package com.timmattison.bitcoin.test;

import com.timmattison.bitcoin.test.script.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class Script {
    private List<Byte> scriptByteList = new ArrayList<Byte>();
    private List<Word> words;

    public Script(Byte[] scriptBytes) {
        scriptByteList = Arrays.asList(scriptBytes);

        buildListOfWords();
    }

    private void buildListOfWords() {
        words = new ArrayList<Word>();

        while(!scriptByteList.isEmpty()) {
            // Consume the bytes
        }
    }
}
