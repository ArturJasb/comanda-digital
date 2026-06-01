package br.com.unasp.comandadigital.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CnpjValidator implements ConstraintValidator<CNPJ, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // @NotBlank cuida do obrigatorio
        }
        return isValidCnpj(value.replaceAll("[^0-9]", ""));
    }

    /** Valida os digitos verificadores do CNPJ (14 digitos). */
    public static boolean isValidCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14 || !cnpj.matches("\\d{14}")) {
            return false;
        }
        // Rejeita sequencias repetidas (ex: 00000000000000)
        if (cnpj.chars().distinct().count() == 1) {
            return false;
        }
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int dig1 = calcularDigito(cnpj.substring(0, 12), pesos1);
        int dig2 = calcularDigito(cnpj.substring(0, 12) + dig1, pesos2);

        return cnpj.equals(cnpj.substring(0, 12) + dig1 + dig2);
    }

    private static int calcularDigito(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
