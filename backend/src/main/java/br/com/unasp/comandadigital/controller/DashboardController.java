package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.response.DashboardResumoResponse;
import br.com.unasp.comandadigital.dto.response.TopPratoResponse;
import br.com.unasp.comandadigital.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Dashboard (admin)", description = "KPIs e graficos (RF-34..37)")
@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "KPIs do dia + alertas + vendas dos ultimos 7 dias (RF-34/36/37)")
    @GetMapping("/resumo")
    public DashboardResumoResponse resumo() {
        return dashboardService.resumo();
    }

    @Operation(summary = "Top 5 pratos mais vendidos (periodo em dias, padrao 30) - RF-35")
    @GetMapping("/top-pratos")
    public List<TopPratoResponse> topPratos(@RequestParam(defaultValue = "30") int dias) {
        return dashboardService.topPratos(dias);
    }
}
