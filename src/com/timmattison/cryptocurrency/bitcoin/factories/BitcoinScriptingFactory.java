package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifierFactory;
import com.timmattison.cryptocurrency.bitcoin.BitcoinInputScript;
import com.timmattison.cryptocurrency.bitcoin.BitcoinOutputScript;
import com.timmattison.cryptocurrency.bitcoin.BitcoinValidationScript;
import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.bitcoin.words.arithmetic.*;
import com.timmattison.cryptocurrency.bitcoin.words.bitwiselogic.*;
import com.timmattison.cryptocurrency.bitcoin.words.constants.*;
import com.timmattison.cryptocurrency.bitcoin.words.crypto.*;
import com.timmattison.cryptocurrency.bitcoin.words.flowcontrol.*;
import com.timmattison.cryptocurrency.bitcoin.words.pseudowords.OpInvalidOpcode;
import com.timmattison.cryptocurrency.bitcoin.words.pseudowords.OpPubKey;
import com.timmattison.cryptocurrency.bitcoin.words.pseudowords.OpPubKeyHash;
import com.timmattison.cryptocurrency.bitcoin.words.reservedwords.*;
import com.timmattison.cryptocurrency.bitcoin.words.splice.*;
import com.timmattison.cryptocurrency.bitcoin.words.stack.*;
import com.timmattison.cryptocurrency.factories.SignatureProcessorFactory;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.standard.OutputScript;
import com.timmattison.cryptocurrency.standard.Script;
import com.timmattison.cryptocurrency.standard.ValidationScript;

import javax.inject.Inject;
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
public class BitcoinScriptingFactory implements ScriptingFactory {
    /**
     * These are the classes that can be instantiated without constructor arguments
     */
    private static Map<Byte, Class<Word>> noArgClassesByOpcode;
    /**
     * These are the no argument classes listed by name
     */
    private static HashMap<String, Class<Word>> noArgClassesByName;
    private final SignatureProcessorFactory signatureProcessorFactory;
    private final ECCMessageSignatureVerifierFactory eccMessageSignatureVerifierFactory;
    /**
     * The list of all of the words that take no constructor arguments
     */
    List<Class> noArgConstructorWords = new ArrayList<Class>() {{
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

    @Inject
    public BitcoinScriptingFactory(SignatureProcessorFactory signatureProcessorFactory, ECCMessageSignatureVerifierFactory eccMessageSignatureVerifierFactory) throws InstantiationException, IllegalAccessException {
        this.signatureProcessorFactory = signatureProcessorFactory;
        this.eccMessageSignatureVerifierFactory = eccMessageSignatureVerifierFactory;

        if (noArgClassesByOpcode == null) {
            createOpcodeLookupTable();
        }

        if (noArgClassesByName == null) {
            createNameLookupTable();
        }
    }

    private void createOpcodeLookupTable() throws IllegalAccessException, InstantiationException {
        noArgClassesByOpcode = new HashMap<Byte, Class<Word>>();

        for (Class<Word> clazz : noArgConstructorWords) {
            Word instance = clazz.newInstance();
            noArgClassesByOpcode.put(instance.getOpcode(), clazz);
        }
    }

    private void createNameLookupTable() throws IllegalAccessException, InstantiationException {
        noArgClassesByName = new HashMap<String, Class<Word>>();

        for (Class<Word> clazz : noArgConstructorWords) {
            Word instance = clazz.newInstance();
            noArgClassesByName.put(instance.getName(), clazz);
        }
    }

    @Override
    public Word createWord(byte opcode) {
        return getWordByOpcode(opcode);
    }

    private Word getWordByOpcode(byte opcode) {
        if ((opcode >= 0x01) && (opcode <= 0x4B)) {
            return new VirtualOpPush(opcode);
        }

        // See if this class is a no argument class
        Class<Word> clazz = noArgClassesByOpcode.get(opcode);

        // Did we find it?
        if (clazz != null) {
            // Yes, attempt to instantiate and return it
            try {
                return clazz.newInstance();
            } catch (InstantiationException e) {
                throw new UnsupportedOperationException("Couldn't instantiate class for opcode " + opcode);
            } catch (IllegalAccessException e) {
                throw new UnsupportedOperationException("Illegal access exception for opcode " + opcode);
            }
        }

        // Didn't find it, see if this is a class that takes arguments
        Word word = buildWordForOpcode(opcode);

        // Did we find it?
        if (word != null) {
            // Yes, return it
            return word;
        }

        // Didn't find it, throw an exception
        throw new UnsupportedOperationException("No word found for opcode " + opcode + " [" + ByteArrayHelper.toHex(opcode) + "]");
    }

    private Word buildWordForOpcode(byte opcode) {
        switch (opcode) {
            case (byte) 0xac:
                return new OpCheckSig(signatureProcessorFactory, eccMessageSignatureVerifierFactory, this);
            default:
                // Didn't find any match
                return null;
        }
    }

    @Override
    public InputScript createInputScript(int transactionVersionNumber, long scriptLength, boolean coinbase) {
        return (InputScript) new BitcoinInputScript(this, transactionVersionNumber, scriptLength, coinbase);
    }

    @Override
    public OutputScript createOutputScript(int transactionVersionNumber, long scriptLength) {
        return (OutputScript) new BitcoinOutputScript(this, transactionVersionNumber, scriptLength);
    }

    @Override
    public ValidationScript createValidationScript(Script inputScript, Script outputScript) {
        ValidationScript validationScript = new BitcoinValidationScript(this, inputScript.dump().length + outputScript.dump().length);
        validationScript.build(ByteArrayHelper.concatenate(inputScript.dump(), outputScript.dump()));

        return validationScript;
    }
}
