package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.request.FornecedorRequest;
import br.com.unasp.comandadigital.dto.response.CotacaoResponse;
import br.com.unasp.comandadigital.dto.response.FornecedorResponse;
import br.com.unasp.comandadigital.service.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Fornecedores (admin)", description = "CRUD, catalogo e cotacao (RF-21..23)")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @Operation(summary = "Lista fornecedores (com catalogo)")
    @GetMapping("/fornecedores")
    public List<FornecedorResponse> listar() {
        return fornecedorService.listar();
    }

    @Operation(summary = "Detalhe do fornecedor")
    @GetMapping("/fornecedores/{id}")
    public FornecedorResponse detalhe(@PathVariable Long id) {
        return fornecedorService.buscarResponse(id);
    }

    @Operation(summary = "Cria fornecedor (CNPJ validado - RN-07)")
    @PostMapping("/fornecedores")
    public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody FornecedorRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorService.criar(req));
    }

    @Operation(summary = "Edita fornecedor e catalogo")
    @PutMapping("/fornecedores/{id}")
    public FornecedorResponse atualizar(@PathVariable Long id, @Valid @RequestBody FornecedorRequest req) {
        return fornecedorService.atualizar(id, req);
    }

    @Operation(summary = "Desativa fornecedor (soft delete - RN-06)")
    @DeleteMapping("/fornecedores/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        fornecedorService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cotacao comparativa de um ingrediente (RF-23)")
    @GetMapping("/cotacao/{ingredienteId}")
    public CotacaoResponse cotacao(@PathVariable Long ingredienteId) {
        return fornecedorService.cotacao(ingredienteId);
    }
}
