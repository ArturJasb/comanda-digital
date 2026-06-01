package br.com.unasp.comandadigital.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidoItemRequest {

    @NotNull
    private Long pratoId;

    @NotNull
    @Min(value = 1, message = "A quantidade deve ser no minimo 1")
    private Integer quantidade;

    private String observacoes;
}
