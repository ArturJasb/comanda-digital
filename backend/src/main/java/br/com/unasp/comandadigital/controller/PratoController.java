package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.request.FichaTecnicaRequest;
import br.com.unasp.comandadigital.dto.request.PratoRequest;
import br.com.unasp.comandadigital.dto.response.CustoResponse;
import br.com.unasp.comandadigital.dto.response.FichaTecnicaResponse;
import br.com.unasp.comandadigital.dto.response.PratoResponse;
import br.com.unasp.comandadigital.service.FichaTecnicaService;
import br.com.unasp.comandadigital.service.PratoService;
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

@Tag(name = "Pratos & Ficha tecnica (admin)", description = "CRUD de pratos e ficha (RF-10..14)")
@RestController
@RequestMapping("/api/admin/pratos")
@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
public class PratoController {

    private final PratoService pratoService;
    private final FichaTecnicaService fichaTecnicaService;

    public PratoController(PratoService pratoService, FichaTecnicaService fichaTecnicaService) {
        this.pratoService = pratoService;
        this.fichaTecnicaService = fichaTecnicaService;
    }

    @Operation(summary = "Lista pratos (paginado)")
    @GetMapping
    public Page<PratoResponse> listar(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
                                      Pageable pageable) {
        return pratoService.listar(pageable);
    }

    @Operation(summary = "Detalhe do prato (com custo e food cost)")
    @GetMapping("/{id}")
    public PratoResponse detalhe(@PathVariable Long id) {
        return pratoService.buscarResponse(id);
    }

    @Operation(summary = "Cria prato")
    @PostMapping
    public ResponseEntity<PratoResponse> criar(@Valid @RequestBody PratoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pratoService.criar(req));
    }

    @Operation(summary = "Edita prato (RN-01: so ativa com ficha)")
    @PutMapping("/{id}")
    public PratoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PratoRequest req) {
        return pratoService.atualizar(id, req);
    }

    @Operation(summary = "Desativa prato (soft delete - RN-06)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        pratoService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Custo calculado + food cost do prato (RF-12/13)")
    @GetMapping("/{id}/custo")
    public CustoResponse custo(@PathVariable Long id) {
        return fichaTecnicaService.custoDoPrato(id);
    }

    @Operation(summary = "Consulta a ficha tecnica do prato (RF-11)")
    @GetMapping("/{id}/ficha")
    public FichaTecnicaResponse buscarFicha(@PathVariable Long id) {
        return fichaTecnicaService.buscarPorPrato(id);
    }

    @Operation(summary = "Cria/atualiza a ficha tecnica do prato (RF-11)")
    @PostMapping("/{id}/ficha")
    public FichaTecnicaResponse salvarFicha(@PathVariable Long id, @Valid @RequestBody FichaTecnicaRequest req) {
        return fichaTecnicaService.salvar(id, req);
    }

    @Operation(summary = "Atualiza a ficha tecnica do prato (RF-11)")
    @PutMapping("/{id}/ficha")
    public FichaTecnicaResponse atualizarFicha(@PathVariable Long id, @Valid @RequestBody FichaTecnicaRequest req) {
        return fichaTecnicaService.salvar(id, req);
    }
}
