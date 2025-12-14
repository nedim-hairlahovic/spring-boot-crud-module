package dev.nhairlahovic.crud.error;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

public enum CommonFieldErrorCode {

    REQUIRED_NOT_BLANK("must not be blank"),
    REQUIRED_NOT_NULL("must not be null"),
    NOT_UNIQUE("must be unique"),
    INVALID_ENUM_VALUE("must be one of allowed values"),
    POSITIVE("must be a positive number"),

    MIN("must be greater than or equal to {min}", CommonFieldErrorCode::extractMin),
    MAX("must be less than or equal to {max}", CommonFieldErrorCode::extractMax),
    RANGE("must be between {min} and {max}", CommonFieldErrorCode::extractRange),

    INVALID("is invalid");

    @Getter
    private final String defaultMessage;
    private final ArgsExtractor argsExtractor;

    CommonFieldErrorCode(String defaultMessage) {
        this(defaultMessage, null);
    }

    CommonFieldErrorCode(String defaultMessage, ArgsExtractor argsExtractor) {
        this.defaultMessage = defaultMessage;
        this.argsExtractor = argsExtractor;
    }

    public static CommonFieldErrorCode fromCode(String springCode) {
        return switch (springCode) {
            case "NotBlank" -> REQUIRED_NOT_BLANK;
            case "NotNull" -> REQUIRED_NOT_NULL;
            case "EnumValue" -> INVALID_ENUM_VALUE;
            case "Positive" -> POSITIVE;
            case "Range" -> RANGE;
            default -> INVALID;
        };
    }

    public Map<String, Object> extractParams(Object[] args) {
        return argsExtractor != null ? argsExtractor.extract(args) : null;
    }

    @FunctionalInterface
    interface ArgsExtractor {
        Map<String, Object> extract(Object[] args);
    }

    private static Map<String, Object> extractMin(Object[] args) {
        Object min = lastArg(args);
        if (min == null) return null;
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("min", min);
        return params;
    }

    private static Map<String, Object> extractMax(Object[] args) {
        Object max = lastArg(args);
        if (max == null) return null;
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("max", max);
        return params;
    }

    private static Map<String, Object> extractRange(Object[] args) {
        if (args == null || args.length < 2) return null;
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("min", args[args.length - 1]);
        params.put("max", args[args.length - 2]);
        return params;
    }

    private static Object lastArg(Object[] args) {
        if (args == null || args.length == 0) return null;
        return args[args.length - 1];
    }
}
