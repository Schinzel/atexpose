package com.atexpose.dispatcher.parser.textparser;

import io.schinzel.basicutils.Checker;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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


    static List<String> splitOnComma_DoubleQuoteQualifier(String stringToSplit) {
        return split(stringToSplit, PATTERN_COMMA_DOUBLE_QUOTE_QUALIFIER, QUALIFIER_DOUBLE_QUOTE);
    }


    static List<String> splitOnComma_SingleQuoteQualifier(String stringToSplit) {
        return split(stringToSplit, PATTERN_COMMA_SINGLE_QUOTE_QUALIFIER, QUALIFIER_SINGLE_QUOTE);
    }


    private static List<String> split(String stringToSplit, Pattern pattern, char qualifier) {
        return Checker.isEmpty(stringToSplit)
                ? Collections.emptyList()
                : pattern.splitAsStream(stringToSplit.trim())
                .map(v -> scrubQualifiers(v, qualifier))
                .collect(Collectors.toList());

    }


    private static String scrubQualifiers(String value, char qualifier) {
        return (isSurroundedByQualifiers(value, qualifier))
                ? value.substring(1, value.length() - 1)
                : value;
    }


    private static boolean isSurroundedByQualifiers(String value, char qualifier) {
        return (value.length() > 1 && value.charAt(0) == qualifier && value.charAt(value.length() - 1) == qualifier);
    }
}
