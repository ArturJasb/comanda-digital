package br.com.unasp.comandadigital.repository.projection;

import java.math.BigDecimal;

public interface TopPratoView {
    Long getPratoId();
    String getNome();
    Long getQuantidade();
    BigDecimal getTotal();
}
