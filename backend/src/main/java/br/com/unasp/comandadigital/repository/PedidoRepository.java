package br.com.unasp.comandadigital.repository;

import br.com.unasp.comandadigital.model.Pedido;
import br.com.unasp.comandadigital.model.StatusPedido;
import br.com.unasp.comandadigital.repository.projection.VendaDiaView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Page<Pedido> findByClienteIdOrderByCreatedAtDesc(Long clienteId, Pageable pageable);

    Page<Pedido> findByStatus(StatusPedido status, Pageable pageable);

    // ---- KPIs do dashboard (RF-34) ----
    @Query(value = "SELECT COALESCE(SUM(valor_total),0) FROM pedido " +
            "WHERE status <> 'CANCELADO' AND created_at BETWEEN :inicio AND :fim", nativeQuery = true)
    BigDecimal faturamentoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(value = "SELECT COUNT(*) FROM pedido " +
            "WHERE status <> 'CANCELADO' AND created_at BETWEEN :inicio AND :fim", nativeQuery = true)
    long contarPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Vendas por periodo - faturamento diario (RF-37)
    @Query(value = "SELECT DATE(created_at) AS dia, COALESCE(SUM(valor_total),0) AS total FROM pedido " +
            "WHERE status <> 'CANCELADO' AND created_at BETWEEN :inicio AND :fim " +
            "GROUP BY DATE(created_at) ORDER BY dia", nativeQuery = true)
    List<VendaDiaView> vendasPorDia(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
