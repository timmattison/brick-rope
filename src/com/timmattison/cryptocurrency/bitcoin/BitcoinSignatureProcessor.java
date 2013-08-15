package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/15/13
 * Time: 7:14 AM
 * To change this template use File | Settings | File Templates.
 */
PARAMETERIZE THIS!

public class BitcoinSignatureProcessor implements SignatureProcessor {
    @Override
    public Object getSignature(byte[] data) {
        /*
        Signature should be in this format (from http://www.bitcoinsecurity.org/2012/07/22/7/):
          [sig] = [sigLength][0×30][rsLength][0×02][rLength][sig_r][0×02][sLength][sig_s][0×01]
            where
              sigLength gives the number of bytes taken up the rest of the signature ([0×30]…[0×01])
              rsLength gives the number of bytes in [0×02][rLength][sig_r][0×02][sLength][sig_s]
              rLength gives the number of bytes in [sig_r] (approx 32 bytes)
              sLength gives the number of bytes in [sig_s] (approx 32 bytes)
         */

        // At this point we don't have the sigLength bytes so let's validate what we know for sure

        // First byte must be 0x30
        if (data[0] != 0x30) {
            throw new UnsupportedOperationException("Signature does not start with 0x30");
        }

        // Second byte must be the length of the data minus 3 (1 for the byte we just checked, 1 for this byte,
        //   and 1 for the last byte which is the signature hash type)
        if (data[1] != (data.length - 3)) {
            throw new UnsupportedOperationException("Invalid rsLength [" + data[1] + ", " + data.length + "]");
        }

        // Third byte must be 0x02
        if (data[2] != 0x02) {
            throw new UnsupportedOperationException("sig_r Signature type is not 0x02 [" + data[2] + "]");
        }

        // sig_r length must be (XXX - docs say approx 32 bytes - XXX) 32 bytes
        if (data[3] != 32) {
            throw new UnsupportedOperationException("rLength is not 32 [" + data[3] + "]");
        }

        // First byte after sig_r must be 0x02
        if (data[3 + 32] != 0x02) {
            throw new UnsupportedOperationException("sig_s Signature type is not 0x02 [" + data[2] + "]");
        }

        // sig_s length must be (XXX - docs say approx 32 bytes - XXX) 32 bytes
        if (data[3 + 32 + 1] != 32) {
            throw new UnsupportedOperationException("sLength is not 32 [" + data[3] + "]");
        }

        // Last byte must be a valid signature hash value
        byte hashTypeByte = data[data.length - 1];

        if (BitcoinHashType.convert(hashTypeByte) == null) {
            throw new UnsupportedOperationException("Unsupported hash type [" + hashTypeByte + "]");
        }
    }
}
