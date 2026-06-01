package br.com.unasp.comandadigital.dto.response;

import java.util.List;

public record FornecedorResponse(
        Long id,
        String razaoSocial,
        String cnpj,
        String telefone,
        String email,
        String status,
        List<FornecedorProdutoResponse> produtos
) {}
