package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record FichaTecnicaResponse(
        Long id,
        Long pratoId,
        String pratoNome,
        Integer rendimento,
        String modoPreparo,
        List<FichaItemResponse> itens,
        BigDecimal custoTotal,
        BigDecimal precoVenda,
        BigDecimal foodCostPct,
        String foodCostCor,
        String warning
) {}
