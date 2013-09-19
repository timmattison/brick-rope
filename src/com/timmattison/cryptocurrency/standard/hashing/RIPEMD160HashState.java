package com.timmattison.cryptocurrency.standard.hashing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/16/13
 * Time: 6:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class RIPEMD160HashState implements HashState {
    private NamedValue<Integer> inputALeft = new NamedValue<Integer>("Input A [left]", 0);
    private NamedValue<Integer> inputBLeft = new NamedValue<Integer>("Input B [left]", 0);
    private NamedValue<Integer> inputCLeft = new NamedValue<Integer>("Input C [left]", 0);
    private NamedValue<Integer> inputDLeft = new NamedValue<Integer>("Input D [left]", 0);
    private NamedValue<Integer> inputELeft = new NamedValue<Integer>("Input E [left]", 0);
    private NamedValue<Integer> inputARight = new NamedValue<Integer>("Input A [right]", 0);
    private NamedValue<Integer> inputBRight = new NamedValue<Integer>("Input B [right]", 0);
    private NamedValue<Integer> inputCRight = new NamedValue<Integer>("Input C [right]", 0);
    private NamedValue<Integer> inputDRight = new NamedValue<Integer>("Input D [right]", 0);
    private NamedValue<Integer> inputERight = new NamedValue<Integer>("Input E [right]", 0);
    private NamedValue<Integer> tValue = new NamedValue<Integer>("T", 0);
    private NamedValue<Integer> h0Value = new NamedValue<Integer>("h0", 0);
    private NamedValue<Integer> h1Value = new NamedValue<Integer>("h1", 0);
    private NamedValue<Integer> h2Value = new NamedValue<Integer>("h2", 0);
    private NamedValue<Integer> h3Value = new NamedValue<Integer>("h3", 0);
    private NamedValue<Integer> h4Value = new NamedValue<Integer>("h4", 0);
    private NamedValue<Integer> roundNumber = new NamedValue<Integer>("Round number", 0);
    private boolean finished = false;
    private Integer h0;

    @Override
    public List<NamedValue> getState() {
        List<NamedValue> returnValue = new ArrayList<NamedValue>();

        returnValue.add(inputALeft);
        returnValue.add(inputBLeft);
        returnValue.add(inputCLeft);
        returnValue.add(inputDLeft);
        returnValue.add(inputELeft);

        returnValue.add(inputARight);
        returnValue.add(inputBRight);
        returnValue.add(inputCRight);
        returnValue.add(inputDRight);
        returnValue.add(inputERight);

        returnValue.add(tValue);

        returnValue.add(h0Value);
        returnValue.add(h1Value);
        returnValue.add(h2Value);
        returnValue.add(h3Value);
        returnValue.add(h4Value);

        returnValue.add(roundNumber);

        return returnValue;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    public NamedValue<Integer> getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber.setValue(roundNumber);
    }

    public NamedValue<Integer> getT() {
        return tValue;
    }

    public void setT(int tValue) {
        this.tValue.setValue(tValue);
    }

    public void setInputALeft(Integer inputALeft) {
        this.inputALeft.setValue(inputALeft);
    }

    public NamedValue<Integer> getInputALeft() {
        return inputALeft;
    }

    public void setInputBLeft(Integer inputBLeft) {
        this.inputBLeft.setValue(inputBLeft);
    }

    public NamedValue<Integer> getInputBLeft() {
        return inputBLeft;
    }

    public void setInputCLeft(Integer inputCLeft) {
        this.inputCLeft.setValue(inputCLeft);
    }

    public NamedValue<Integer> getInputCLeft() {
        return inputCLeft;
    }

    public void setInputDLeft(Integer inputDLeft) {
        this.inputDLeft.setValue(inputDLeft);
    }

    public NamedValue<Integer> getInputDLeft() {
        return inputDLeft;
    }

    public void setInputELeft(Integer inputELeft) {
        this.inputELeft.setValue(inputELeft);
    }

    public NamedValue<Integer> getInputELeft() {
        return inputELeft;
    }

    public void setInputARight(Integer inputARight) {
        this.inputARight.setValue(inputARight);
    }

    public NamedValue<Integer> getInputARight() {
        return inputARight;
    }

    public void setInputBRight(Integer inputBRight) {
        this.inputBRight.setValue(inputBRight);
    }

    public NamedValue<Integer> getInputBRight() {
        return inputBRight;
    }

    public void setInputCRight(Integer inputCRight) {
        this.inputCRight.setValue(inputCRight);
    }

    public NamedValue<Integer> getInputCRight() {
        return inputCRight;
    }

    public void setInputDRight(Integer inputDRight) {
        this.inputDRight.setValue(inputDRight);
    }

    public NamedValue<Integer> getInputDRight() {
        return inputDRight;
    }

    public void setInputERight(Integer inputERight) {
        this.inputERight.setValue(inputERight);
    }

    public NamedValue<Integer> getInputERight() {
        return inputERight;
    }

    public void setH0(Integer h0) {
        this.h0Value.setValue(h0);
    }

    public void setH1(Integer h1) {
        this.h1Value.setValue(h1);
    }

    public void setH2(Integer h2) {
        this.h2Value.setValue(h2);
    }

    public void setH3(Integer h3) {
        this.h3Value.setValue(h3);
    }

    public void setH4(Integer h4) {
        this.h4Value.setValue(h4);
    }

    public NamedValue<Integer> getH0() {
        return this.h0Value;
    }

    public NamedValue<Integer> getH1() {
        return this.h1Value;
    }

    public NamedValue<Integer> getH2() {
        return this.h2Value;
    }

    public NamedValue<Integer> getH3() {
        return this.h3Value;
    }

    public NamedValue<Integer> getH4() {
        return this.h4Value;
    }
}
