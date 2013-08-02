package com.timmattison.cryptocurrency.interfaces;

import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.standard.OutputScript;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 7:20 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ScriptFactory {
    InputScript createInputScript(InputStream inputStream, long scriptLength, boolean coinbase);

    OutputScript createOutputScript(InputStream inputStream, long scriptLength);
}
