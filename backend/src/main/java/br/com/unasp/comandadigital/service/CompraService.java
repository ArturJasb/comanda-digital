package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.request.CompraItemRequest;
import br.com.unasp.comandadigital.dto.request.CompraRequest;
import br.com.unasp.comandadigital.dto.response.CompraItemResponse;
import br.com.unasp.comandadigital.dto.response.CompraResponse;
import br.com.unasp.comandadigital.exception.BusinessException;
import br.com.unasp.comandadigital.exception.ResourceNotFoundException;
import br.com.unasp.comandadigital.model.*;
import br.com.unasp.comandadigital.repository.FornecedorRepository;
import br.com.unasp.comandadigital.repository.IngredienteRepository;
import br.com.unasp.comandadigital.repository.PedidoCompraRepository;
import br.com.unasp.comandadigital.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CompraService {

    private final PedidoCompraRepository compraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final IngredienteRepository ingredienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstoqueService estoqueService;

    public CompraService(PedidoCompraRepository compraRepository, FornecedorRepository fornecedorRepository,
                         IngredienteRepository ingredienteRepository, UsuarioRepository usuarioRepository,
                         EstoqueService estoqueService) {
        this.compraRepository = compraRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.estoqueService = estoqueService;
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> listar() {
        return compraRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CompraResponse buscarResponse(Long id) {
        return toResponse(buscar(id));
    }

    /** RF-24: cria pedido de compra (status RASCUNHO). */
    @Transactional
    public CompraResponse criar(CompraRequest req, Long usuarioId) {
        Fornecedor fornecedor = fornecedorRepository.findById(req.getFornecedorId())
                .orElseThrow(() -> ResourceNotFoundException.of("Fornecedor", req.getFornecedorId()));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", usuarioId));

        PedidoCompra compra = new PedidoCompra();
        compra.setFornecedor(fornecedor);
        compra.setUsuario(usuario);
        compra.setStatus(StatusPedidoCompra.RASCUNHO);
        aplicarItens(compra, req.getItens());
        return toResponse(compraRepository.save(compra));
    }

    @Transactional
    public CompraResponse atualizar(Long id, CompraRequest req) {
        PedidoCompra compra = buscar(id);
        if (compra.getStatus() == StatusPedidoCompra.RECEBIDO || compra.getStatus() == StatusPedidoCompra.CANCELADO) {
            throw new BusinessException("Nao e possivel editar uma compra " + compra.getStatus());
        }
        Fornecedor fornecedor = fornecedorRepository.findById(req.getFornecedorId())
                .orElseThrow(() -> ResourceNotFoundException.of("Fornecedor", req.getFornecedorId()));
        compra.setFornecedor(fornecedor);
        compra.getItens().clear();
        aplicarItens(compra, req.getItens());
        return toResponse(compraRepository.save(compra));
    }

    /** Transicao de status (RASCUNHO -> ENVIADO -> ... / CANCELADO). */
    @Transactional
    public CompraResponse mudarStatus(Long id, StatusPedidoCompra novo) {
        PedidoCompra compra = buscar(id);
        if (compra.getStatus() == StatusPedidoCompra.RECEBIDO) {
            throw new BusinessException("Compra ja recebida nao pode mudar de status");
        }
        if (novo == StatusPedidoCompra.RECEBIDO) {
            throw new BusinessException("Use o endpoint de recebimento para receber a compra");
        }
        compra.setStatus(novo);
        return toResponse(compraRepository.save(compra));
    }

    /**
     * RF-25 / RN-05: recebe a mercadoria, cria entradas no estoque e
     * atualiza o custo unitario de cada ingrediente para o preco da compra.
     */
    @Transactional
    public CompraResponse receber(Long id, Long usuarioId) {
        PedidoCompra compra = buscar(id);
        if (compra.getStatus() == StatusPedidoCompra.RECEBIDO) {
            throw new BusinessException("Esta compra ja foi recebida");
        }
        if (compra.getStatus() == StatusPedidoCompra.CANCELADO) {
            throw new BusinessException("Compra cancelada nao pode ser recebida");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", usuarioId));

        for (PedidoCompraItem item : compra.getItens()) {
            Ingrediente ing = item.getIngrediente();
            // Entrada no estoque (RF-28/RF-29)
            estoqueService.registrar(ing, TipoMovimentacao.ENTRADA, item.getQuantidade(),
                    MotivoMovimentacao.COMPRA, item.getPrecoUnitario(), null, null, usuario, null, compra);
            // RN-05: custo unitario do ingrediente passa a ser o preco pago
            ing.setCustoUnitario(item.getPrecoUnitario());
            ingredienteRepository.save(ing);
        }
        compra.setStatus(StatusPedidoCompra.RECEBIDO);
        return toResponse(compraRepository.save(compra));
    }

    private void aplicarItens(PedidoCompra compra, List<CompraItemRequest> itens) {
        BigDecimal total = BigDecimal.ZERO;
        for (CompraItemRequest ir : itens) {
            Ingrediente ing = ingredienteRepository.findById(ir.getIngredienteId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Ingrediente", ir.getIngredienteId()));
            PedidoCompraItem item = new PedidoCompraItem();
            item.setPedidoCompra(compra);
            item.setIngrediente(ing);
            item.setQuantidade(ir.getQuantidade());
            item.setPrecoUnitario(ir.getPrecoUnitario());
            BigDecimal subtotal = ir.getQuantidade().multiply(ir.getPrecoUnitario());
            item.setSubtotal(subtotal);
            compra.getItens().add(item);
            total = total.add(subtotal);
        }
        compra.setValorTotal(total);
    }

    private PedidoCompra buscar(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Pedido de compra", id));
    }

    private CompraResponse toResponse(PedidoCompra c) {
        List<CompraItemResponse> itens = c.getItens().stream()
                .map(i -> new CompraItemResponse(i.getId(), i.getIngrediente().getId(),
                        i.getIngrediente().getNome(), i.getQuantidade(), i.getPrecoUnitario(), i.getSubtotal()))
                .toList();
        return new CompraResponse(c.getId(), c.getFornecedor().getId(), c.getFornecedor().getRazaoSocial(),
                c.getStatus().name(), c.getValorTotal(),
                c.getUsuario() != null ? c.getUsuario().getNome() : null, c.getCreatedAt(), itens);
    }
}
