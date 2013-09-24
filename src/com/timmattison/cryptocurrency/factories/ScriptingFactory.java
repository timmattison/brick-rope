package com.timmattison.cryptocurrency.factories;

import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.standard.OutputScript;
import com.timmattison.cryptocurrency.standard.Script;
import com.timmattison.cryptocurrency.standard.ValidationScript;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/5/13
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ScriptingFactory {
    Word createWord(byte opcode);

    InputScript createInputScript(int transactionVersionNumber, long scriptLength, boolean coinbase);

    OutputScript createOutputScript(int transactionVersionNumber, long scriptLength);

    ValidationScript createValidationScript(Script inputScript, Script outputScript);
}
