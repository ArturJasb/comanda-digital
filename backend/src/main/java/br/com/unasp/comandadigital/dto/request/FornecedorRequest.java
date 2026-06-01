package br.com.unasp.comandadigital.dto.request;

import br.com.unasp.comandadigital.model.StatusGenerico;
import br.com.unasp.comandadigital.util.CNPJ;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FornecedorRequest {

    @NotBlank
    private String razaoSocial;

    // RN-07: CNPJ validado por algoritmo
    @NotBlank
    @CNPJ
    private String cnpj;

    private String telefone;

    private String email;

    private StatusGenerico status = StatusGenerico.ATIVO;

    @Valid
    private List<FornecedorProdutoRequest> produtos = new ArrayList<>();
}
