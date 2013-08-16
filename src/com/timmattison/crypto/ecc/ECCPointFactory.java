package com.timmattison.crypto.ecc;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/16/13
 * Time: 7:03 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCPointFactory {
    ECCPoint create(ECCCurve eccCurve, ECCFieldElement p, ECCFieldElement a, ECCFieldElement b);
}
