package br.com.unasp.comandadigital.dto.response;

public record CategoriaResponse(
        Long id,
        String nome,
        String descricao,
        Integer ordem,
        String status
) {}
