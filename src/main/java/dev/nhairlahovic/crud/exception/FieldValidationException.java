package dev.nhairlahovic.crud.exception;

import dev.nhairlahovic.crud.error.CommonFieldErrorCode;
import dev.nhairlahovic.crud.error.FieldErrorInfo;
import lombok.Getter;

import java.util.Map;

@Getter
public class FieldValidationException extends RuntimeException {

    private final Map<String, FieldErrorInfo> fieldErrors;

    public FieldValidationException(String field, FieldErrorInfo error) {
        super("Validation failed");
        this.fieldErrors = Map.of(field, error);
    }

    public static FieldValidationException required(String field) {
        return new FieldValidationException(field, FieldErrorInfo.of(CommonFieldErrorCode.REQUIRED_NOT_NULL, null));
    }
}
