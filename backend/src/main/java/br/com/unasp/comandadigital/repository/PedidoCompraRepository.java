package br.com.unasp.comandadigital.repository;

import br.com.unasp.comandadigital.model.PedidoCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long> {
}
