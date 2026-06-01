import { Component, OnInit } from '@angular/core';
import { CardapioService } from '../../../core/services/cardapio.service';
import { CarrinhoService } from '../../../core/services/carrinho.service';
import { NotifyService } from '../../../core/services/notify.service';
import { CardapioItem, Categoria } from '../../../core/models/models';

@Component({
  selector: 'app-cardapio',
  templateUrl: './cardapio.component.html',
  styleUrl: './cardapio.component.scss'
})
export class CardapioComponent implements OnInit {
  pratos: CardapioItem[] = [];
  categorias: Categoria[] = [];
  categoriaAtiva: number | null = null;
  carregando = true;

  constructor(
    private cardapioService: CardapioService,
    private carrinho: CarrinhoService,
    private notify: NotifyService
  ) {}

  ngOnInit(): void {
    this.cardapioService.categorias().subscribe(c => this.categorias = c);
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;
    this.cardapioService.listar(this.categoriaAtiva ?? undefined).subscribe({
      next: p => { this.pratos = p; this.carregando = false; },
      error: () => { this.carregando = false; }
    });
  }

  filtrar(categoriaId: number | null): void {
    this.categoriaAtiva = categoriaId;
    this.carregar();
  }

  adicionar(prato: CardapioItem): void {
    this.carrinho.adicionar(prato, 1);
    this.notify.success(`${prato.nome} adicionado ao carrinho`);
  }
}
