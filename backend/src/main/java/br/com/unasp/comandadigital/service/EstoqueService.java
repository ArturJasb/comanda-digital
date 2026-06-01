package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.request.MovimentacaoManualRequest;
import br.com.unasp.comandadigital.dto.response.MovimentacaoResponse;
import br.com.unasp.comandadigital.dto.response.SaldoResponse;
import br.com.unasp.comandadigital.exception.BusinessException;
import br.com.unasp.comandadigital.exception.InsufficientStockException;
import br.com.unasp.comandadigital.exception.ResourceNotFoundException;
import br.com.unasp.comandadigital.model.*;
import br.com.unasp.comandadigital.repository.EstoqueMovimentacaoRepository;
import br.com.unasp.comandadigital.repository.IngredienteRepository;
import br.com.unasp.comandadigital.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class EstoqueService {

    private static final Set<MotivoMovimentacao> MOTIVOS_SAIDA_MANUAL =
            EnumSet.of(MotivoMovimentacao.DESPERDICIO, MotivoMovimentacao.VENCIMENTO,
                    MotivoMovimentacao.USO_INTERNO, MotivoMovimentacao.AJUSTE);

    private final EstoqueMovimentacaoRepository movimentacaoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final UsuarioRepository usuarioRepository;

    public EstoqueService(EstoqueMovimentacaoRepository movimentacaoRepository,
                          IngredienteRepository ingredienteRepository,
                          UsuarioRepository usuarioRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** RF-31: saldo de um ingrediente (entradas/estornos - saidas). */
    @Transactional(readOnly = true)
    public BigDecimal saldo(Long ingredienteId) {
        BigDecimal s = movimentacaoRepository.saldoDoIngrediente(ingredienteId);
        return s == null ? BigDecimal.ZERO : s;
    }

    /** RF-31: saldo de todos os ingredientes. */
    @Transactional(readOnly = true)
    public List<SaldoResponse> saldos() {
        return ingredienteRepository.findAll().stream().map(this::toSaldo).toList();
    }

    /** RF-32: ingredientes abaixo do estoque minimo. */
    @Transactional(readOnly = true)
    public List<SaldoResponse> alertas() {
        return saldos().stream().filter(SaldoResponse::abaixoMinimo).toList();
    }

    /** RF-33: historico de movimentacoes. */
    @Transactional(readOnly = true)
    public Page<MovimentacaoResponse> historico(Long ingredienteId, Pageable pageable) {
        Page<EstoqueMovimentacao> page = (ingredienteId == null)
                ? movimentacaoRepository.findAllByOrderByCreatedAtDesc(pageable)
                : movimentacaoRepository.findByIngredienteIdOrderByCreatedAtDesc(ingredienteId, pageable);
        return page.map(this::toResponse);
    }

    /** RF-30: saida manual (perda) com motivo obrigatorio. */
    @Transactional
    public MovimentacaoResponse saidaManual(MovimentacaoManualRequest req, Long usuarioId) {
        if (!MOTIVOS_SAIDA_MANUAL.contains(req.getMotivo())) {
            throw new BusinessException("Motivo invalido para saida manual: " + req.getMotivo());
        }
        Ingrediente ing = ingredienteRepository.findById(req.getIngredienteId())
                .orElseThrow(() -> ResourceNotFoundException.of("Ingrediente", req.getIngredienteId()));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", usuarioId));

        BigDecimal saldoAtual = saldo(ing.getId());
        if (saldoAtual.compareTo(req.getQuantidade()) < 0) {
            throw new InsufficientStockException(List.of(String.format(
                    "%s: precisa %s, disponivel %s", ing.getNome(),
                    req.getQuantidade().stripTrailingZeros().toPlainString(),
                    saldoAtual.stripTrailingZeros().toPlainString())));
        }
        EstoqueMovimentacao mov = registrar(ing, TipoMovimentacao.SAIDA, req.getQuantidade(),
                req.getMotivo(), ing.getCustoUnitario(), req.getLote(), req.getValidade(),
                usuario, null, null);
        return toResponse(mov);
    }

    /** Cria e persiste uma movimentacao (reutilizado por pedido e compra). */
    @Transactional
    public EstoqueMovimentacao registrar(Ingrediente ingrediente, TipoMovimentacao tipo, BigDecimal quantidade,
                                         MotivoMovimentacao motivo, BigDecimal custoUnitario, String lote,
                                         LocalDate validade, Usuario usuario, Pedido pedido, PedidoCompra pedidoCompra) {
        EstoqueMovimentacao mov = new EstoqueMovimentacao();
        mov.setIngrediente(ingrediente);
        mov.setTipo(tipo);
        mov.setQuantidade(quantidade);
        mov.setMotivo(motivo);
        mov.setCustoUnitario(custoUnitario);
        mov.setLote(lote);
        mov.setValidade(validade);
        mov.setUsuario(usuario);
        mov.setPedido(pedido);
        mov.setPedidoCompra(pedidoCompra);
        return movimentacaoRepository.save(mov);
    }

    private SaldoResponse toSaldo(Ingrediente i) {
        BigDecimal saldo = saldo(i.getId());
        boolean abaixo = saldo.compareTo(i.getEstoqueMinimo()) < 0;
        return new SaldoResponse(i.getId(), i.getNome(), i.getSku(), i.getUnidadePadrao().name(),
                saldo, i.getEstoqueMinimo(), abaixo);
    }

    private MovimentacaoResponse toResponse(EstoqueMovimentacao m) {
        return new MovimentacaoResponse(m.getId(), m.getIngrediente().getId(), m.getIngrediente().getNome(),
                m.getTipo().name(), m.getQuantidade(), m.getMotivo().name(), m.getLote(), m.getValidade(),
                m.getCustoUnitario(),
                m.getPedido() != null ? m.getPedido().getId() : null,
                m.getPedidoCompra() != null ? m.getPedidoCompra().getId() : null,
                m.getUsuario() != null ? m.getUsuario().getNome() : null,
                m.getCreatedAt());
    }
}
