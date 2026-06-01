package br.com.unasp.comandadigital.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * RN-07: valida o CNPJ do fornecedor pelo algoritmo dos digitos verificadores.
 */
@Documented
@Constraint(validatedBy = CnpjValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CNPJ {
    String message() default "CNPJ invalido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
