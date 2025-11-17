package org.waterwood.waterfunservice.infrastructure.validation;

import jakarta.validation.Constraint;
import org.springframework.messaging.handler.annotation.Payload;
import org.waterwood.waterfunservice.infrastructure.validation.validator.PhoneNumberValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "{verification.phone.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
