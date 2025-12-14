package dev.nhairlahovic.crud.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class FieldErrorInfo {
    private String code;
    private String message;
    private Object rejectedValue;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> params;

    public static FieldErrorInfo of(CommonFieldErrorCode code, String message) {
        return new FieldErrorInfo(code.name(), code.getDefaultMessage(), null, null);
    }

    public static FieldErrorInfo of(CommonFieldErrorCode code, Object rejectedValue) {
        return new FieldErrorInfo(code.name(), code.getDefaultMessage(), rejectedValue, null);
    }

    public static FieldErrorInfo of(String errorCode, String message) {
        return new FieldErrorInfo(errorCode, message, null, null);
    }

    public static <E extends Enum<E>> FieldErrorInfo of(E enumValue, String message, Object rejectedValue, Map<String, Object> params) {
        return new FieldErrorInfo(enumValue.name(), message, rejectedValue, params);
    }
}
