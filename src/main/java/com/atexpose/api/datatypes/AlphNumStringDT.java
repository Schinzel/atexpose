package com.atexpose.api.datatypes;

import java.util.regex.Pattern;

/**
 *
 * @author Schinzel
 */
public class AlphNumStringDT extends StringDT {
    //The pattern for alphanumeric strings
    private static final Pattern ALPH_NUM_PATTERN = Pattern.compile("[a-zA-Z0-9_@.-]*");


    AlphNumStringDT() {
        super("alph_num_string", "Allowed values are the alphanumeric characters, underscore, at sign, full stop and "
                + "hyphen-minus. The alphanumeric character set consists of the numbers 0 to 9 and "
                + "letters A to Z in the English alphabet case-sensitive. "
                + "The maximum length is " + MAX_LENGTH + " characters. Reserved value is the string 'null'.");
    }

    @Override
    public boolean verifyValue(String value) {
        return super.verifyValue(value) && ALPH_NUM_PATTERN.matcher(value).matches();
    }


}
