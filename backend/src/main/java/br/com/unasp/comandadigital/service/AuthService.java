package br.com.unasp.comandadigital.service;

import br.com.unasp.comandadigital.dto.request.LoginRequest;
import br.com.unasp.comandadigital.dto.request.RegisterRequest;
import br.com.unasp.comandadigital.dto.response.AuthResponse;
import br.com.unasp.comandadigital.exception.BusinessException;
import br.com.unasp.comandadigital.model.Perfil;
import br.com.unasp.comandadigital.model.StatusGenerico;
import br.com.unasp.comandadigital.model.Usuario;
import br.com.unasp.comandadigital.repository.UsuarioRepository;
import br.com.unasp.comandadigital.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /** RF-38: login para qualquer perfil. Retorna JWT. */
    public AuthResponse login(LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getSenha()));
        } catch (AuthenticationException ex) {
            throw new BadCredentials();
        }
        Usuario usuario = usuarioRepository.findByEmail(req.getEmail())
                .orElseThrow(BadCredentials::new);
        return toAuth(usuario);
    }

    /** RF-39 / RN-10: cadastro publico (sempre perfil CLIENTE), email unico. */
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Ja existe um usuario com este email");
        }
        Usuario u = new Usuario();
        u.setNome(req.getNome());
        u.setEmail(req.getEmail());
        u.setSenhaHash(passwordEncoder.encode(req.getSenha()));
        u.setPerfil(Perfil.CLIENTE);
        u.setTelefone(req.getTelefone());
        u.setEndereco(req.getEndereco());
        u.setStatus(StatusGenerico.ATIVO);
        usuarioRepository.save(u);
        return toAuth(u);
    }

    private AuthResponse toAuth(Usuario usuario) {
        String token = jwtService.generateToken(usuario);
        return AuthResponse.bearer(token, usuario.getId(), usuario.getNome(),
                usuario.getEmail(), usuario.getPerfil().name());
    }

    /** Sinaliza 401 (mapeado no GlobalExceptionHandler). */
    public static class BadCredentials extends AuthenticationException {
        public BadCredentials() {
            super("Email ou senha invalidos");
        }
    }
}
