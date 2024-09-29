package com.recipe.jamanchu.annotation;

import com.recipe.jamanchu.validator.AtLeastOneNotEmptyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AtLeastOneNotEmptyValidator.class)
@Target({ ElementType.TYPE })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneNotEmpty {
  String message() default "하나 이상의 검색 조건이 필요합니다.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
