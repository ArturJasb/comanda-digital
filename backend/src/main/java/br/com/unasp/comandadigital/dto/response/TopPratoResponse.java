package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

public record TopPratoResponse(
        Long pratoId,
        String nome,
        Long quantidade,
        BigDecimal total
) {}
