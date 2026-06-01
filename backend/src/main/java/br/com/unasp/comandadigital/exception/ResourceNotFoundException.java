package br.com.unasp.comandadigital.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String entidade, Long id) {
        return new ResourceNotFoundException(entidade + " nao encontrado(a) com id " + id);
    }
}
