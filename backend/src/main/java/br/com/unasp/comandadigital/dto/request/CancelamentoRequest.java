package br.com.unasp.comandadigital.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelamentoRequest {

    // RF-18: cancelamento exige motivo
    @NotBlank(message = "O motivo do cancelamento e obrigatorio")
    private String motivo;
}
