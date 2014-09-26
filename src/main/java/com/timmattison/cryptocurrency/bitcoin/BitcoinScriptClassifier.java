package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.standard.interfaces.InputScript;
import com.timmattison.cryptocurrency.standard.interfaces.OutputScript;

/**
 * Created by timmattison on 9/26/14.
 */
public interface BitcoinScriptClassifier {
    public BitcoinScriptType determineScriptType(InputScript inputScript, OutputScript outputScript);
}
