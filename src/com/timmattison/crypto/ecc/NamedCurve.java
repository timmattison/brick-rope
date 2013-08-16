package com.timmattison.crypto.ecc;

import javax.inject.Inject;
import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 8:23 AM
 * To change this template use File | Settings | File Templates.
 */
public interface NamedCurve {
    public ECCParameters getSecp128r1();

    public ECCParameters getSecp160k1();

    public ECCParameters getSecp160r1();

    public ECCParameters getSecp192k1();

    public ECCParameters getSecp192r1();

    public ECCParameters getSecp224r1();

    public ECCParameters getSecp256r1();

    public ECCParameters getSecp256k1();
}
