package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.Inject;
import com.timmattison.crypto.ecc.interfaces.ECCNamedCurve;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.cryptocurrency.factories.ECCParamsFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/13/13
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinECCParamsFactory implements ECCParamsFactory {
    private final ECCNamedCurve namedCurve;

    @Inject
    public BitcoinECCParamsFactory(ECCNamedCurve namedCurve) {
        this.namedCurve = namedCurve;
    }
    @Override
    public ECCParameters create() {
        return namedCurve.getSecp256k1();
    }
}
