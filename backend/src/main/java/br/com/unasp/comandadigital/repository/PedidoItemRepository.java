package br.com.unasp.comandadigital.repository;

import br.com.unasp.comandadigital.model.PedidoItem;
import br.com.unasp.comandadigital.repository.projection.TopPratoView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {

    // Top 5 pratos mais vendidos no periodo (RF-35)
    @Query(value = "SELECT pi.prato_id AS pratoId, pr.nome AS nome, " +
            "SUM(pi.quantidade) AS quantidade, SUM(pi.quantidade * pi.preco_unitario) AS total " +
            "FROM pedido_item pi " +
            "JOIN pedido p ON p.id = pi.pedido_id " +
            "JOIN prato pr ON pr.id = pi.prato_id " +
            "WHERE p.status <> 'CANCELADO' AND p.created_at BETWEEN :inicio AND :fim " +
            "GROUP BY pi.prato_id, pr.nome " +
            "ORDER BY quantidade DESC " +
            "LIMIT 5", nativeQuery = true)
    List<TopPratoView> topPratos(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
