package br.com.unasp.comandadigital.repository;

import br.com.unasp.comandadigital.model.FichaTecnica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FichaTecnicaRepository extends JpaRepository<FichaTecnica, Long> {

    Optional<FichaTecnica> findByPratoId(Long pratoId);

    boolean existsByPratoId(Long pratoId);
}
