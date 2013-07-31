package com.timmattison.cryptocurrency.interfaces;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 7:07 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockChainPart<T> extends Bitstream {
    public T build(InputStream inputStream);
}
