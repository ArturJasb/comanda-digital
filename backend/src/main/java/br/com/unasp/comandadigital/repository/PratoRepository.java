package br.com.unasp.comandadigital.repository;

import br.com.unasp.comandadigital.model.Prato;
import br.com.unasp.comandadigital.model.StatusPrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PratoRepository extends JpaRepository<Prato, Long> {

    // Cardapio publico (RN-09): apenas ATIVO
    List<Prato> findByStatus(StatusPrato status);

    List<Prato> findByStatusAndCategoriaId(StatusPrato status, Long categoriaId);

    boolean existsByCategoriaId(Long categoriaId);
}
