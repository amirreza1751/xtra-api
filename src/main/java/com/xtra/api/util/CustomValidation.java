package com.xtra.api.util;

import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class CustomValidation {
    @ConstraintComposition(CompositionType.AND)
    @Min(value = 5)
    @Max(value = 10)
    @Target( { ElementType.ANNOTATION_TYPE } )
    @Retention( RetentionPolicy.RUNTIME )
    @Constraint(validatedBy = { })
    public @interface ZeroOrValidRange {
        String message() default "field length should be grater than";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }
}
