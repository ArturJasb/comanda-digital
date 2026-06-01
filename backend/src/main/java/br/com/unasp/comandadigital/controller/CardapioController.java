package br.com.unasp.comandadigital.controller;

import br.com.unasp.comandadigital.dto.response.CardapioItemResponse;
import br.com.unasp.comandadigital.dto.response.CategoriaResponse;
import br.com.unasp.comandadigital.service.CardapioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cardapio publico", description = "Acesso sem login (RF-01/RF-02)")
@RestController
@RequestMapping("/api/cardapio")
public class CardapioController {

    private final CardapioService cardapioService;

    public CardapioController(CardapioService cardapioService) {
        this.cardapioService = cardapioService;
    }

    @Operation(summary = "Lista pratos ativos (filtro opcional por categoria)")
    @GetMapping
    public List<CardapioItemResponse> listar(@RequestParam(required = false) Long categoriaId) {
        return cardapioService.listar(categoriaId);
    }

    @Operation(summary = "Categorias ativas para o filtro do cardapio")
    @GetMapping("/categorias")
    public List<CategoriaResponse> categorias() {
        return cardapioService.categorias();
    }

    @Operation(summary = "Detalhe de um prato do cardapio")
    @GetMapping("/{id}")
    public CardapioItemResponse detalhe(@PathVariable Long id) {
        return cardapioService.detalhe(id);
    }
}
