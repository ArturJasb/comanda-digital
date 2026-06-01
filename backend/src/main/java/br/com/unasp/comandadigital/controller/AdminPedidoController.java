package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.request.CancelamentoRequest;
import br.com.unasp.comandadigital.dto.request.StatusPedidoRequest;
import br.com.unasp.comandadigital.dto.response.PedidoResponse;
import br.com.unasp.comandadigital.model.StatusPedido;
import br.com.unasp.comandadigital.security.CustomUserDetails;
import br.com.unasp.comandadigital.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pedidos (admin/cozinha)", description = "Gestao de pedidos (RF-15..20)")
@RestController
@RequestMapping("/api/admin/pedidos")
@PreAuthorize("hasAnyRole('ADMIN','GERENTE','COZINHEIRO')")
public class AdminPedidoController {

    private final PedidoService pedidoService;

    public AdminPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Lista pedidos (paginado, filtro por status) - RF-19")
    @GetMapping
    public Page<PedidoResponse> listar(@RequestParam(required = false) StatusPedido status,
                                       @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                       Pageable pageable) {
        return pedidoService.listar(status, pageable);
    }

    @Operation(summary = "Detalhe do pedido (RF-20)")
    @GetMapping("/{id}")
    public PedidoResponse detalhe(@PathVariable Long id) {
        return pedidoService.buscarResponse(id);
    }

    @Operation(summary = "Muda status do pedido (RF-16); CONFIRMADO da baixa no estoque (RF-17)")
    @PatchMapping("/{id}/status")
    public PedidoResponse mudarStatus(@PathVariable Long id, @Valid @RequestBody StatusPedidoRequest req,
                                      @AuthenticationPrincipal CustomUserDetails user) {
        return pedidoService.mudarStatus(id, req.getStatus(), user.getId());
    }

    @Operation(summary = "Cancela pedido com motivo e estorna estoque (RF-18/RN-04)")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @PatchMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelamentoRequest req,
                                   @AuthenticationPrincipal CustomUserDetails user) {
        return pedidoService.cancelar(id, req.getMotivo(), user.getId(), user.getUsuario().getPerfil());
    }
}
