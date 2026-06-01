package br.com.unasp.comandadigital.dto.response;

public record AuthResponse(
        String token,
        String tipo,
        Long id,
        String nome,
        String email,
        String perfil
) {
    public static AuthResponse bearer(String token, Long id, String nome, String email, String perfil) {
        return new AuthResponse(token, "Bearer", id, nome, email, perfil);
    }
}
