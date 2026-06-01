package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.request.PedidoRequest;
import br.com.unasp.comandadigital.dto.response.PedidoResponse;
import br.com.unasp.comandadigital.security.CustomUserDetails;
import br.com.unasp.comandadigital.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

@Tag(name = "Pedidos (cliente)", description = "Area do cliente (RF-06/07/08)")
@RestController
@RequestMapping("/api/pedidos")
@PreAuthorize("hasRole('CLIENTE')")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Cria pedido a partir do carrinho (RF-06)")
    @ApiResponse(responseCode = "201", description = "Pedido criado (RECEBIDO)")
    @ApiResponse(responseCode = "422", description = "Estoque insuficiente")
    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest req,
                                                @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(req, user.getId()));
    }

    @Operation(summary = "Meus pedidos - historico do cliente logado (RF-08)")
    @GetMapping("/meus")
    public Page<PedidoResponse> meus(@AuthenticationPrincipal CustomUserDetails user,
                                     @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                     Pageable pageable) {
        return pedidoService.meusPedidos(user.getId(), pageable);
    }

    @Operation(summary = "Status atual do pedido (RF-07)")
    @GetMapping("/{id}/status")
    public PedidoResponse status(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        return pedidoService.buscarDoCliente(id, user.getId());
    }
}
