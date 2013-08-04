package com.timmattison.cryptocurrency.factories;

import com.timmattison.cryptocurrency.interfaces.Block;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockFactory {
    Block createBlock() throws IOException;
}
