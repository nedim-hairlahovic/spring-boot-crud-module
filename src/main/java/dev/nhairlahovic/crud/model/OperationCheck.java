package dev.nhairlahovic.crud.model;

import lombok.Getter;

@Getter
public class OperationCheck {

    private final boolean allowed;
    private final String message;

    public static final String DEFAULT_DENIED_MESSAGE = "Operation is not allowed";

    private OperationCheck(boolean isAllowed, String message) {
        this.allowed = isAllowed;
        this.message = message;
    }

    public static OperationCheck permitted() {
        return new OperationCheck(true, null);
    }

    public static OperationCheck denied() {
        return new OperationCheck(false, DEFAULT_DENIED_MESSAGE);
    }

    public static OperationCheck denied(String message) {
        return new OperationCheck(false, message);
    }

    public static OperationCheck permitted(String message) {
        return new OperationCheck(true, message);
    }
}
