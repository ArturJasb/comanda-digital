package br.com.unasp.comandadigital.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prato")
@Getter
@Setter
@NoArgsConstructor
public class Prato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "tempo_preparo_min")
    private Integer tempoPreparoMin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPrato status = StatusPrato.INATIVO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
