package br.com.unasp.comandadigital.dto.request;

import br.com.unasp.comandadigital.model.StatusGenerico;
import br.com.unasp.comandadigital.model.UnidadePadrao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredienteRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String sku;

    @NotNull
    private UnidadePadrao unidadePadrao;

    @NotNull
    @DecimalMin(value = "0.0", message = "O estoque minimo nao pode ser negativo")
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", message = "O custo unitario nao pode ser negativo")
    private BigDecimal custoUnitario = BigDecimal.ZERO;

    private StatusGenerico status = StatusGenerico.ATIVO;
}
