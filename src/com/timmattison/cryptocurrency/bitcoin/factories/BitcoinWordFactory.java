package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.bitcoin.words.arithmetic.*;
import com.timmattison.cryptocurrency.bitcoin.words.bitwiselogic.*;
import com.timmattison.cryptocurrency.bitcoin.words.constants.*;
import com.timmattison.cryptocurrency.bitcoin.words.crypto.*;
import com.timmattison.cryptocurrency.bitcoin.words.flowcontrol.*;
import com.timmattison.cryptocurrency.bitcoin.words.pseudowords.*;
import com.timmattison.cryptocurrency.bitcoin.words.reservedwords.*;
import com.timmattison.cryptocurrency.bitcoin.words.splice.*;
import com.timmattison.cryptocurrency.bitcoin.words.stack.*;
import com.timmattison.cryptocurrency.factories.WordFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 8:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinWordFactory implements WordFactory {
    private static Map<Byte, Class<Word>> classesByOpcode;
    private static HashMap<String, Class<Word>> classesByName;

    // Initialize the list of available words
    List<Class> availableWords = new ArrayList<Class>() {{
   }};

    public BitcoinWordFactory() throws InstantiationException, IllegalAccessException {
        if (classesByOpcode == null) {
            createOpcodeLookupTable();
        }

        if (classesByName == null) {
            createNameLookupTable();
        }
    }

    @Override
    public Word createWord(byte opcode) {
      switch(opcode) {
          case 0x00 : return new OpFalse(); break;
          case 0x4c : return new OpPushData1(); break;
          case 0x4d : return new OpPushData2(); break;
          case 0x4e : return new OpPushData4(); break;
          case 0x4f : return new Op1Negate(); break;
          case 0x51 : return new OpTrue(); break;
          case 0x52 : return new Op2(); break;
          case 0x53 : return new Op3(); break;
          case 0x54 : return new Op4(); break;
          case 0x55 : return new Op5(); break;
          case 0x56 : return new Op6(); break;
          case 0x57 : return new Op7(); break;
          case 0x58 : return new Op8(); break;
          case 0x59 : return new Op9(); break;
          case 0x5a : return new Op10(); break;
          case 0x5b : return new Op11(); break;
          case 0x5c : return new Op12(); break;
          case 0x5d : return new Op13(); break;
          case 0x5e : return new Op14(); break;
          case 0x5f : return new Op15(); break;
          case 0x60 : return new Op16(); break;
          case 0x61 : return new OpNop(); break;
          case 0x63 : return new OpIf(); break;
          case 0x64 :
              Op0NotEqual
              Op1Add
              Op1Sub
              Op2Div
              Op2Mul
              OpAbs
              OpAdd
              OpBoolAnd
              OpBoolOr
              OpDiv
              OpGreaterThan
              OpGreaterThanOrEqual
              OpLessThan
              OpLessThanOrEqual
              OpLShift
              OpMax
              OpMin
              OpMod
              OpMul
              OpNegate
              OpNot
              OpNumEqual
              OpNumEqualVerify
              OpNumNotEqual
              OpRShift
              OpSub
              OpWithin
              OpAnd
              OpEqual
              OpEqualVerify
              OpInvert
              OpOr
              OpXor
              Op0
              Op1
              Op10
              Op11
              Op12
              Op13
              Op14
              Op15
              Op16
              Op1Negate
              Op2
              Op3
              Op4
              Op5
              Op6
              Op7
              Op8
              Op9
              OpFalse
              OpPushData1
              OpPushData2
              OpPushData4
              OpTrue
              OpCheckMultiSig
              OpCheckMultiSigVerify
              OpCheckSig
              OpCheckSigVerify
              OpCodeSeparator
              OpHash160
              OpHash256
              OpRipEmd160
              OpSha1
              OpSha256
              OpElse
              OpEndIf
              OpIf
              OpNop
              OpNotIf
              OpReturn
              OpVerify
              OpInvalidOpcode
              OpPubKey
              OpPubKeyHash
              OpNop1
              OpNop10
              OpNop2
              OpNop3
              OpNop4
              OpNop5
              OpNop6
              OpNop7
              OpNop8
              OpNop9
              OpReserved
              OpReserved1
              OpReserved2
              OpVer
              OpVerIf
              OpVerNotIf
              OpCat
              OpLeft
              OpRight
              OpSize
              OpSubStr
              Op2Drop
              Op2Dup
              Op2Over
              Op2Rot
              Op2Swap
              Op3Dup
              OpDepth
              OpDrop
              OpDup
              OpFromAltStack
              OpIfDup
              OpNip
              OpOver
              OpPick
              OpRoll
              OpRot
              OpSwap
              OpToAltStack
              OpTuck
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
            classesByName.put(instance.getName(), clazz);
        }
    }

    private Word getWordByOpcode(byte opcode) {
        if ((opcode >= 0x01) && (opcode <= 0x4B)) {
            return new VirtualOpPush(opcode);
        }

        Class<Word> clazz = classesByOpcode.get(opcode);

        if (clazz == null) {
            throw new UnsupportedOperationException("No word found for opcode " + opcode + " [" + ByteArrayHelper.toHex(opcode) + "]");
        }

        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException("Couldn't instantiate class for opcode " + opcode);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("Illegal access exception for opcode " + opcode);
        }
    }

    private Word getWordByName(String name) {
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
