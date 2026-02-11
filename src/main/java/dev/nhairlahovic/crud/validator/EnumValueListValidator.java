package dev.nhairlahovic.crud.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EnumValueListValidator implements ConstraintValidator<EnumValue, Collection<String>> {

    private List<String> enumValues;
    private String messageTemplate;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        enumValues = new ArrayList<>();
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();

        @SuppressWarnings("rawtypes")
        Enum[] enumValArr = enumClass.getEnumConstants();
        for (@SuppressWarnings("rawtypes") Enum enumVal : enumValArr) {
            enumValues.add(enumVal.toString().toUpperCase());
        }

        messageTemplate = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Collection<String> values, ConstraintValidatorContext context) {
        if (values == null || values.isEmpty()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        String message = this.messageTemplate.replace("{enumValues}", String.join(", ", enumValues));

        boolean valid = true;
        int i = 0;
        for (String value : values) {
            if (value == null || !enumValues.contains(value.toUpperCase())) {
                context.buildConstraintViolationWithTemplate(message)
                        .addBeanNode().inIterable().atIndex(i)
                        .addConstraintViolation();
                valid = false;
            }
            i++;
        }

        return valid;
    }
}
