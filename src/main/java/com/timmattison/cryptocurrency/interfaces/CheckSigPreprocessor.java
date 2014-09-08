package com.timmattison.cryptocurrency.interfaces;

import com.timmattison.cryptocurrency.bitcoin.HashType;

/**
 * Created by timmattison on 9/10/14.
 */
public interface CheckSigPreprocessor {
    // TODO - Make BitcoinHashType a more generic HashType
    byte[] preprocessTransactionData(byte[] previousTransactionHash, byte[] currentTransactionHash, int previousOutputIndex, int inputNumber, HashType hashType);
}
