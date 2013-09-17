package com.timmattison.cryptocurrency.factories;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/5/13
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
public interface StateMachineFactory {
    StateMachine createStateMachine();
}
