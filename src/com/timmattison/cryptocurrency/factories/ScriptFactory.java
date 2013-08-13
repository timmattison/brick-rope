package com.timmattison.cryptocurrency.factories;

import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.standard.OutputScript;
import com.timmattison.cryptocurrency.standard.Script;
import com.timmattison.cryptocurrency.standard.ValidationScript;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 7:20 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ScriptFactory {
    InputScript createInputScript(int transactionVersionNumber, long scriptLength, boolean coinbase);

    OutputScript createOutputScript(int transactionVersionNumber, long scriptLength);

    ValidationScript createValidationScript(Script inputScript, Script outputScript);
}
