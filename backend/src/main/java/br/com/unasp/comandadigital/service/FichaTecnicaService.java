package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.request.FichaItemRequest;
import br.com.unasp.comandadigital.dto.request.FichaTecnicaRequest;
import br.com.unasp.comandadigital.dto.response.CustoResponse;
import br.com.unasp.comandadigital.dto.response.FichaItemResponse;
import br.com.unasp.comandadigital.dto.response.FichaTecnicaResponse;
import br.com.unasp.comandadigital.exception.ResourceNotFoundException;
import br.com.unasp.comandadigital.model.*;
import br.com.unasp.comandadigital.repository.FichaTecnicaRepository;
import br.com.unasp.comandadigital.repository.IngredienteRepository;
import br.com.unasp.comandadigital.repository.PratoRepository;
import br.com.unasp.comandadigital.util.FoodCostUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class FichaTecnicaService {

    private final FichaTecnicaRepository fichaRepository;
    private final PratoRepository pratoRepository;
    private final IngredienteRepository ingredienteRepository;

    public FichaTecnicaService(FichaTecnicaRepository fichaRepository, PratoRepository pratoRepository,
                               IngredienteRepository ingredienteRepository) {
        this.fichaRepository = fichaRepository;
        this.pratoRepository = pratoRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    /** RN-01: existe ficha com pelo menos 1 ingrediente? */
    @Transactional(readOnly = true)
    public boolean temFichaValida(Long pratoId) {
        return fichaRepository.findByPratoId(pratoId)
                .map(f -> f.getItens() != null && !f.getItens().isEmpty())
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public FichaTecnicaResponse buscarPorPrato(Long pratoId) {
        FichaTecnica ficha = fichaRepository.findByPratoId(pratoId)
                .orElseThrow(() -> new ResourceNotFoundException("Prato " + pratoId + " ainda nao possui ficha tecnica"));
        return toResponse(ficha);
    }

    /** POST/PUT /api/admin/pratos/{id}/ficha - cria ou substitui a ficha. */
    @Transactional
    public FichaTecnicaResponse salvar(Long pratoId, FichaTecnicaRequest req) {
        Prato prato = pratoRepository.findById(pratoId)
                .orElseThrow(() -> ResourceNotFoundException.of("Prato", pratoId));

        FichaTecnica ficha = fichaRepository.findByPratoId(pratoId).orElseGet(() -> {
            FichaTecnica nova = new FichaTecnica();
            nova.setPrato(prato);
            return nova;
        });
        ficha.setRendimento(req.getRendimento() != null ? req.getRendimento() : 1);
        ficha.setModoPreparo(req.getModoPreparo());

        ficha.getItens().clear();
        for (FichaItemRequest itemReq : req.getItens()) {
            Ingrediente ing = ingredienteRepository.findById(itemReq.getIngredienteId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Ingrediente", itemReq.getIngredienteId()));
            FichaTecnicaItem item = new FichaTecnicaItem();
            item.setFichaTecnica(ficha);
            item.setIngrediente(ing);
            item.setQuantidade(itemReq.getQuantidade());
            item.setUnidade(itemReq.getUnidade());
            item.setFatorCorrecao(itemReq.getFatorCorrecao() != null ? itemReq.getFatorCorrecao() : BigDecimal.ONE);
            ficha.getItens().add(item);
        }
        return toResponse(fichaRepository.save(ficha));
    }

    /** GET /api/admin/pratos/{id}/custo */
    @Transactional(readOnly = true)
    public CustoResponse custoDoPrato(Long pratoId) {
        Prato prato = pratoRepository.findById(pratoId)
                .orElseThrow(() -> ResourceNotFoundException.of("Prato", pratoId));
        Resultado r = calcular(fichaRepository.findByPratoId(pratoId).orElse(null), prato.getPrecoVenda());
        return new CustoResponse(pratoId, r.custoTotal(), prato.getPrecoVenda(),
                r.foodCostPct(), r.cor(), r.warning());
    }

    /** Calculo central de custo/food cost (SRS 3.2). */
    public Resultado calcular(FichaTecnica ficha, BigDecimal precoVenda) {
        if (ficha == null || ficha.getItens() == null || ficha.getItens().isEmpty()) {
            return new Resultado(BigDecimal.ZERO, BigDecimal.ZERO, "verde", null);
        }
        BigDecimal soma = BigDecimal.ZERO;
        for (FichaTecnicaItem item : ficha.getItens()) {
            soma = soma.add(FoodCostUtil.custoItem(item.getQuantidade(), item.getFatorCorrecao(),
                    item.getIngrediente().getCustoUnitario()));
        }
        BigDecimal custoTotal = FoodCostUtil.custoTotal(soma, ficha.getRendimento());
        BigDecimal pct = FoodCostUtil.foodCostPct(custoTotal, precoVenda);
        return new Resultado(custoTotal, pct, FoodCostUtil.cor(pct), FoodCostUtil.warning(pct));
    }

    private FichaTecnicaResponse toResponse(FichaTecnica ficha) {
        Prato prato = ficha.getPrato();
        Resultado r = calcular(ficha, prato.getPrecoVenda());
        List<FichaItemResponse> itens = new ArrayList<>();
        for (FichaTecnicaItem item : ficha.getItens()) {
            Ingrediente ing = item.getIngrediente();
            BigDecimal custoItem = FoodCostUtil.custoItem(item.getQuantidade(), item.getFatorCorrecao(),
                    ing.getCustoUnitario());
            itens.add(new FichaItemResponse(item.getId(), ing.getId(), ing.getNome(),
                    item.getQuantidade(), item.getUnidade().name(), item.getFatorCorrecao(),
                    ing.getCustoUnitario(), custoItem.stripTrailingZeros()));
        }
        return new FichaTecnicaResponse(ficha.getId(), prato.getId(), prato.getNome(),
                ficha.getRendimento(), ficha.getModoPreparo(), itens,
                r.custoTotal(), prato.getPrecoVenda(), r.foodCostPct(), r.cor(), r.warning());
    }

    /** Resultado do calculo de custo. */
    public record Resultado(BigDecimal custoTotal, BigDecimal foodCostPct, String cor, String warning) {}
}
