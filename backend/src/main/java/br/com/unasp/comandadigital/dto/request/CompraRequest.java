package br.com.unasp.comandadigital.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompraRequest {

    @NotNull
    private Long fornecedorId;

    @Valid
    @NotEmpty(message = "O pedido de compra precisa de pelo menos 1 item")
    private List<CompraItemRequest> itens = new ArrayList<>();
}
