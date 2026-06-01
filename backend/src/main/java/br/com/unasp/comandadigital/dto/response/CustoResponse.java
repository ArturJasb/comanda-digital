package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

/** RF-12/RF-13: custo calculado + food cost de um prato. */
public record CustoResponse(
        Long pratoId,
        BigDecimal custoTotal,
        BigDecimal precoVenda,
        BigDecimal foodCostPct,
        String foodCostCor,
        String warning
) {}
