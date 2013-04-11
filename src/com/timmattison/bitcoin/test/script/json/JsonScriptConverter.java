package com.timmattison.bitcoin.test.script.json;

import com.timmattison.bitcoin.test.WordFactory;
import com.timmattison.bitcoin.test.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/18/13
 * Time: 7:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class JsonScriptConverter {
    public static String convertOpcodeStringsToBytes(String script) throws IllegalAccessException, InstantiationException {
        // Build a word factory so we can look up opcodes
        WordFactory wordFactory = new WordFactory();

        int position = 0;
        int length = script.length();
        int previousPosition = 0;
        StringBuilder stringBuilder = new StringBuilder();

        // Loop while there are still characters left
        while (position < length) {
            // Find any instance of the string that starts opcodes
            position = script.indexOf("OP_", position);

            // Did we find an opcode?
            if (position != -1) {
                // TODO - Yes, process it
                // Grab the text before the opcode and add them to the string
                stringBuilder.append(script.substring(previousPosition, position));

                // Find the next space so we can grab the opcode
                int endOfOpcode = script.indexOf(" ", position);

                // Was there another space?
                if (endOfOpcode == -1) {
                    // No, this is the last opcode.  Just read to the end of the string.
                    endOfOpcode = length;
                }

                // Get the opcode's name
                String opcodeName = script.substring(position, endOfOpcode);

                // Look it up
                Word opcode = wordFactory.getWordByName(opcodeName);

                // Add the opcode's opcode byte to the output string
                stringBuilder.append(String.format("%02x", opcode.getOpcode()));

                // Update the previous position value
                previousPosition = position;

                // Update the position value
                position = endOfOpcode;
            } else {
                // No, we are done
                stringBuilder.append(script.substring(previousPosition, length));

                position = length;
            }
        }

        // Remove any spaces that are left over
        String returnValue = stringBuilder.toString().replace(" ", "");

        return returnValue;
    }

    // TODO - Find opcode strings (strings starting with a space, ending with a space or EOL)
    // TODO - Use the word factory to get the opcodes for the string
    // TODO - Convert the opcode to a byte and put it back into the input string

    // Could this all be done with regex?  Very inefficient but simple.  Could have weird issues with overlapping opcode names
    //   if there are any.
}
