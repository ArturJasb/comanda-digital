package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.request.CategoriaRequest;
import br.com.unasp.comandadigital.dto.response.CategoriaResponse;
import br.com.unasp.comandadigital.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categorias (admin)", description = "CRUD de categorias (RF-09)")
@RestController
@RequestMapping("/api/admin/categorias")
@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Lista categorias")
    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaService.listar();
    }

    @Operation(summary = "Cria categoria")
    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criar(req));
    }

    @Operation(summary = "Edita categoria")
    @PutMapping("/{id}")
    public CategoriaResponse atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest req) {
        return categoriaService.atualizar(id, req);
    }

    @Operation(summary = "Desativa categoria (soft delete - RN-06)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        categoriaService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
