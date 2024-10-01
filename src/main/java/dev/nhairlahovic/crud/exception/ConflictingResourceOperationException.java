package dev.nhairlahovic.crud.exception;

public class ConflictingResourceOperationException extends RuntimeException {

    public ConflictingResourceOperationException(String message) {
        super(message);
    }
}

