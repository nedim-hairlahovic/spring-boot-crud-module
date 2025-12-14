package dev.nhairlahovic.crud.error;

import lombok.Getter;
import lombok.With;

import java.util.Map;

@Getter
public class ErrorInfo {

    private final String code;

    @With
    private final String message;

    @With
    private final Map<String, Object> params;

    @With
    private final Map<String, FieldErrorInfo> fieldErrors;

    private ErrorInfo(String code, String message, Map<String, Object> params, Map<String, FieldErrorInfo> fieldErrors) {
        this.code = code;
        this.message = message;
        this.params = params;
        this.fieldErrors = fieldErrors;
    }

    public static <E extends Enum<E>> ErrorInfo forCode(E code) {
        return new ErrorInfo(code.name(), null, null, null);
    }

    public static ErrorInfo forCode(String code) {
        return new ErrorInfo(code, null, null, null);
    }

    public static ErrorInfo conflict(String message) {
        return new ErrorInfo(CommonErrorCode.RESOURCE_CONFLICT.name(), message, null, null);
    }

    public static ErrorInfo conflict(String fieldName, CommonFieldErrorCode code, Object rejectedValue) {
        return new ErrorInfo(
                CommonErrorCode.RESOURCE_CONFLICT.name(),
                "Operation cannot be completed due to a conflict.",
                null,
                Map.of(fieldName, FieldErrorInfo.of(code, rejectedValue))
        );
    }
}

