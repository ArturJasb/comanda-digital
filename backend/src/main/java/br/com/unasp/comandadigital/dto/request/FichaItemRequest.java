package br.com.unasp.comandadigital.dto.request;

import br.com.unasp.comandadigital.model.UnidadePadrao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FichaItemRequest {

    @NotNull
    private Long ingredienteId;

    @NotNull
    @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero")
    private BigDecimal quantidade;

    @NotNull
    private UnidadePadrao unidade;

    // RN-08: fator de correcao nao pode ser menor que 1.0
    @NotNull
    @DecimalMin(value = "1.0", message = "O fator de correcao nao pode ser menor que 1.0")
    private BigDecimal fatorCorrecao = BigDecimal.ONE;
}
