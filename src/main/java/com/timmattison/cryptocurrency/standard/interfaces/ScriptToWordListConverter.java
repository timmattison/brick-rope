package com.timmattison.cryptocurrency.standard.interfaces;

import com.timmattison.cryptocurrency.bitcoin.Word;

import java.util.List;

/**
 * Created by timmattison on 9/26/14.
 */
public interface ScriptToWordListConverter {
    public List<Word> convert(Script script);
}
