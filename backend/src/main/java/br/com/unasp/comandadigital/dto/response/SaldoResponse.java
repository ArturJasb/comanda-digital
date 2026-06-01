package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

/** RF-31/RF-32: saldo atual de um ingrediente e se esta abaixo do minimo. */
public record SaldoResponse(
        Long ingredienteId,
        String nome,
        String sku,
        String unidadePadrao,
        BigDecimal saldoAtual,
        BigDecimal estoqueMinimo,
        boolean abaixoMinimo
) {}
