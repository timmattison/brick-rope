package com.timmattison.cryptocurrency.standard.hashing.ripemd;

import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.standard.hashing.Endianness;
import com.timmattison.cryptocurrency.standard.hashing.HashFunction;
import com.timmattison.cryptocurrency.standard.hashing.HashState;
import com.timmattison.cryptocurrency.standard.hashing.HashStateListener;
import com.timmattison.cryptocurrency.standard.hashing.chunks.ChunkExtractor;
import com.timmattison.cryptocurrency.standard.hashing.padding.MessagePadder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/15/13
 * Time: 7:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class RIPEMD160 implements HashFunction {
    private static final int numberOfRounds = 80;
    /**
     * From RIPEMD-160 section 3.1 4 and appendix A
     */
    private static final int[] leftShifts = new int[]{
            11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
            7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
            11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
            11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12,
            9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6
    };
    /**
     * From RIPEMD-160 section 3.1 4 and appendix A
     */
    private static final int[] rightShifts = new int[]{
            8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
            9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
            9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
            15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8,
            8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11
    };
    /**
     * From RIPEMD-160 section 3.1 2 and appendix A
     */
    private static final int[] leftPermutations = new int[]{
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
            3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
            1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2,
            4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13};
    /**
     * From RIPEMD-160 section 3.1 2 and appendix A
     */
    private static final int[] rightPermutations = new int[]{
            5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
            6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
            15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
            8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14,
            12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11
    };
    /**
     * From RIPEMD-160 section 3.1 5 and appendix A
     */
    private static int[] leftConstants = new int[]{
            0,
            0x5a827999, //(int) ((double) (2 << 30) * (double) Math.sqrt(2));
            0x6ed9eba1, //(int) ((double) (2 << 30) * (double) Math.sqrt(3));
            0x8f1bbcdc, //(int) ((double) (2 << 30) * (double) Math.sqrt(5));
            0xa953fd4e //(int) ((double) (2 << 30) * (double) Math.sqrt(7));
    };
    /**
     * From RIPEMD-160 section 3.1 5 and appendix A
     */
    private static int[] rightConstants = new int[]{
            0x50a28be6, //(int) ((double) (2 << 30) * (double) Math.cbrt(2));
            0x5c4dd124, //(int) ((double) (2 << 30) * (double) Math.cbrt(3));
            0x6d703ef3, //(int) ((double) (2 << 30) * (double) Math.cbrt(5));
            0x7a6d76e9, //(int) ((double) (2 << 30) * (double) Math.cbrt(7));
            0};
    /**
     * Constants
     */
    private static int target = 448;
    private static int modulus = 512;
    private static int sizeOfLengthInBits = 64;
    private static int sizeOfChunkInBytes = 64;
    private static int numberOfWords = 16;
    private RIPEMD160HashState ripemd160HashState;
    private List<HashStateListener> hashStateListeners = new ArrayList<HashStateListener>();
    private List<byte[]> chunks;
    private Iterator<byte[]> chunkIterator;
    private byte[] currentChunk;
    private String leftAHex;
    private String leftBHex;
    private String leftCHex;
    private String leftDHex;
    private String leftEHex;
    private String rightAHex;
    private String rightBHex;
    private String rightCHex;
    private String rightDHex;
    private String rightEHex;

    private final Endianness endianness = Endianness.LittleEndian;

    private final MessagePadder messagePadder;
    private final ChunkExtractor chunkExtractor;

    public RIPEMD160(MessagePadder messagePadder, ChunkExtractor chunkExtractor) {
        this.messagePadder = messagePadder;
        this.chunkExtractor = chunkExtractor;
    }

    private static int getIndex(int roundNumber) {
        int index = roundNumber / 16;

        if (index >= 5) {
            throw new UnsupportedOperationException("Round number is out of range [" + roundNumber + "]");
        }

        return index;
    }

    private static int getConstant(int roundNumber, RIPEMD160LeftRight leftRight) {
        int index = getIndex(roundNumber);

        if (leftRight == RIPEMD160LeftRight.Left) {
            return leftConstants[index];
        } else {
            return rightConstants[index];
        }
    }

    @Override
    public void initialize(byte[] input, int messageBitLength) {
        ripemd160HashState = new RIPEMD160HashState();

        // Initialize A, B, C, D, and E
        ripemd160HashState.setH0(0x67452301);
        ripemd160HashState.setH1(0xEFCDAB89);
        ripemd160HashState.setH2(0x98BADCFE);
        ripemd160HashState.setH3(0x10325476);
        ripemd160HashState.setH4(0xC3D2E1F0);

        ripemd160HashState.setInputALeft(ripemd160HashState.getH0().getValue());
        ripemd160HashState.setInputBLeft(ripemd160HashState.getH1().getValue());
        ripemd160HashState.setInputCLeft(ripemd160HashState.getH2().getValue());
        ripemd160HashState.setInputDLeft(ripemd160HashState.getH3().getValue());
        ripemd160HashState.setInputELeft(ripemd160HashState.getH4().getValue());

        ripemd160HashState.setInputARight(ripemd160HashState.getH0().getValue());
        ripemd160HashState.setInputBRight(ripemd160HashState.getH1().getValue());
        ripemd160HashState.setInputCRight(ripemd160HashState.getH2().getValue());
        ripemd160HashState.setInputDRight(ripemd160HashState.getH3().getValue());
        ripemd160HashState.setInputERight(ripemd160HashState.getH4().getValue());

        // Pad the message
        input = messagePadder.pad(endianness, input, messageBitLength);

        // Create the list of chunks
        // Create the list of chunks
        chunks = chunkExtractor.getChunks(input, messagePadder.getBitLengthWithPadding(), messagePadder.getModulus());

        chunkIterator = chunks.iterator();
        currentChunk = chunkIterator.next();

        ripemd160HashState.setRoundNumber(0);
    }

    private byte[] getCurrentChunk() {
        return currentChunk;
    }

    private void moveToNextChunk() {
        if (chunkIterator.hasNext()) {
            currentChunk = chunkIterator.next();
        } else {
            currentChunk = null;
        }

        ripemd160HashState.setRoundNumber(0);
    }

    @Override
    public void step() {
        try {
            // Are we already finished?
            if (ripemd160HashState.isFinished()) {
                // Yes, this should never happen
                throw new UnsupportedOperationException("Hash is already finished, cannot complete another step");
            }

            // Allocate enough space for the 16 words
            int[] words = new int[numberOfWords];
            String[] wordStrings = new String[numberOfWords];

            // Extract 16 32-bit words
            for (int loop = 0; loop < numberOfWords; loop++) {
                words[loop] = ByteArrayHelper.get32BitWord(getCurrentChunk(), loop);
                wordStrings[loop] = Integer.toHexString(words[loop]);
            }

            if (ripemd160HashState.getRoundNumber().getValue() >= numberOfRounds) {
                throw new UnsupportedOperationException("Loop is greater than " + numberOfRounds);
            }

            updateLeftAndRightValues();

            int roundNumber = ripemd160HashState.getRoundNumber().getValue();
            ripemd160HashState.setT(leftRotate(ripemd160HashState.getInputALeft().getValue() + applyFunctionForRound(roundNumber, ripemd160HashState.getInputBLeft().getValue(), ripemd160HashState.getInputCLeft().getValue(), ripemd160HashState.getInputDLeft().getValue()) + words[leftPermutations[roundNumber]] + getConstant(roundNumber, RIPEMD160LeftRight.Left), leftShifts[roundNumber]) + ripemd160HashState.getInputELeft().getValue());
            String leftT = ripemd160HashState.getT().getHexValue();
            String leftFbcd = Integer.toHexString(applyFunctionForRound(roundNumber, ripemd160HashState.getInputBLeft().getValue(), ripemd160HashState.getInputCLeft().getValue(), ripemd160HashState.getInputDLeft().getValue()));
            String leftWord = Integer.toHexString(words[leftPermutations[roundNumber]]);
            String leftConstant = Integer.toHexString(getConstant(roundNumber, RIPEMD160LeftRight.Left));
            String leftShift = Integer.toHexString(leftShifts[roundNumber]);
            String leftPermutation = Integer.toHexString(leftPermutations[roundNumber]);
            String leftTBeforeShift = Integer.toHexString(ripemd160HashState.getInputALeft().getValue() + applyFunctionForRound(roundNumber, ripemd160HashState.getInputBLeft().getValue(), ripemd160HashState.getInputCLeft().getValue(), ripemd160HashState.getInputDLeft().getValue()) + words[leftPermutations[roundNumber]] + getConstant(roundNumber, RIPEMD160LeftRight.Left));
            String leftTAfterShift = Integer.toHexString(leftRotate(ripemd160HashState.getInputALeft().getValue() + applyFunctionForRound(roundNumber, ripemd160HashState.getInputBLeft().getValue(), ripemd160HashState.getInputCLeft().getValue(), ripemd160HashState.getInputDLeft().getValue()) + words[leftPermutations[roundNumber]] + getConstant(roundNumber, RIPEMD160LeftRight.Left), leftShifts[roundNumber]));

            ripemd160HashState.setInputALeft(ripemd160HashState.getInputELeft().getValue());
            updateLeftAndRightValues();
            ripemd160HashState.setInputELeft(ripemd160HashState.getInputDLeft().getValue());
            updateLeftAndRightValues();
            ripemd160HashState.setInputDLeft(leftRotate(ripemd160HashState.getInputCLeft().getValue(), 10));
            updateLeftAndRightValues();
            ripemd160HashState.setInputCLeft(ripemd160HashState.getInputBLeft().getValue());
            updateLeftAndRightValues();
            ripemd160HashState.setInputBLeft(ripemd160HashState.getT().getValue());
            updateLeftAndRightValues();

            ripemd160HashState.setT(leftRotate(ripemd160HashState.getInputARight().getValue() + applyFunctionForRound(79 - roundNumber, ripemd160HashState.getInputBRight().getValue(), ripemd160HashState.getInputCRight().getValue(), ripemd160HashState.getInputDRight().getValue()) + words[rightPermutations[roundNumber]] + getConstant(roundNumber, RIPEMD160LeftRight.Right), rightShifts[roundNumber]) + ripemd160HashState.getInputERight().getValue());
            updateLeftAndRightValues();

            String rightT = ripemd160HashState.getT().getHexValue();
            String rightFbcd = Integer.toHexString(applyFunctionForRound(79 - roundNumber, ripemd160HashState.getInputBRight().getValue(), ripemd160HashState.getInputCRight().getValue(), ripemd160HashState.getInputDRight().getValue()));
            String rightWord = Integer.toHexString(words[rightPermutations[roundNumber]]);
            String rightConstant = Integer.toHexString(getConstant(roundNumber, RIPEMD160LeftRight.Right));
            String rightShift = Integer.toHexString(rightShifts[roundNumber]);
            String rightPermutation = Integer.toHexString(rightPermutations[roundNumber]);
            String rightTBeforeShift = Integer.toHexString(ripemd160HashState.getInputARight().getValue() + applyFunctionForRound(79 - roundNumber, ripemd160HashState.getInputBRight().getValue(), ripemd160HashState.getInputCRight().getValue(), ripemd160HashState.getInputDRight().getValue()) + words[rightPermutations[roundNumber]] + getConstant(roundNumber, RIPEMD160LeftRight.Right));
            String rightTAfterShift = Integer.toHexString(leftRotate(ripemd160HashState.getInputARight().getValue() + applyFunctionForRound(79 - roundNumber, ripemd160HashState.getInputBRight().getValue(), ripemd160HashState.getInputCRight().getValue(), ripemd160HashState.getInputDRight().getValue()) + words[rightPermutations[roundNumber]] + getConstant(roundNumber, RIPEMD160LeftRight.Right), leftShifts[roundNumber]));

            ripemd160HashState.setInputARight(ripemd160HashState.getInputERight().getValue());
            updateLeftAndRightValues();
            ripemd160HashState.setInputERight(ripemd160HashState.getInputDRight().getValue());
            updateLeftAndRightValues();
            ripemd160HashState.setInputDRight(leftRotate(ripemd160HashState.getInputCRight().getValue(), 10));
            updateLeftAndRightValues();
            ripemd160HashState.setInputCRight(ripemd160HashState.getInputBRight().getValue());
            updateLeftAndRightValues();
            ripemd160HashState.setInputBRight(ripemd160HashState.getT().getValue());
            updateLeftAndRightValues();

            updateLeftAndRightValues();

            sendHashStateListenerEvent("Updated for next step");

            // Go to the next round
            ripemd160HashState.setRoundNumber(ripemd160HashState.getRoundNumber().getValue() + 1);

            // Are we at the last step?
            if (ripemd160HashState.getRoundNumber().getValue() == numberOfRounds) {
                // Yes, move to the next chunk
                moveToNextChunk();

                // Update the intermediate values and the outputs
                ripemd160HashState.setT(ripemd160HashState.getH1().getValue() + ripemd160HashState.getInputCLeft().getValue() + ripemd160HashState.getInputDRight().getValue());
                ripemd160HashState.setH1(ripemd160HashState.getH2().getValue() + ripemd160HashState.getInputDLeft().getValue() + ripemd160HashState.getInputERight().getValue());
                ripemd160HashState.setH2(ripemd160HashState.getH3().getValue() + ripemd160HashState.getInputELeft().getValue() + ripemd160HashState.getInputARight().getValue());
                ripemd160HashState.setH3(ripemd160HashState.getH4().getValue() + ripemd160HashState.getInputALeft().getValue() + ripemd160HashState.getInputBRight().getValue());
                ripemd160HashState.setH4(ripemd160HashState.getH0().getValue() + ripemd160HashState.getInputBLeft().getValue() + ripemd160HashState.getInputCRight().getValue());
                ripemd160HashState.setH0(ripemd160HashState.getT().getValue());

                ripemd160HashState.setInputALeft(ripemd160HashState.getH0().getValue());
                ripemd160HashState.setInputBLeft(ripemd160HashState.getH1().getValue());
                ripemd160HashState.setInputCLeft(ripemd160HashState.getH2().getValue());
                ripemd160HashState.setInputDLeft(ripemd160HashState.getH3().getValue());
                ripemd160HashState.setInputELeft(ripemd160HashState.getH4().getValue());

                ripemd160HashState.setInputARight(ripemd160HashState.getH0().getValue());
                ripemd160HashState.setInputBRight(ripemd160HashState.getH1().getValue());
                ripemd160HashState.setInputCRight(ripemd160HashState.getH2().getValue());
                ripemd160HashState.setInputDRight(ripemd160HashState.getH3().getValue());
                ripemd160HashState.setInputERight(ripemd160HashState.getH4().getValue());

                // Is the current chunk NULL?
                if (getCurrentChunk() == null) {
                    // Yes, we are done
                    ripemd160HashState.setFinished(true);
                    sendHashStateListenerEvent("Finished");
                }
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private void updateLeftAndRightValues() {
        leftAHex = ripemd160HashState.getInputALeft().getHexValue();
        leftBHex = ripemd160HashState.getInputBLeft().getHexValue();
        leftCHex = ripemd160HashState.getInputCLeft().getHexValue();
        leftDHex = ripemd160HashState.getInputDLeft().getHexValue();
        leftEHex = ripemd160HashState.getInputELeft().getHexValue();

        rightAHex = ripemd160HashState.getInputARight().getHexValue();
        rightBHex = ripemd160HashState.getInputBRight().getHexValue();
        rightCHex = ripemd160HashState.getInputCRight().getHexValue();
        rightDHex = ripemd160HashState.getInputDRight().getHexValue();
        rightEHex = ripemd160HashState.getInputERight().getHexValue();

        /*
        leftA = ripemd160HashState.getInputALeft().getValue();
        leftB = ripemd160HashState.getInputBLeft().getValue();
        leftC = ripemd160HashState.getInputCLeft().getValue();
        leftD = ripemd160HashState.getInputDLeft().getValue();
        leftE = ripemd160HashState.getInputELeft().getValue();

        rightA = ripemd160HashState.getInputARight().getValue();
        rightB = ripemd160HashState.getInputBRight().getValue();
        rightC = ripemd160HashState.getInputCRight().getValue();
        rightD = ripemd160HashState.getInputDRight().getValue();
        rightE = ripemd160HashState.getInputERight().getValue();
        */

        int a = 5;
        a++;
    }

    @Override
    public String getOutput() {
        StringBuilder stringBuilder = new StringBuilder();
        appendValue(stringBuilder, ripemd160HashState.getH0().getValue());
        appendValue(stringBuilder, ripemd160HashState.getH1().getValue());
        appendValue(stringBuilder, ripemd160HashState.getH2().getValue());
        appendValue(stringBuilder, ripemd160HashState.getH3().getValue());
        appendValue(stringBuilder, ripemd160HashState.getH4().getValue());
        return stringBuilder.toString();
    }

    @Override
    public String getName() {
        return "RIPEMD160";
    }

    private void sendHashStateListenerEvent(String stateInfo) {
        for (HashStateListener hashStateListener : hashStateListeners) {
            if (hashStateListener != null) {
                hashStateListener.stateUpdated(getHashState(), stateInfo);
            }
        }
    }

    @Override
    public HashState getHashState() {
        return ripemd160HashState;
    }

    @Override
    public void addHashStateListener(HashStateListener hashStateListener) {
        hashStateListeners.add(hashStateListener);
    }

    @Override
    public boolean isFinished() {
        return ripemd160HashState.isFinished();
    }

    private void appendValue(StringBuilder stringBuilder, int input) {
        String string = Integer.toHexString(input);

        // Horrendously inefficient!
        while (string.length() != 8) {
            string = "0" + string;
        }

        stringBuilder.append(string.substring(6, 8));
        stringBuilder.append(string.substring(4, 6));
        stringBuilder.append(string.substring(2, 4));
        stringBuilder.append(string.substring(0, 2));
    }

    private int leftRotate(int input, int bits) {
        int returnValue = (input << bits) | (input >>> (32 - bits));

        return returnValue;
    }

    /**
     * From RIPEMD-160 - 3.1 3
     *
     * @param roundNumber
     * @param x
     * @param y
     * @param z
     * @return
     * @throws Exception
     */
    private int applyFunctionForRound(int roundNumber, int x, int y, int z) throws Exception {
        int functionNumber = getIndex(roundNumber);

        switch (functionNumber) {
            case 0:
                return functionF1(x, y, z);
            case 1:
                return functionF2(x, y, z);
            case 2:
                return functionF3(x, y, z);
            case 3:
                return functionF4(x, y, z);
            case 4:
                return functionF5(x, y, z);
            default:
                throw new Exception("Invalid round number [" + roundNumber + "]");
        }
    }

    private int functionF1(int x, int y, int z) {
        return x ^ y ^ z;
    }

    private int functionF2(int x, int y, int z) {
        return (x & y) | (~x & z);
    }

    private int functionF3(int x, int y, int z) {
        return (x | ~y) ^ z;
    }

    private int functionF4(int x, int y, int z) {
        return (x & z) | (y & ~z);
    }

    private int functionF5(int x, int y, int z) {
        return x ^ (y | ~z);
    }
}
