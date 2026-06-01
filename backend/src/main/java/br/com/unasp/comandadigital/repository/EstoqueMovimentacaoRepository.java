package br.com.unasp.comandadigital.repository;

import br.com.unasp.comandadigital.model.EstoqueMovimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface EstoqueMovimentacaoRepository extends JpaRepository<EstoqueMovimentacao, Long> {

    // Saldo de um ingrediente (SRS 4.6): entradas/estornos somam, saidas subtraem
    @Query(value = "SELECT COALESCE(SUM(CASE WHEN tipo IN ('ENTRADA','ESTORNO') THEN quantidade ELSE -quantidade END), 0) " +
            "FROM estoque_movimentacao WHERE ingrediente_id = :ingredienteId", nativeQuery = true)
    BigDecimal saldoDoIngrediente(@Param("ingredienteId") Long ingredienteId);

    Page<EstoqueMovimentacao> findByIngredienteIdOrderByCreatedAtDesc(Long ingredienteId, Pageable pageable);

    Page<EstoqueMovimentacao> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
