package dev.nhairlahovic.crud.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValueValidator.class)
public @interface EnumValueConstraint {

    Class<? extends Enum<?>> enumClass();

    String message() default "must be one of the following values: {enumValues}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean blankable() default false;

}
