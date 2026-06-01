import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription, interval, startWith, switchMap } from 'rxjs';
import { PedidoService } from '../../../core/services/pedido.service';
import { Pedido, StatusPedido } from '../../../core/models/models';

@Component({
  selector: 'app-pedido-status',
  templateUrl: './pedido-status.component.html',
  styleUrl: './pedido-status.component.scss'
})
export class PedidoStatusComponent implements OnInit, OnDestroy {
  pedido?: Pedido;
  carregando = true;
  private sub?: Subscription;

  readonly etapas: { status: StatusPedido; label: string; icon: string }[] = [
    { status: 'RECEBIDO', label: 'Recebido', icon: 'receipt' },
    { status: 'CONFIRMADO', label: 'Confirmado', icon: 'check_circle' },
    { status: 'EM_PREPARO', label: 'Em preparo', icon: 'soup_kitchen' },
    { status: 'PRONTO', label: 'Pronto', icon: 'restaurant' },
    { status: 'SAIU_ENTREGA', label: 'Saiu para entrega', icon: 'delivery_dining' }
  ];

  constructor(private route: ActivatedRoute, private pedidoService: PedidoService) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    // RF-07: polling a cada 10s
    this.sub = interval(10000).pipe(
      startWith(0),
      switchMap(() => this.pedidoService.status(id))
    ).subscribe({
      next: p => { this.pedido = p; this.carregando = false; },
      error: () => { this.carregando = false; }
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  indiceAtual(): number {
    if (!this.pedido) return -1;
    if (this.pedido.status === 'FINALIZADO') return this.etapas.length;
    return this.etapas.findIndex(e => e.status === this.pedido!.status);
  }

  get cancelado(): boolean {
    return this.pedido?.status === 'CANCELADO';
  }
}
