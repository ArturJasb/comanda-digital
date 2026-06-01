package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

public record CompraItemResponse(
        Long id,
        Long ingredienteId,
        String ingredienteNome,
        BigDecimal quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {}
