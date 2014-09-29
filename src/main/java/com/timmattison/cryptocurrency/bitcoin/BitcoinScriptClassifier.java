package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.standard.interfaces.Script;

/**
 * Created by timmattison on 9/26/14.
 */
public interface BitcoinScriptClassifier {
    public BitcoinScriptType determineScriptType(boolean isCoinbase, Script inputScript, Script outputScript);
}
