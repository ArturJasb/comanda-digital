package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.request.PratoRequest;
import br.com.unasp.comandadigital.dto.response.PratoResponse;
import br.com.unasp.comandadigital.exception.BusinessException;
import br.com.unasp.comandadigital.exception.ResourceNotFoundException;
import br.com.unasp.comandadigital.model.Categoria;
import br.com.unasp.comandadigital.model.FichaTecnica;
import br.com.unasp.comandadigital.model.Prato;
import br.com.unasp.comandadigital.model.StatusPrato;
import br.com.unasp.comandadigital.repository.CategoriaRepository;
import br.com.unasp.comandadigital.repository.FichaTecnicaRepository;
import br.com.unasp.comandadigital.repository.PratoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PratoService {

    private final PratoRepository pratoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FichaTecnicaRepository fichaRepository;
    private final FichaTecnicaService fichaTecnicaService;

    public PratoService(PratoRepository pratoRepository, CategoriaRepository categoriaRepository,
                        FichaTecnicaRepository fichaRepository, FichaTecnicaService fichaTecnicaService) {
        this.pratoRepository = pratoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fichaRepository = fichaRepository;
        this.fichaTecnicaService = fichaTecnicaService;
    }

    @Transactional(readOnly = true)
    public Page<PratoResponse> listar(Pageable pageable) {
        return pratoRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PratoResponse buscarResponse(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public PratoResponse criar(PratoRequest req) {
        Prato p = new Prato();
        aplicar(p, req);
        return toResponse(pratoRepository.save(p));
    }

    @Transactional
    public PratoResponse atualizar(Long id, PratoRequest req) {
        Prato p = buscar(id);
        aplicar(p, req);
        return toResponse(pratoRepository.save(p));
    }

    /** RN-06: soft delete. */
    @Transactional
    public void desativar(Long id) {
        Prato p = buscar(id);
        p.setStatus(StatusPrato.INATIVO);
        pratoRepository.save(p);
    }

    public Prato buscar(Long id) {
        return pratoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Prato", id));
    }

    private void aplicar(Prato p, PratoRequest req) {
        Categoria categoria = categoriaRepository.findById(req.getCategoriaId())
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", req.getCategoriaId()));
        p.setCategoria(categoria);
        p.setNome(req.getNome());
        p.setDescricao(req.getDescricao());
        p.setFotoUrl(req.getFotoUrl());
        p.setPrecoVenda(req.getPrecoVenda());
        p.setTempoPreparoMin(req.getTempoPreparoMin());

        StatusPrato novoStatus = req.getStatus() != null ? req.getStatus() : StatusPrato.INATIVO;
        // RN-01: so pode ATIVAR se houver ficha tecnica com pelo menos 1 ingrediente
        if (novoStatus == StatusPrato.ATIVO && (p.getId() == null || !fichaTecnicaService.temFichaValida(p.getId()))) {
            throw new BusinessException(
                    "Nao e possivel ativar o prato sem uma ficha tecnica com pelo menos 1 ingrediente");
        }
        p.setStatus(novoStatus);
    }

    private PratoResponse toResponse(Prato p) {
        FichaTecnica ficha = fichaRepository.findByPratoId(p.getId()).orElse(null);
        FichaTecnicaService.Resultado r = fichaTecnicaService.calcular(ficha, p.getPrecoVenda());
        boolean temFicha = ficha != null && ficha.getItens() != null && !ficha.getItens().isEmpty();
        return new PratoResponse(p.getId(), p.getCategoria().getId(), p.getCategoria().getNome(),
                p.getNome(), p.getDescricao(), p.getFotoUrl(), p.getPrecoVenda(), p.getTempoPreparoMin(),
                p.getStatus().name(), r.custoTotal(), r.foodCostPct(), r.cor(), temFicha);
    }
}
