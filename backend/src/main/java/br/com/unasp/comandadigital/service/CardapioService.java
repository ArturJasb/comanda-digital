package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.response.CardapioItemResponse;
import br.com.unasp.comandadigital.dto.response.CategoriaResponse;
import br.com.unasp.comandadigital.exception.ResourceNotFoundException;
import br.com.unasp.comandadigital.model.Prato;
import br.com.unasp.comandadigital.model.StatusGenerico;
import br.com.unasp.comandadigital.model.StatusPrato;
import br.com.unasp.comandadigital.repository.CategoriaRepository;
import br.com.unasp.comandadigital.repository.PratoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CardapioService {

    private final PratoRepository pratoRepository;
    private final CategoriaRepository categoriaRepository;

    public CardapioService(PratoRepository pratoRepository, CategoriaRepository categoriaRepository) {
        this.pratoRepository = pratoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /** Categorias ativas para os chips de filtro do cardapio publico. */
    @Transactional(readOnly = true)
    public List<CategoriaResponse> categorias() {
        return categoriaRepository.findByStatusOrderByOrdemAsc(StatusGenerico.ATIVO).stream()
                .map(c -> new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao(),
                        c.getOrdem(), c.getStatus().name()))
                .toList();
    }

    /** RF-01 / RN-09: cardapio publico lista apenas pratos ATIVOS (filtro opcional por categoria). */
    @Transactional(readOnly = true)
    public List<CardapioItemResponse> listar(Long categoriaId) {
        List<Prato> pratos = (categoriaId == null)
                ? pratoRepository.findByStatus(StatusPrato.ATIVO)
                : pratoRepository.findByStatusAndCategoriaId(StatusPrato.ATIVO, categoriaId);
        return pratos.stream().map(this::toResponse).toList();
    }

    /** RF-02: detalhe de um prato (apenas se ATIVO). */
    @Transactional(readOnly = true)
    public CardapioItemResponse detalhe(Long id) {
        Prato p = pratoRepository.findById(id)
                .filter(prato -> prato.getStatus() == StatusPrato.ATIVO)
                .orElseThrow(() -> ResourceNotFoundException.of("Prato", id));
        return toResponse(p);
    }

    private CardapioItemResponse toResponse(Prato p) {
        return new CardapioItemResponse(p.getId(), p.getCategoria().getId(), p.getCategoria().getNome(),
                p.getNome(), p.getDescricao(), p.getFotoUrl(), p.getPrecoVenda(), p.getTempoPreparoMin());
    }
}
