package br.com.unasp.comandadigital.dto.request;

import br.com.unasp.comandadigital.model.UnidadePadrao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FornecedorProdutoRequest {

    @NotNull
    private Long ingredienteId;

    @NotNull
    @DecimalMin(value = "0.0001", message = "O preco deve ser maior que zero")
    private BigDecimal preco;

    @NotNull
    private UnidadePadrao unidadeVenda;
}
