package com.timmattison.cryptocurrency.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockValidator {
    boolean hasValidHeaders(Block block);

    boolean isValid(Block block, long blockNumber);

    boolean isParentOf(Block parentBlock, Block childBlock);
}
