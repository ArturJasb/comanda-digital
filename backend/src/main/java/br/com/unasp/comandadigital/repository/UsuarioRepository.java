package br.com.unasp.comandadigital.repository;

import br.com.unasp.comandadigital.model.Perfil;
import br.com.unasp.comandadigital.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Usuario> findByPerfilIn(List<Perfil> perfis, Pageable pageable);
}
