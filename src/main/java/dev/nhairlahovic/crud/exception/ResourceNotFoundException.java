package dev.nhairlahovic.crud.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(resourceType + " (ID: " + id + ") not found");
    }

    public ResourceNotFoundException(String resourceType, String id) {
        super(resourceType + " (ID: " + id + ") not found");
    }
}
