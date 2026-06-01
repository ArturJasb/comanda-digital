package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.request.MovimentacaoManualRequest;
import br.com.unasp.comandadigital.dto.response.MovimentacaoResponse;
import br.com.unasp.comandadigital.dto.response.SaldoResponse;
import br.com.unasp.comandadigital.security.CustomUserDetails;
import br.com.unasp.comandadigital.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Estoque (admin)", description = "Saldo, alertas e movimentacoes (RF-28..33)")
@RestController
@RequestMapping("/api/admin/estoque")
@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @Operation(summary = "Saldo atual de todos os ingredientes (RF-31)")
    @GetMapping("/saldo")
    public List<SaldoResponse> saldo() {
        return estoqueService.saldos();
    }

    @Operation(summary = "Ingredientes abaixo do estoque minimo (RF-32)")
    @GetMapping("/alertas")
    public List<SaldoResponse> alertas() {
        return estoqueService.alertas();
    }

    @Operation(summary = "Historico de movimentacoes (RF-33)")
    @GetMapping("/movimentacoes")
    public Page<MovimentacaoResponse> movimentacoes(@RequestParam(required = false) Long ingredienteId,
                                                    @PageableDefault(size = 20, sort = "createdAt",
                                                            direction = Sort.Direction.DESC) Pageable pageable) {
        return estoqueService.historico(ingredienteId, pageable);
    }

    @Operation(summary = "Registra saida manual / perda (RF-30)")
    @PostMapping("/movimentacao")
    public ResponseEntity<MovimentacaoResponse> saidaManual(@Valid @RequestBody MovimentacaoManualRequest req,
                                                            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estoqueService.saidaManual(req, user.getId()));
    }
}
