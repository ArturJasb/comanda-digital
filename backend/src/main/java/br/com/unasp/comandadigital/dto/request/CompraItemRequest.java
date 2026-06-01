package br.com.unasp.comandadigital.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompraItemRequest {

    @NotNull
    private Long ingredienteId;

    @NotNull
    @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero")
    private BigDecimal quantidade;

    @NotNull
    @DecimalMin(value = "0.0001", message = "O preco unitario deve ser maior que zero")
    private BigDecimal precoUnitario;
}
