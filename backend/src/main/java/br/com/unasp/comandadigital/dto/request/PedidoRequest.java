package br.com.unasp.comandadigital.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PedidoRequest {

    private String enderecoEntrega;

    private String observacoes;

    @Valid
    @NotEmpty(message = "O pedido precisa de pelo menos 1 item")
    private List<PedidoItemRequest> itens = new ArrayList<>();
}
