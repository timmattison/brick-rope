package com.timmattison.cryptocurrency.factories;

import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/15/13
 * Time: 6:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SignatureProcessorFactory {
    SignatureProcessor create();
}
