package br.com.unasp.comandadigital.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "fornecedor_produto",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fornecedor_id", "ingrediente_id"}))
@Getter
@Setter
@NoArgsConstructor
public class FornecedorProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_venda", nullable = false, length = 10)
    private UnidadePadrao unidadeVenda;
}
