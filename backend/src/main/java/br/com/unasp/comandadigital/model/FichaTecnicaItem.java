package br.com.unasp.comandadigital.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ficha_tecnica_item")
@Getter
@Setter
@NoArgsConstructor
public class FichaTecnicaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_tecnica_id", nullable = false)
    private FichaTecnica fichaTecnica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UnidadePadrao unidade;

    @Column(name = "fator_correcao", nullable = false, precision = 6, scale = 2)
    private BigDecimal fatorCorrecao = BigDecimal.ONE;
}
