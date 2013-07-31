package com.timmattison.bitcoin.test.script;

import com.timmattison.bitcoin.test.script.words.constants.OpFalse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/9/13
 * Time: 7:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptHelper {
    public static List<Word> getEmptyWordList() {
        List<Word> returnValue = new ArrayList<Word>();
        returnValue.add(new OpFalse());
        return returnValue;
    }

    public static byte[] getEmptyScriptBytes() {
        byte[] returnValue = new byte[1];

        returnValue[0] = new OpFalse().getOpcode();

        return returnValue;
    }
}
