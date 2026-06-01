package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

/** Prato exibido no cardapio publico (RF-01 / RF-02). Sem custo nem food cost. */
public record CardapioItemResponse(
        Long id,
        Long categoriaId,
        String categoriaNome,
        String nome,
        String descricao,
        String fotoUrl,
        BigDecimal precoVenda,
        Integer tempoPreparoMin
) {}
