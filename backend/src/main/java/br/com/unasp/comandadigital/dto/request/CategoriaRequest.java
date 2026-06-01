package br.com.unasp.comandadigital.dto.request;

import br.com.unasp.comandadigital.model.StatusGenerico;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequest {

    @NotBlank
    private String nome;

    private String descricao;

    private Integer ordem = 0;

    private StatusGenerico status = StatusGenerico.ATIVO;
}
