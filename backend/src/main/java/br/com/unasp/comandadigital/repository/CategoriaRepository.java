package br.com.unasp.comandadigital.repository;

import br.com.unasp.comandadigital.model.Categoria;
import br.com.unasp.comandadigital.model.StatusGenerico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByStatusOrderByOrdemAsc(StatusGenerico status);

    List<Categoria> findAllByOrderByOrdemAsc();
}
