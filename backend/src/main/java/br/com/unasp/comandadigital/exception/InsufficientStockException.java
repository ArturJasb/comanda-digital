package br.com.unasp.comandadigital.exception;

import java.util.List;

/** RN-03: estoque insuficiente para atender o pedido (HTTP 422). */
public class InsufficientStockException extends RuntimeException {

    private final List<String> detalhes;

    public InsufficientStockException(List<String> detalhes) {
        super("Estoque insuficiente para um ou mais ingredientes");
        this.detalhes = detalhes;
    }

    public List<String> getDetalhes() {
        return detalhes;
    }
}
