package br.com.unasp.comandadigital.repository.projection;

import java.math.BigDecimal;

public interface VendaDiaView {
    String getDia();
    BigDecimal getTotal();
}
