package br.com.unasp.comandadigital.dto.request;

import br.com.unasp.comandadigital.model.StatusPrato;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PratoRequest {

    @NotBlank
    private String nome;

    private String descricao;

    private String fotoUrl;

    @NotNull
    @DecimalMin(value = "0.01", message = "O preco de venda deve ser maior que zero")
    private BigDecimal precoVenda;

    private Integer tempoPreparoMin;

    @NotNull
    private Long categoriaId;

    private StatusPrato status = StatusPrato.INATIVO;
}
