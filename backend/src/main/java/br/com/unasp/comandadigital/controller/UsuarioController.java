package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.request.UsuarioRequest;
import br.com.unasp.comandadigital.dto.response.UsuarioResponse;
import br.com.unasp.comandadigital.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuarios internos (admin)", description = "CRUD de usuarios internos (RF-41)")
@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Lista usuarios internos (admin, gerente, cozinheiro)")
    @GetMapping
    public Page<UsuarioResponse> listar(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
                                        Pageable pageable) {
        return usuarioService.listarInternos(pageable);
    }

    @Operation(summary = "Detalhe do usuario")
    @GetMapping("/{id}")
    public UsuarioResponse detalhe(@PathVariable Long id) {
        return usuarioService.buscarResponse(id);
    }

    @Operation(summary = "Cria usuario interno (senha com BCrypt)")
    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(req));
    }

    @Operation(summary = "Edita usuario interno")
    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req) {
        return usuarioService.atualizar(id, req);
    }

    @Operation(summary = "Desativa usuario interno (soft delete - RN-06)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
