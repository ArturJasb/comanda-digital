package br.com.unasp.comandadigital.repository;

import br.com.unasp.comandadigital.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);
}
