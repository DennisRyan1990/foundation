package com.zchi.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class Assert {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static void notNull(Object obj) {
        notNull(obj, "[Assertion failed] - the object argument must be null");
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isPositive(Number number) {
        isPositive(number, "[Assertion failed] - the number argument must be positive");
    }

    public static void isPositive(Number number, String message) {
        if (number == null || number.doubleValue() <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNegative(Number number) {
        notNegative(number, "[Assertion failed] - the number argument must not be negative");
    }

    public static void notNegative(Number number, String message) {
        if (number == null || number.doubleValue() < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notBlank(CharSequence charSequence) {
        notBlank(charSequence, "[Assertion failed] - the string argument must not be blank");
    }

    public static void notBlank(CharSequence charSequence, String message) {
        if (StringUtils.isBlank(charSequence)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Collection<?> collection) {
        notEmpty(collection, "[Assertion failed] - the collection argument must not be empty");
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
