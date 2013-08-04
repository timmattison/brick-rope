package com.timmattison.cryptocurrency.bitcoin;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinStateMachine implements StateMachine {
    Stack<Object> stack;

    public Object pop() {
        throwExceptionIfStackNotInitialized();
        return stack.pop();
    }

    public void push(Object input) {
        if (stack == null) {
            stack = new Stack<Object>();
        }

        stack.push(input);
    }

    public Object peek() {
        throwExceptionIfStackNotInitialized();
        return stack.peek();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    private void throwExceptionIfStackNotInitialized() {
        if (stack == null) {
            throw new IllegalStateException("Stack has not been initialzied");
        }
    }
}
