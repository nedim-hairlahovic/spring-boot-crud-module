package dev.nhairlahovic.crud.exception;

import dev.nhairlahovic.crud.error.ErrorInfo;
import lombok.Getter;

@Getter
public class ConflictingResourceOperationException extends RuntimeException {

    private final ErrorInfo error;

    public ConflictingResourceOperationException(String message, ErrorInfo error) {
        super(message);
        this.error = error;
    }
}
