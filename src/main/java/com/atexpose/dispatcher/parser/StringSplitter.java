package com.atexpose.dispatcher.parser;

import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.Checker;

import java.util.regex.Pattern;

/**
 * This class handles splitting strings with qualifiers.
 *
 * @author Schinzel
 */
class StringSplitter {
    //Generic regex string for splitting on a delimiter outside text qualifiers
    private static final String GENERIC_REGEX = "%1$s(?=(?:[^%2$s]*%2$s[^%2$s]*%2$s)*(?![^%2$s]*%2$s))";
    //Delimiters
    private static final String DELIMITER_COMMA = "( *, *)";
    //Qualifiers
    private static final char QUALIFIER_DOUBLE_QUOTE = '"';
    private static final char QUALIFIER_SINGLE_QUOTE = '\'';
    //Patterns
    private static final Pattern PATTERN_COMMA_DOUBLE_QUOTE_QUALIFIER;
    private static final Pattern PATTERN_COMMA_SINGLE_QUOTE_QUALIFIER;

    static {
        String regex;
        regex = String.format(GENERIC_REGEX, DELIMITER_COMMA, QUALIFIER_DOUBLE_QUOTE);
        PATTERN_COMMA_DOUBLE_QUOTE_QUALIFIER = Pattern.compile(regex);
        regex = String.format(GENERIC_REGEX, DELIMITER_COMMA, QUALIFIER_SINGLE_QUOTE);
        PATTERN_COMMA_SINGLE_QUOTE_QUALIFIER = Pattern.compile(regex);

    }


    static String[] splitOnComma_DoubleQuoteQualifier(String stringToSplit) {
        return split(stringToSplit, PATTERN_COMMA_DOUBLE_QUOTE_QUALIFIER, QUALIFIER_DOUBLE_QUOTE);
    }


    static String[] splitOnComma_SingleQuoteQualifier(String stringToSplit) {
        return split(stringToSplit, PATTERN_COMMA_SINGLE_QUOTE_QUALIFIER, QUALIFIER_SINGLE_QUOTE);
    }


    private static String[] split(String stringToSplit, Pattern pattern, char qualifier) {
        if (Checker.isEmpty(stringToSplit)) {
            return EmptyObjects.EMPTY_STRING_ARRAY;
        }
        stringToSplit = stringToSplit.trim();
        String[] values = pattern.split(stringToSplit);
        //Go through all values
        for (int i = 0; i < values.length; i++) {
            //Get current value
            String value = values[i];
            //If current value is not empty and for and last chart is a qualifier
            if (value.length() > 1 && value.charAt(0) == qualifier && value.charAt(value.length() - 1) == qualifier) {
                //Remove the qualifiers
                values[i] = value.substring(1, value.length() - 1);
            }
        }
        return values;
    }

}
