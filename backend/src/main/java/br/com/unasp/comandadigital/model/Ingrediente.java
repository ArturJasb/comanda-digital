package br.com.unasp.comandadigital.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingrediente")
@Getter
@Setter
@NoArgsConstructor
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 60)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_padrao", nullable = false, length = 10)
    private UnidadePadrao unidadePadrao;

    @Column(name = "estoque_minimo", nullable = false, precision = 12, scale = 3)
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;

    @Column(name = "custo_unitario", nullable = false, precision = 10, scale = 4)
    private BigDecimal custoUnitario = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusGenerico status = StatusGenerico.ATIVO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
