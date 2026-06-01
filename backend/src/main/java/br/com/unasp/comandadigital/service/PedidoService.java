package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.request.PedidoItemRequest;
import br.com.unasp.comandadigital.dto.request.PedidoRequest;
import br.com.unasp.comandadigital.dto.response.PedidoItemResponse;
import br.com.unasp.comandadigital.dto.response.PedidoResponse;
import br.com.unasp.comandadigital.exception.BusinessException;
import br.com.unasp.comandadigital.exception.InsufficientStockException;
import br.com.unasp.comandadigital.exception.ResourceNotFoundException;
import br.com.unasp.comandadigital.model.*;
import br.com.unasp.comandadigital.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PratoRepository pratoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FichaTecnicaRepository fichaRepository;
    private final EstoqueService estoqueService;

    public PedidoService(PedidoRepository pedidoRepository, PratoRepository pratoRepository,
                         UsuarioRepository usuarioRepository, FichaTecnicaRepository fichaRepository,
                         EstoqueService estoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.pratoRepository = pratoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fichaRepository = fichaRepository;
        this.estoqueService = estoqueService;
    }

    // ===================== Cliente =====================

    /** RF-06/RN-03: cria pedido a partir do carrinho. Valida estoque (422 se faltar). */
    @Transactional
    public PedidoResponse criar(PedidoRequest req, Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", clienteId));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.RECEBIDO);
        pedido.setEnderecoEntrega(req.getEnderecoEntrega() != null ? req.getEnderecoEntrega() : cliente.getEndereco());
        pedido.setObservacoes(req.getObservacoes());

        BigDecimal total = BigDecimal.ZERO;
        for (PedidoItemRequest ir : req.getItens()) {
            Prato prato = pratoRepository.findById(ir.getPratoId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Prato", ir.getPratoId()));
            if (prato.getStatus() != StatusPrato.ATIVO) {
                throw new BusinessException("O prato '" + prato.getNome() + "' nao esta disponivel");
            }
            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setPrato(prato);
            item.setQuantidade(ir.getQuantidade());
            item.setPrecoUnitario(prato.getPrecoVenda());
            item.setObservacoes(ir.getObservacoes());
            pedido.getItens().add(item);
            total = total.add(prato.getPrecoVenda().multiply(BigDecimal.valueOf(ir.getQuantidade())));
        }
        pedido.setValorTotal(total);

        // RN-03: valida disponibilidade de estoque (sem dar baixa ainda)
        Map<Long, Necessidade> necessidade = calcularNecessidade(pedido);
        validarEstoque(necessidade);

        return toResponse(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> meusPedidos(Long clienteId, Pageable pageable) {
        return pedidoRepository.findByClienteIdOrderByCreatedAtDesc(clienteId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarDoCliente(Long pedidoId, Long clienteId) {
        Pedido p = buscar(pedidoId);
        if (!p.getCliente().getId().equals(clienteId)) {
            throw new ResourceNotFoundException("Pedido " + pedidoId + " nao encontrado para este cliente");
        }
        return toResponse(p);
    }

    // ===================== Admin =====================

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listar(StatusPedido status, Pageable pageable) {
        Page<Pedido> page = (status == null)
                ? pedidoRepository.findAll(pageable)
                : pedidoRepository.findByStatus(status, pageable);
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarResponse(Long id) {
        return toResponse(buscar(id));
    }

    /**
     * RF-16/RF-17: muda status do pedido. Ao ir RECEBIDO -> CONFIRMADO,
     * da baixa automatica no estoque (mesma transacao, rollback se faltar).
     */
    @Transactional
    public PedidoResponse mudarStatus(Long id, StatusPedido novo, Long usuarioId) {
        Pedido pedido = buscar(id);
        StatusPedido atual = pedido.getStatus();

        if (atual == StatusPedido.CANCELADO || atual == StatusPedido.FINALIZADO) {
            throw new BusinessException("Pedido " + atual + " nao pode mudar de status");
        }
        if (novo == StatusPedido.CANCELADO) {
            throw new BusinessException("Use o endpoint de cancelamento para cancelar o pedido");
        }

        // RF-17: baixa automatica ao confirmar
        if (novo == StatusPedido.CONFIRMADO && atual == StatusPedido.RECEBIDO) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> ResourceNotFoundException.of("Usuario", usuarioId));
            Map<Long, Necessidade> necessidade = calcularNecessidade(pedido);
            validarEstoque(necessidade);
            for (Necessidade n : necessidade.values()) {
                estoqueService.registrar(n.ingrediente, TipoMovimentacao.SAIDA, n.quantidade,
                        MotivoMovimentacao.VENDA, n.ingrediente.getCustoUnitario(), null, null,
                        usuario, pedido, null);
            }
        }
        pedido.setStatus(novo);
        return toResponse(pedidoRepository.save(pedido));
    }

    /** RF-18/RN-04: cancela pedido com motivo. Estorna estoque se ja havia baixa. */
    @Transactional
    public PedidoResponse cancelar(Long id, String motivo, Long usuarioId, Perfil perfilSolicitante) {
        Pedido pedido = buscar(id);
        StatusPedido atual = pedido.getStatus();

        if (atual == StatusPedido.CANCELADO) {
            throw new BusinessException("Pedido ja esta cancelado");
        }
        if (atual == StatusPedido.FINALIZADO) {
            throw new BusinessException("Pedido finalizado nao pode ser cancelado");
        }
        // RN-04: a partir de EM_PREPARO, somente GERENTE ou ADMIN podem cancelar
        boolean depoisDePreparo = atual == StatusPedido.EM_PREPARO || atual == StatusPedido.PRONTO
                || atual == StatusPedido.SAIU_ENTREGA;
        if (depoisDePreparo && perfilSolicitante != Perfil.ADMIN && perfilSolicitante != Perfil.GERENTE) {
            throw new BusinessException("Apos iniciar o preparo, somente GERENTE ou ADMIN podem cancelar");
        }

        // Estorno: se o estoque ja foi baixado (CONFIRMADO em diante), devolve os ingredientes
        boolean houveBaixa = atual != StatusPedido.RECEBIDO;
        if (houveBaixa) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> ResourceNotFoundException.of("Usuario", usuarioId));
            Map<Long, Necessidade> necessidade = calcularNecessidade(pedido);
            for (Necessidade n : necessidade.values()) {
                estoqueService.registrar(n.ingrediente, TipoMovimentacao.ESTORNO, n.quantidade,
                        MotivoMovimentacao.ESTORNO, n.ingrediente.getCustoUnitario(), null, null,
                        usuario, pedido, null);
            }
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setMotivoCancelamento(motivo);
        return toResponse(pedidoRepository.save(pedido));
    }

    // ===================== Helpers =====================

    /** Calcula a quantidade total necessaria de cada ingrediente para o pedido (via ficha tecnica). */
    private Map<Long, Necessidade> calcularNecessidade(Pedido pedido) {
        Map<Long, Necessidade> mapa = new LinkedHashMap<>();
        for (PedidoItem item : pedido.getItens()) {
            FichaTecnica ficha = fichaRepository.findByPratoId(item.getPrato().getId()).orElse(null);
            if (ficha == null || ficha.getItens().isEmpty()) {
                continue; // prato sem ficha nao movimenta estoque
            }
            int rendimento = ficha.getRendimento() <= 0 ? 1 : ficha.getRendimento();
            BigDecimal qtdPedido = BigDecimal.valueOf(item.getQuantidade());
            for (FichaTecnicaItem fi : ficha.getItens()) {
                // qtd_total = qtd_pedido x qtd_ficha x fator_correcao / rendimento
                BigDecimal qtd = qtdPedido
                        .multiply(fi.getQuantidade())
                        .multiply(fi.getFatorCorrecao())
                        .divide(BigDecimal.valueOf(rendimento), 3, RoundingMode.HALF_UP);
                Ingrediente ing = fi.getIngrediente();
                mapa.computeIfAbsent(ing.getId(), k -> new Necessidade(ing))
                        .quantidade = mapa.get(ing.getId()).quantidade.add(qtd);
            }
        }
        return mapa;
    }

    /** RN-03: confere saldo de cada ingrediente; lanca 422 listando o que falta. */
    private void validarEstoque(Map<Long, Necessidade> necessidade) {
        List<String> faltantes = new ArrayList<>();
        for (Necessidade n : necessidade.values()) {
            BigDecimal saldo = estoqueService.saldo(n.ingrediente.getId());
            if (saldo.compareTo(n.quantidade) < 0) {
                faltantes.add(String.format("%s: precisa %s%s, disponivel %s%s",
                        n.ingrediente.getNome(),
                        n.quantidade.stripTrailingZeros().toPlainString(), n.ingrediente.getUnidadePadrao().name(),
                        saldo.stripTrailingZeros().toPlainString(), n.ingrediente.getUnidadePadrao().name()));
            }
        }
        if (!faltantes.isEmpty()) {
            throw new InsufficientStockException(faltantes);
        }
    }

    private Pedido buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Pedido", id));
    }

    private PedidoResponse toResponse(Pedido p) {
        List<PedidoItemResponse> itens = p.getItens().stream()
                .map(i -> new PedidoItemResponse(i.getId(), i.getPrato().getId(), i.getPrato().getNome(),
                        i.getQuantidade(), i.getPrecoUnitario(),
                        i.getPrecoUnitario().multiply(BigDecimal.valueOf(i.getQuantidade())),
                        i.getObservacoes()))
                .toList();
        return new PedidoResponse(p.getId(), p.getCliente().getId(), p.getCliente().getNome(),
                p.getStatus().name(), p.getValorTotal(), p.getEnderecoEntrega(), p.getObservacoes(),
                p.getMotivoCancelamento(), p.getCreatedAt(), p.getUpdatedAt(), itens);
    }

    /** Acumula a necessidade de um ingrediente. */
    private static class Necessidade {
        final Ingrediente ingrediente;
        BigDecimal quantidade = BigDecimal.ZERO;
        Necessidade(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    }
}
