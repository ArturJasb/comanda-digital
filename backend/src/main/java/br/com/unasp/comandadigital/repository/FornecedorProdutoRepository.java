package br.com.unasp.comandadigital.repository;

import br.com.unasp.comandadigital.model.FornecedorProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FornecedorProdutoRepository extends JpaRepository<FornecedorProduto, Long> {

    // Cotacao comparativa (RF-23): fornecedores que vendem o ingrediente, ordenado por preco
    List<FornecedorProduto> findByIngredienteIdOrderByPrecoAsc(Long ingredienteId);

    List<FornecedorProduto> findByFornecedorId(Long fornecedorId);
}
