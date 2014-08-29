package com.timmattison.cryptocurrency.factories;

import com.google.inject.assistedinject.Assisted;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/10/13
 * Time: 7:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockStorageFactory {
    BlockStorage getBlockStorage();
}
