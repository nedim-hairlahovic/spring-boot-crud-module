package dev.nhairlahovic.crud.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class EnumValueValidator implements ConstraintValidator<EnumValueConstraint, String> {

    private List<String> enumValues;
    private String messageTemplate;
    private boolean allowBlank;

    @Override
    public void initialize(EnumValueConstraint constraintAnnotation) {
        enumValues = new ArrayList<>();
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();

        @SuppressWarnings("rawtypes")
        Enum[] enumValArr = enumClass.getEnumConstants();
        for (@SuppressWarnings("rawtypes") Enum enumVal : enumValArr) {
            enumValues.add(enumVal.toString().toUpperCase());
        }

        messageTemplate = constraintAnnotation.message();
        allowBlank = constraintAnnotation.blankable();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        String message = this.messageTemplate.replace("{enumValues}", String.join(", ", enumValues));
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

        if (value == null || value.isEmpty()) {
            return allowBlank;
        }

        return enumValues.contains(value.toUpperCase());
    }
}
