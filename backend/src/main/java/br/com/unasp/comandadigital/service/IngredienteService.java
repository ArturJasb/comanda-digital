package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.request.IngredienteRequest;
import br.com.unasp.comandadigital.dto.response.IngredienteResponse;
import br.com.unasp.comandadigital.exception.BusinessException;
import br.com.unasp.comandadigital.exception.ResourceNotFoundException;
import br.com.unasp.comandadigital.model.Ingrediente;
import br.com.unasp.comandadigital.model.StatusGenerico;
import br.com.unasp.comandadigital.repository.EstoqueMovimentacaoRepository;
import br.com.unasp.comandadigital.repository.IngredienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final EstoqueMovimentacaoRepository movimentacaoRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository,
                              EstoqueMovimentacaoRepository movimentacaoRepository) {
        this.ingredienteRepository = ingredienteRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<IngredienteResponse> listar() {
        return ingredienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public IngredienteResponse buscarResponse(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public IngredienteResponse criar(IngredienteRequest req) {
        if (ingredienteRepository.existsBySku(req.getSku())) {
            throw new BusinessException("Ja existe um ingrediente com o SKU " + req.getSku());
        }
        Ingrediente i = new Ingrediente();
        aplicar(i, req);
        return toResponse(ingredienteRepository.save(i));
    }

    @Transactional
    public IngredienteResponse atualizar(Long id, IngredienteRequest req) {
        Ingrediente i = buscar(id);
        if (ingredienteRepository.existsBySkuAndIdNot(req.getSku(), id)) {
            throw new BusinessException("Ja existe um ingrediente com o SKU " + req.getSku());
        }
        aplicar(i, req);
        return toResponse(ingredienteRepository.save(i));
    }

    /** RN-06: soft delete. */
    @Transactional
    public void desativar(Long id) {
        Ingrediente i = buscar(id);
        i.setStatus(StatusGenerico.INATIVO);
        ingredienteRepository.save(i);
    }

    public Ingrediente buscar(Long id) {
        return ingredienteRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Ingrediente", id));
    }

    private void aplicar(Ingrediente i, IngredienteRequest req) {
        i.setNome(req.getNome());
        i.setSku(req.getSku());
        i.setUnidadePadrao(req.getUnidadePadrao());
        i.setEstoqueMinimo(req.getEstoqueMinimo());
        i.setCustoUnitario(req.getCustoUnitario());
        if (req.getStatus() != null) {
            i.setStatus(req.getStatus());
        }
    }

    private IngredienteResponse toResponse(Ingrediente i) {
        BigDecimal saldo = movimentacaoRepository.saldoDoIngrediente(i.getId());
        if (saldo == null) saldo = BigDecimal.ZERO;
        boolean abaixo = saldo.compareTo(i.getEstoqueMinimo()) < 0;
        return new IngredienteResponse(i.getId(), i.getNome(), i.getSku(),
                i.getUnidadePadrao().name(), i.getEstoqueMinimo(), i.getCustoUnitario(),
                i.getStatus().name(), saldo, abaixo);
    }
}
