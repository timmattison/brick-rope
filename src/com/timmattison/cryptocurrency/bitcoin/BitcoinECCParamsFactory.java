package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.ecc.fp.SECNamedCurves;
import com.timmattison.cryptocurrency.ecc.fp.X9ECParameters;
import com.timmattison.cryptocurrency.factories.ECCParamsFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/13/13
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinECCParamsFactory implements ECCParamsFactory {
    @Override
    public X9ECParameters create() {
        return SECNamedCurves.getSecp256k1();
    }
}
