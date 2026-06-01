package br.com.unasp.comandadigital.dto.request;

import br.com.unasp.comandadigital.model.StatusPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusPedidoRequest {

    @NotNull
    private StatusPedido status;
}
