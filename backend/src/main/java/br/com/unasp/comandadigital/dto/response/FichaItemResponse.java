package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

public record FichaItemResponse(
        Long id,
        Long ingredienteId,
        String ingredienteNome,
        BigDecimal quantidade,
        String unidade,
        BigDecimal fatorCorrecao,
        BigDecimal custoUnitario,
        BigDecimal custoItem
) {}
