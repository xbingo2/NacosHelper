package com.xbingo.jsonhelper.common;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class CronUtil {

    protected static final Pattern INVALID_CHARS_REGEX = Pattern.compile("[^\\d|A-Z|? \\-,\\/*#]");
    protected static final String WHITESPACE = " ";
    protected static final String DOUBLE_WHITESPACE = "  ";

    public static boolean isCronExpression(String expression) {

        if (StringUtils.isBlank(expression)) {
            return false;
        }

        if (expression.startsWith(WHITESPACE) || expression.endsWith(WHITESPACE)) {
            return false;
        }

        if (expression.contains(DOUBLE_WHITESPACE)) {
            return false;
        }

        int expressionParts = expression.split(WHITESPACE, 8).length;

        if (expressionParts < 5 || expressionParts > 7) {
            return false;
        }

        return !INVALID_CHARS_REGEX.matcher(expression).find();
    }

}
