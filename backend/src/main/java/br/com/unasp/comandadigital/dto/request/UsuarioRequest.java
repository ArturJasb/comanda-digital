package br.com.unasp.comandadigital.dto.request;

import br.com.unasp.comandadigital.model.Perfil;
import br.com.unasp.comandadigital.model.StatusGenerico;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioRequest {

    @NotBlank
    private String nome;

    @NotBlank
    @Email
    private String email;

    // Obrigatoria na criacao; opcional na edicao (mantem a atual se vazia)
    private String senha;

    @NotNull
    private Perfil perfil;

    private String telefone;

    private String endereco;

    private StatusGenerico status = StatusGenerico.ATIVO;
}
