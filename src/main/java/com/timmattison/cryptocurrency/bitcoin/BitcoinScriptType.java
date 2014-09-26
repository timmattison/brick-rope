package com.timmattison.cryptocurrency.bitcoin;

/**
 * Created by timmattison on 9/26/14.
 */
public enum BitcoinScriptType {
    PayToPubkey("Pay to public key [obsolete]"),
    PayToPubkeyHash("Pay to public key hash"),
    AnyoneCanSpend("Anyone can spend"),
    TransactionPuzzle("Transaction puzzle"),
    ProvablyUnspendable("Transaction puzzle"),
    Unknown("Unknown");

    private final String description;

    BitcoinScriptType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
