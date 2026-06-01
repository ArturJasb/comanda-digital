package br.com.unasp.comandadigital.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** RF-34/36/37: KPIs do dia + alertas de estoque + vendas por periodo. */
public record DashboardResumoResponse(
        BigDecimal faturamentoDia,
        long totalPedidosDia,
        BigDecimal ticketMedio,
        BigDecimal foodCostMedio,
        List<SaldoResponse> alertasEstoque,
        List<VendaDiaResponse> vendasPorDia
) {}
