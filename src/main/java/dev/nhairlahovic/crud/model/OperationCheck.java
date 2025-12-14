package dev.nhairlahovic.crud.model;

import dev.nhairlahovic.crud.error.ErrorInfo;
import lombok.Getter;

@Getter
public class OperationCheck {

    private final boolean allowed;
    private final String message;
    private final ErrorInfo error;

    public static final String DEFAULT_DENIED_MESSAGE = "Operation is not allowed";

    private OperationCheck(boolean isAllowed, String message, ErrorInfo error) {
        this.allowed = isAllowed;
        this.message = message;
        this.error = error;
    }

    public static OperationCheck permitted() {
        return new OperationCheck(true, null, null);
    }

    public static OperationCheck denied() {
        return new OperationCheck(false, DEFAULT_DENIED_MESSAGE, null);
    }

    public static OperationCheck denied(ErrorInfo error) {
        return new OperationCheck(false, error.getMessage(), error);
    }
}
