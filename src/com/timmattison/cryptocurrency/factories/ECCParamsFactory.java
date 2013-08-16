package com.timmattison.cryptocurrency.factories;

import com.timmattison.crypto.ecc.ECCParameters;
import com.timmattison.crypto.ecc.fp.X9ECParameters;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/13/13
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCParamsFactory {
    public ECCParameters create();
}
