package com.timmattison.cryptocurrency.interfaces;

import java.io.InputStream;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 7:30 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockChain extends Iterator<Block> {
    public void setInputStream(InputStream inputStream);
}
