package br.com.unasp.comandadigital.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FichaTecnicaRequest {

    @NotNull
    @Min(value = 1, message = "O rendimento deve ser no minimo 1")
    private Integer rendimento = 1;

    private String modoPreparo;

    // RN-01: ficha precisa de pelo menos 1 ingrediente
    @Valid
    @NotEmpty(message = "A ficha tecnica precisa de pelo menos 1 ingrediente")
    private List<FichaItemRequest> itens = new ArrayList<>();
}
