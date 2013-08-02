package com.timmattison.cryptocurrency.bitcoin;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 9:14 AM
 * To change this template use File | Settings | File Templates.
 */
public interface StateMachine {
    public Object pop();

    public void push(Object object);

    public Object peek();
}
