package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.request.CompraRequest;
import br.com.unasp.comandadigital.dto.response.CompraResponse;
import br.com.unasp.comandadigital.model.StatusPedidoCompra;
import br.com.unasp.comandadigital.security.CustomUserDetails;
import br.com.unasp.comandadigital.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Compras (admin)", description = "Pedido de compra e recebimento (RF-24/25)")
@RestController
@RequestMapping("/api/admin/compras")
@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @Operation(summary = "Lista pedidos de compra")
    @GetMapping
    public List<CompraResponse> listar() {
        return compraService.listar();
    }

    @Operation(summary = "Detalhe do pedido de compra")
    @GetMapping("/{id}")
    public CompraResponse detalhe(@PathVariable Long id) {
        return compraService.buscarResponse(id);
    }

    @Operation(summary = "Cria pedido de compra (RASCUNHO)")
    @PostMapping
    public ResponseEntity<CompraResponse> criar(@Valid @RequestBody CompraRequest req,
                                                @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.criar(req, user.getId()));
    }

    @Operation(summary = "Edita pedido de compra (antes do recebimento)")
    @PutMapping("/{id}")
    public CompraResponse atualizar(@PathVariable Long id, @Valid @RequestBody CompraRequest req) {
        return compraService.atualizar(id, req);
    }

    @Operation(summary = "Muda status (ex: RASCUNHO -> ENVIADO / CANCELADO)")
    @PatchMapping("/{id}/status")
    public CompraResponse mudarStatus(@PathVariable Long id, @RequestParam StatusPedidoCompra status) {
        return compraService.mudarStatus(id, status);
    }

    @Operation(summary = "Registra recebimento -> entrada no estoque + atualiza custo (RF-25/RN-05)")
    @PostMapping("/{id}/receber")
    public CompraResponse receber(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        return compraService.receber(id, user.getId());
    }
}
