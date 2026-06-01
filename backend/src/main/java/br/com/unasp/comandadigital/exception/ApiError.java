package br.com.unasp.comandadigital.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/** Formato unico de erro (RNF-06 / SRS 4.8). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> detalhes,
        String path
) {
    public static ApiError of(int status, String error, String message, List<String> detalhes, String path) {
        return new ApiError(LocalDateTime.now(), status, error, message, detalhes, path);
    }
}
