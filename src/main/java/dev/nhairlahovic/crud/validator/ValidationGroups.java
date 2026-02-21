package dev.nhairlahovic.crud.validator;

import jakarta.validation.groups.Default;

/**
 * Defines validation groups.
 * All groups run in parallel to show all validation errors at once.
 */
public class ValidationGroups {

    /**
     * Validation group for required field checks
     */
    public interface RequiredChecks {}

    /**
     * Validation group for format and valid value checks
     */
    public interface FormatChecks {}

    /**
     * Combined group that includes Default, RequiredChecks, and FormatChecks.
     * All validators run in parallel and all errors are shown at once.
     */
    public interface All extends Default, RequiredChecks, FormatChecks {}

}
