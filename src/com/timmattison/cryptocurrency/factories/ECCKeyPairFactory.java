package com.timmattison.cryptocurrency.factories;

import com.timmattison.crypto.ecc.fp.ECKeyPairFp;
import com.timmattison.crypto.ecc.fp.ECParametersFp;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/14/13
 * Time: 6:56 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCKeyPairFactory {
    ECKeyPairFp create(ECParametersFp parameters, BigInteger d);
}
