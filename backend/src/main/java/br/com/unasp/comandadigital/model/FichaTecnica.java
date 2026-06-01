package br.com.unasp.comandadigital.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ficha_tecnica")
@Getter
@Setter
@NoArgsConstructor
public class FichaTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prato_id", nullable = false, unique = true)
    private Prato prato;

    @Column(nullable = false)
    private Integer rendimento = 1;

    @Column(name = "modo_preparo", columnDefinition = "TEXT")
    private String modoPreparo;

    @OneToMany(mappedBy = "fichaTecnica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FichaTecnicaItem> itens = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
