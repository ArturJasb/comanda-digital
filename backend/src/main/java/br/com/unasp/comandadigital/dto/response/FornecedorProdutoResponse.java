package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;

public record FornecedorProdutoResponse(
        Long id,
        Long ingredienteId,
        String ingredienteNome,
        BigDecimal preco,
        String unidadeVenda
) {}
