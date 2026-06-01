package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** RF-23: comparativo de fornecedores que vendem um ingrediente, ordenado por preco. */
public record CotacaoResponse(
        Long ingredienteId,
        String ingredienteNome,
        List<Opcao> opcoes
) {
    public record Opcao(
            Long fornecedorProdutoId,
            Long fornecedorId,
            String fornecedorNome,
            BigDecimal preco,
            String unidadeVenda
    ) {}
}
