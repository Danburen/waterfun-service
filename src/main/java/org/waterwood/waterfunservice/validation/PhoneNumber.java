package org.waterwood.waterfunservice.validation;

import jakarta.validation.Constraint;
import org.springframework.messaging.handler.annotation.Payload;
import org.waterwood.waterfunservice.validation.validator.PhoneNumberValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "PHONE_NUMBER_EMPTY_OR_INVALID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
