package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

public record PratoResponse(
        Long id,
        Long categoriaId,
        String categoriaNome,
        String nome,
        String descricao,
        String fotoUrl,
        BigDecimal precoVenda,
        Integer tempoPreparoMin,
        String status,
        BigDecimal custo,
        BigDecimal foodCostPct,
        String foodCostCor,
        boolean temFicha
) {}
