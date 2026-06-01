package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

public record PedidoItemResponse(
        Long id,
        Long pratoId,
        String pratoNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal,
        String observacoes
) {}
