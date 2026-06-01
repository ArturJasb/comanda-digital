package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraResponse(
        Long id,
        Long fornecedorId,
        String fornecedorNome,
        String status,
        BigDecimal valorTotal,
        String usuarioNome,
        LocalDateTime createdAt,
        List<CompraItemResponse> itens
) {}
