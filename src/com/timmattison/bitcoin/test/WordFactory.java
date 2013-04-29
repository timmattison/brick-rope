package com.timmattison.bitcoin.test;

import com.timmattison.bitcoin.test.script.Word;
import com.timmattison.bitcoin.test.script.words.arithmetic.*;
import com.timmattison.bitcoin.test.script.words.bitwiselogic.*;
import com.timmattison.bitcoin.test.script.words.constants.*;
import com.timmattison.bitcoin.test.script.words.crypto.*;
import com.timmattison.bitcoin.test.script.words.flowcontrol.*;
import com.timmattison.bitcoin.test.script.words.pseudowords.OpInvalidOpcode;
import com.timmattison.bitcoin.test.script.words.pseudowords.OpPubKey;
import com.timmattison.bitcoin.test.script.words.pseudowords.OpPubKeyHash;
import com.timmattison.bitcoin.test.script.words.reservedwords.*;
import com.timmattison.bitcoin.test.script.words.splice.*;
import com.timmattison.bitcoin.test.script.words.stack.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordFactory {
    private static Map<Byte, Class<Word>> classesByOpcode;
    private static HashMap<String, Class<Word>> classesByName;
    // Initialize the list of available words
    List<Class> availableWords = new ArrayList<Class>() {{
        add(Op0NotEqual.class);
        add(Op1Add.class);
        add(Op1Sub.class);
        add(Op2Div.class);
        add(Op2Mul.class);
        add(OpAbs.class);
        add(OpAdd.class);
        add(OpBoolAnd.class);
        add(OpBoolOr.class);
        add(OpDiv.class);
        add(OpGreaterThan.class);
        add(OpGreaterThanOrEqual.class);
        add(OpLessThan.class);
        add(OpLessThanOrEqual.class);
        add(OpLShift.class);
        add(OpMax.class);
        add(OpMin.class);
        add(OpMod.class);
        add(OpMul.class);
        add(OpNegate.class);
        add(OpNot.class);
        add(OpNumEqual.class);
        add(OpNumEqualVerify.class);
        add(OpNumNotEqual.class);
        add(OpRShift.class);
        add(OpSub.class);
        add(OpWithin.class);
        add(OpAnd.class);
        add(OpEqual.class);
        add(OpEqualVerify.class);
        add(OpInvert.class);
        add(OpOr.class);
        add(OpXor.class);
        add(Op0.class);
        add(Op1.class);
        add(Op10.class);
        add(Op11.class);
        add(Op12.class);
        add(Op13.class);
        add(Op14.class);
        add(Op15.class);
        add(Op16.class);
        add(Op1Negate.class);
        add(Op2.class);
        add(Op3.class);
        add(Op4.class);
        add(Op5.class);
        add(Op6.class);
        add(Op7.class);
        add(Op8.class);
        add(Op9.class);
        add(OpFalse.class);
        add(OpPushData1.class);
        add(OpPushData2.class);
        add(OpPushData4.class);
        add(OpTrue.class);
        add(OpCheckMultiSig.class);
        add(OpCheckMultiSigVerify.class);
        add(OpCheckSig.class);
        add(OpCheckSigVerify.class);
        add(OpCodeSeparator.class);
        add(OpHash160.class);
        add(OpHash256.class);
        add(OpRipEmd160.class);
        add(OpSha1.class);
        add(OpSha256.class);
        add(OpElse.class);
        add(OpEndIf.class);
        add(OpIf.class);
        add(OpNop.class);
        add(OpNotIf.class);
        add(OpReturn.class);
        add(OpVerify.class);
        add(OpInvalidOpcode.class);
        add(OpPubKey.class);
        add(OpPubKeyHash.class);
        add(OpNop1.class);
        add(OpNop10.class);
        add(OpNop2.class);
        add(OpNop3.class);
        add(OpNop4.class);
        add(OpNop5.class);
        add(OpNop6.class);
        add(OpNop7.class);
        add(OpNop8.class);
        add(OpNop9.class);
        add(OpReserved.class);
        add(OpReserved1.class);
        add(OpReserved2.class);
        add(OpVer.class);
        add(OpVerIf.class);
        add(OpVerNotIf.class);
        add(OpCat.class);
        add(OpLeft.class);
        add(OpRight.class);
        add(OpSize.class);
        add(OpSubStr.class);
        add(Op2Drop.class);
        add(Op2Dup.class);
        add(Op2Over.class);
        add(Op2Rot.class);
        add(Op2Swap.class);
        add(Op3Dup.class);
        add(OpDepth.class);
        add(OpDrop.class);
        add(OpDup.class);
        add(OpFromAltStack.class);
        add(OpIfDup.class);
        add(OpNip.class);
        add(OpOver.class);
        add(OpPick.class);
        add(OpRoll.class);
        add(OpRot.class);
        add(OpSwap.class);
        add(OpToAltStack.class);
        add(OpTuck.class);
    }};

    public WordFactory() throws InstantiationException, IllegalAccessException {
        if (classesByOpcode == null) {
            createOpcodeLookupTable();
        }

        if (classesByName == null) {
            createNameLookupTable();
        }
    }

    private void createOpcodeLookupTable() throws IllegalAccessException, InstantiationException {
        classesByOpcode = new HashMap<Byte, Class<Word>>();

        for (Class<Word> clazz : availableWords) {
            Word instance = clazz.newInstance();
            classesByOpcode.put(instance.getOpcode(), clazz);
        }

        /*
        for(int loop = 0x01; loop < 0x4B; loop++) {
            VirtualOpPush opPush = new VirtualOpPush(loop);
            classesByOpcode.put(loop, (Class<Word>) opPush.getClass());
        }
        */
    }

    private void createNameLookupTable() throws IllegalAccessException, InstantiationException {
        classesByName = new HashMap<String, Class<Word>>();

        for (Class<Word> clazz : availableWords) {
            Word instance = clazz.newInstance();
            classesByName.put(instance.getWord(), clazz);
        }
    }

    public Word getWordByOpcode(byte opcode) {
        if ((opcode >= 0x01) && (opcode <= 0x4B)) {
            return new VirtualOpPush(opcode);
        }

        Class<Word> clazz = classesByOpcode.get(opcode);

        if (clazz == null) {
            throw new UnsupportedOperationException("No word found for opcode " + opcode + " in block #" + BlockChain.blockNumber);
        }

        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException("Couldn't instantiate class for opcode " + opcode);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("Illegal access exception for opcode " + opcode);
        }
    }

    public Word getWordByName(String name) {
        Class<Word> clazz = classesByName.get(name);

        if (clazz == null) {
            throw new UnsupportedOperationException("No word found for name " + name);
        }

        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException("Couldn't instantiate class for opcode " + name);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("Illegal access exception for opcode " + name);
        }
    }
}
