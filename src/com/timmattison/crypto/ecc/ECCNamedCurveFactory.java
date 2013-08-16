package com.timmattison.crypto.ecc;

import com.google.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/16/13
 * Time: 8:21 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCNamedCurveFactory {
    public NamedCurve create();
}
