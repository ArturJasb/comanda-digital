package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        String status,
        BigDecimal valorTotal,
        String enderecoEntrega,
        String observacoes,
        String motivoCancelamento,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<PedidoItemResponse> itens
) {}
