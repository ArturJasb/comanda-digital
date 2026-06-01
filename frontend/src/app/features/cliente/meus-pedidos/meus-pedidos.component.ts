import { Component, OnInit } from '@angular/core';
import { PedidoService } from '../../../core/services/pedido.service';
import { Pedido } from '../../../core/models/models';

@Component({
  selector: 'app-meus-pedidos',
  templateUrl: './meus-pedidos.component.html',
  styleUrl: './meus-pedidos.component.scss'
})
export class MeusPedidosComponent implements OnInit {
  pedidos: Pedido[] = [];
  carregando = true;

  constructor(private pedidoService: PedidoService) {}

  ngOnInit(): void {
    this.pedidoService.meusPedidos().subscribe({
      next: page => { this.pedidos = page.content; this.carregando = false; },
      error: () => { this.carregando = false; }
    });
  }
}
