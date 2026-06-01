package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.request.CategoriaRequest;
import br.com.unasp.comandadigital.dto.response.CategoriaResponse;
import br.com.unasp.comandadigital.exception.ResourceNotFoundException;
import br.com.unasp.comandadigital.model.Categoria;
import br.com.unasp.comandadigital.model.StatusGenerico;
import br.com.unasp.comandadigital.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAllByOrderByOrdemAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest req) {
        Categoria c = new Categoria();
        aplicar(c, req);
        return toResponse(categoriaRepository.save(c));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest req) {
        Categoria c = buscar(id);
        aplicar(c, req);
        return toResponse(categoriaRepository.save(c));
    }

    /** RN-06: soft delete (status = INATIVO). */
    @Transactional
    public void desativar(Long id) {
        Categoria c = buscar(id);
        c.setStatus(StatusGenerico.INATIVO);
        categoriaRepository.save(c);
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", id));
    }

    private void aplicar(Categoria c, CategoriaRequest req) {
        c.setNome(req.getNome());
        c.setDescricao(req.getDescricao());
        c.setOrdem(req.getOrdem() != null ? req.getOrdem() : 0);
        if (req.getStatus() != null) {
            c.setStatus(req.getStatus());
        }
    }

    private CategoriaResponse toResponse(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao(),
                c.getOrdem(), c.getStatus().name());
    }
}
