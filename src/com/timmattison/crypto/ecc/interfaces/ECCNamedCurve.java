package com.timmattison.crypto.ecc.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 8:23 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCNamedCurve {
    public ECCParameters getSecp128r1();

    public ECCParameters getSecp160k1();

    public ECCParameters getSecp160r1();

    public ECCParameters getSect163k1();

    public ECCParameters getSecp192k1();

    public ECCParameters getSecp192r1();

    public ECCParameters getSecp224r1();

    public ECCParameters getSecp256r1();

    public ECCParameters getSecp256k1();
}
