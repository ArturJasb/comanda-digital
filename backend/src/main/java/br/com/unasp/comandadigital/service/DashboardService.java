package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.response.DashboardResumoResponse;
import br.com.unasp.comandadigital.dto.response.TopPratoResponse;
import br.com.unasp.comandadigital.dto.response.VendaDiaResponse;
import br.com.unasp.comandadigital.model.FichaTecnica;
import br.com.unasp.comandadigital.model.Prato;
import br.com.unasp.comandadigital.model.StatusPrato;
import br.com.unasp.comandadigital.repository.FichaTecnicaRepository;
import br.com.unasp.comandadigital.repository.PedidoItemRepository;
import br.com.unasp.comandadigital.repository.PedidoRepository;
import br.com.unasp.comandadigital.repository.PratoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class DashboardService {

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final PratoRepository pratoRepository;
    private final FichaTecnicaRepository fichaRepository;
    private final FichaTecnicaService fichaTecnicaService;
    private final EstoqueService estoqueService;

    public DashboardService(PedidoRepository pedidoRepository, PedidoItemRepository pedidoItemRepository,
                            PratoRepository pratoRepository, FichaTecnicaRepository fichaRepository,
                            FichaTecnicaService fichaTecnicaService, EstoqueService estoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.pratoRepository = pratoRepository;
        this.fichaRepository = fichaRepository;
        this.fichaTecnicaService = fichaTecnicaService;
        this.estoqueService = estoqueService;
    }

    /** RF-34/36/37: KPIs do dia + alertas + vendas dos ultimos 7 dias. */
    @Transactional(readOnly = true)
    public DashboardResumoResponse resumo() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDia = hoje.atStartOfDay();
        LocalDateTime fimDia = hoje.atTime(LocalTime.MAX);

        BigDecimal faturamento = nz(pedidoRepository.faturamentoPeriodo(inicioDia, fimDia));
        long total = pedidoRepository.contarPeriodo(inicioDia, fimDia);
        BigDecimal ticket = total > 0
                ? faturamento.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal foodCostMedio = calcularFoodCostMedio();

        LocalDateTime inicio7 = hoje.minusDays(6).atStartOfDay();
        List<VendaDiaResponse> vendas = pedidoRepository.vendasPorDia(inicio7, fimDia).stream()
                .map(v -> new VendaDiaResponse(v.getDia(), nz(v.getTotal())))
                .toList();

        return new DashboardResumoResponse(faturamento, total, ticket, foodCostMedio,
                estoqueService.alertas(), vendas);
    }

    /** RF-35: top 5 pratos mais vendidos nos ultimos N dias. */
    @Transactional(readOnly = true)
    public List<TopPratoResponse> topPratos(int dias) {
        LocalDateTime fim = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime inicio = LocalDate.now().minusDays(dias - 1L).atStartOfDay();
        return pedidoItemRepository.topPratos(inicio, fim).stream()
                .map(t -> new TopPratoResponse(t.getPratoId(), t.getNome(), t.getQuantidade(), nz(t.getTotal())))
                .toList();
    }

    private BigDecimal calcularFoodCostMedio() {
        List<Prato> ativos = pratoRepository.findByStatus(StatusPrato.ATIVO);
        BigDecimal soma = BigDecimal.ZERO;
        int n = 0;
        for (Prato p : ativos) {
            FichaTecnica ficha = fichaRepository.findByPratoId(p.getId()).orElse(null);
            if (ficha == null || ficha.getItens().isEmpty()) continue;
            BigDecimal pct = fichaTecnicaService.calcular(ficha, p.getPrecoVenda()).foodCostPct();
            soma = soma.add(pct);
            n++;
        }
        return n == 0 ? BigDecimal.ZERO : soma.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
