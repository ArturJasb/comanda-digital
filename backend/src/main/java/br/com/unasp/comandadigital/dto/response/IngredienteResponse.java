package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

public record IngredienteResponse(
        Long id,
        String nome,
        String sku,
        String unidadePadrao,
        BigDecimal estoqueMinimo,
        BigDecimal custoUnitario,
        String status,
        BigDecimal saldoAtual,
        boolean abaixoMinimo
) {}
