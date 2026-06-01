package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

public record VendaDiaResponse(
        String dia,
        BigDecimal total
) {}
