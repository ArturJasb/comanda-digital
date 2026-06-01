package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovimentacaoResponse(
        Long id,
        Long ingredienteId,
        String ingredienteNome,
        String tipo,
        BigDecimal quantidade,
        String motivo,
        String lote,
        LocalDate validade,
        BigDecimal custoUnitario,
        Long pedidoId,
        Long pedidoCompraId,
        String usuarioNome,
        LocalDateTime createdAt
) {}
