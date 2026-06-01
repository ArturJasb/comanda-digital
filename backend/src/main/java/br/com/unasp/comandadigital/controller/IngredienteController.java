package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.request.IngredienteRequest;
import br.com.unasp.comandadigital.dto.response.IngredienteResponse;
import br.com.unasp.comandadigital.service.IngredienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ingredientes (admin)", description = "CRUD de ingredientes (RF-27)")
@RestController
@RequestMapping("/api/admin/ingredientes")
@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @Operation(summary = "Lista ingredientes (com saldo atual)")
    @GetMapping
    public List<IngredienteResponse> listar() {
        return ingredienteService.listar();
    }

    @Operation(summary = "Detalhe do ingrediente")
    @GetMapping("/{id}")
    public IngredienteResponse detalhe(@PathVariable Long id) {
        return ingredienteService.buscarResponse(id);
    }

    @Operation(summary = "Cria ingrediente")
    @PostMapping
    public ResponseEntity<IngredienteResponse> criar(@Valid @RequestBody IngredienteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredienteService.criar(req));
    }

    @Operation(summary = "Edita ingrediente")
    @PutMapping("/{id}")
    public IngredienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody IngredienteRequest req) {
        return ingredienteService.atualizar(id, req);
    }

    @Operation(summary = "Desativa ingrediente (soft delete - RN-06)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        ingredienteService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
