package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.request.LoginRequest;
import br.com.unasp.comandadigital.dto.request.RegisterRequest;
import br.com.unasp.comandadigital.dto.response.AuthResponse;
import br.com.unasp.comandadigital.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticacao", description = "Login e cadastro (publico)")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login (RF-38) - retorna JWT para qualquer perfil")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @Operation(summary = "Cadastro publico de cliente (RF-39) - perfil CLIENTE")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }
}
