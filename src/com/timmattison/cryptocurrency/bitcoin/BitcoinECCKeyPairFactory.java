package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.crypto.ecc.fp.ECKeyPairFp;
import com.timmattison.crypto.ecc.fp.ECParametersFp;
import com.timmattison.cryptocurrency.factories.ECCKeyPairFactory;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/13/13
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinECCKeyPairFactory implements ECCKeyPairFactory {
    @Override
    public ECKeyPairFp create(ECParametersFp parameters, BigInteger d) {
        //To change body of implemented methods use File | Settings | File Templates.
        throw new UnsupportedOperationException();
    }
}
