package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.request.FornecedorProdutoRequest;
import br.com.unasp.comandadigital.dto.request.FornecedorRequest;
import br.com.unasp.comandadigital.dto.response.CotacaoResponse;
import br.com.unasp.comandadigital.dto.response.FornecedorProdutoResponse;
import br.com.unasp.comandadigital.dto.response.FornecedorResponse;
import br.com.unasp.comandadigital.exception.BusinessException;
import br.com.unasp.comandadigital.exception.ResourceNotFoundException;
import br.com.unasp.comandadigital.model.*;
import br.com.unasp.comandadigital.repository.FornecedorProdutoRepository;
import br.com.unasp.comandadigital.repository.FornecedorRepository;
import br.com.unasp.comandadigital.repository.IngredienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final FornecedorProdutoRepository fornecedorProdutoRepository;
    private final IngredienteRepository ingredienteRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository,
                             FornecedorProdutoRepository fornecedorProdutoRepository,
                             IngredienteRepository ingredienteRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.fornecedorProdutoRepository = fornecedorProdutoRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listar() {
        return fornecedorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponse buscarResponse(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public FornecedorResponse criar(FornecedorRequest req) {
        if (fornecedorRepository.existsByCnpj(req.getCnpj())) {
            throw new BusinessException("Ja existe um fornecedor com este CNPJ");
        }
        Fornecedor f = new Fornecedor();
        f.setRazaoSocial(req.getRazaoSocial());
        f.setCnpj(req.getCnpj());
        f.setTelefone(req.getTelefone());
        f.setEmail(req.getEmail());
        f.setStatus(req.getStatus() != null ? req.getStatus() : StatusGenerico.ATIVO);
        aplicarProdutos(f, req.getProdutos());
        return toResponse(fornecedorRepository.save(f));
    }

    @Transactional
    public FornecedorResponse atualizar(Long id, FornecedorRequest req) {
        Fornecedor f = buscar(id);
        if (fornecedorRepository.existsByCnpjAndIdNot(req.getCnpj(), id)) {
            throw new BusinessException("Ja existe um fornecedor com este CNPJ");
        }
        f.setRazaoSocial(req.getRazaoSocial());
        f.setCnpj(req.getCnpj());
        f.setTelefone(req.getTelefone());
        f.setEmail(req.getEmail());
        if (req.getStatus() != null) {
            f.setStatus(req.getStatus());
        }
        // Remove o catalogo atual e FORCA o flush dos deletes antes de inserir os novos,
        // senao o UNIQUE(fornecedor_id, ingrediente_id) estoura quando o mesmo ingrediente
        // reaparece (Hibernate tende a inserir antes de deletar).
        f.getProdutos().clear();
        fornecedorRepository.saveAndFlush(f);
        aplicarProdutos(f, req.getProdutos());
        return toResponse(fornecedorRepository.save(f));
    }

    /** RN-06: soft delete. */
    @Transactional
    public void desativar(Long id) {
        Fornecedor f = buscar(id);
        f.setStatus(StatusGenerico.INATIVO);
        fornecedorRepository.save(f);
    }

    /** RF-23: cotacao comparativa - fornecedores que vendem o ingrediente, ordenado por preco. */
    @Transactional(readOnly = true)
    public CotacaoResponse cotacao(Long ingredienteId) {
        Ingrediente ing = ingredienteRepository.findById(ingredienteId)
                .orElseThrow(() -> ResourceNotFoundException.of("Ingrediente", ingredienteId));
        List<CotacaoResponse.Opcao> opcoes = fornecedorProdutoRepository
                .findByIngredienteIdOrderByPrecoAsc(ingredienteId).stream()
                .filter(fp -> fp.getFornecedor().getStatus() == StatusGenerico.ATIVO)
                .map(fp -> new CotacaoResponse.Opcao(fp.getId(), fp.getFornecedor().getId(),
                        fp.getFornecedor().getRazaoSocial(), fp.getPreco(), fp.getUnidadeVenda().name()))
                .toList();
        return new CotacaoResponse(ing.getId(), ing.getNome(), opcoes);
    }

    private void aplicarProdutos(Fornecedor f, List<FornecedorProdutoRequest> produtos) {
        if (produtos == null) return;
        for (FornecedorProdutoRequest pr : produtos) {
            Ingrediente ing = ingredienteRepository.findById(pr.getIngredienteId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Ingrediente", pr.getIngredienteId()));
            FornecedorProduto fp = new FornecedorProduto();
            fp.setFornecedor(f);
            fp.setIngrediente(ing);
            fp.setPreco(pr.getPreco());
            fp.setUnidadeVenda(pr.getUnidadeVenda());
            f.getProdutos().add(fp);
        }
    }

    public Fornecedor buscar(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Fornecedor", id));
    }

    private FornecedorResponse toResponse(Fornecedor f) {
        List<FornecedorProdutoResponse> produtos = f.getProdutos().stream()
                .map(fp -> new FornecedorProdutoResponse(fp.getId(), fp.getIngrediente().getId(),
                        fp.getIngrediente().getNome(), fp.getPreco(), fp.getUnidadeVenda().name()))
                .toList();
        return new FornecedorResponse(f.getId(), f.getRazaoSocial(), f.getCnpj(), f.getTelefone(),
                f.getEmail(), f.getStatus().name(), produtos);
    }
}
