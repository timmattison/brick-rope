package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.interfaces.Buildable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Script extends Buildable {
    boolean isExecutable();

    List<Word> getWords();
}
