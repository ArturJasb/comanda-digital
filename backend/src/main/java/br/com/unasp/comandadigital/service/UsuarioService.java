package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.request.UsuarioRequest;
import br.com.unasp.comandadigital.dto.response.UsuarioResponse;
import br.com.unasp.comandadigital.exception.BusinessException;
import br.com.unasp.comandadigital.exception.ResourceNotFoundException;
import br.com.unasp.comandadigital.model.Perfil;
import br.com.unasp.comandadigital.model.StatusGenerico;
import br.com.unasp.comandadigital.model.Usuario;
import br.com.unasp.comandadigital.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private static final List<Perfil> PERFIS_INTERNOS = List.of(Perfil.ADMIN, Perfil.GERENTE, Perfil.COZINHEIRO);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** RF-41: lista usuarios internos (admin, gerente, cozinheiro). */
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarInternos(Pageable pageable) {
        return usuarioRepository.findByPerfilIn(PERFIS_INTERNOS, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarResponse(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest req) {
        // RN-10: email unico
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Ja existe um usuario com este email");
        }
        if (req.getSenha() == null || req.getSenha().isBlank()) {
            throw new BusinessException("A senha e obrigatoria na criacao do usuario");
        }
        Usuario u = new Usuario();
        u.setNome(req.getNome());
        u.setEmail(req.getEmail());
        u.setPerfil(req.getPerfil());
        u.setTelefone(req.getTelefone());
        u.setEndereco(req.getEndereco());
        u.setStatus(req.getStatus() != null ? req.getStatus() : StatusGenerico.ATIVO);
        u.setSenhaHash(passwordEncoder.encode(req.getSenha()));
        return toResponse(usuarioRepository.save(u));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest req) {
        Usuario u = buscar(id);
        if (!u.getEmail().equalsIgnoreCase(req.getEmail()) && usuarioRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Ja existe um usuario com este email");
        }
        u.setNome(req.getNome());
        u.setEmail(req.getEmail());
        u.setPerfil(req.getPerfil());
        u.setTelefone(req.getTelefone());
        u.setEndereco(req.getEndereco());
        if (req.getStatus() != null) {
            u.setStatus(req.getStatus());
        }
        if (req.getSenha() != null && !req.getSenha().isBlank()) {
            u.setSenhaHash(passwordEncoder.encode(req.getSenha()));
        }
        return toResponse(usuarioRepository.save(u));
    }

    /** RN-06: soft delete. */
    @Transactional
    public void desativar(Long id) {
        Usuario u = buscar(id);
        u.setStatus(StatusGenerico.INATIVO);
        usuarioRepository.save(u);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", id));
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil().name(),
                u.getTelefone(), u.getEndereco(), u.getStatus().name(), u.getCreatedAt());
    }
}
