package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.factories.WordFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 8:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinWordFactory implements WordFactory {
    @Override
    public Word createWord(byte opcode) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
