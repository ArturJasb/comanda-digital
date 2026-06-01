import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PedidoService } from '../../../core/services/pedido.service';
import { NotifyService } from '../../../core/services/notify.service';
import { Pedido, StatusPedido } from '../../../core/models/models';

const FLUXO: StatusPedido[] = ['RECEBIDO', 'CONFIRMADO', 'EM_PREPARO', 'PRONTO', 'SAIU_ENTREGA', 'FINALIZADO'];

@Component({
  selector: 'app-pedidos',
  templateUrl: './pedidos.component.html',
  styleUrl: './pedidos.component.scss'
})
export class PedidosComponent implements OnInit {
  pedidos: Pedido[] = [];
  view: 'lista' | 'kanban' = 'lista';
  filtro: StatusPedido | '' = '';
  cols = ['id', 'cliente', 'data', 'itens', 'total', 'status', 'acoes'];
  readonly fluxo = FLUXO;
  readonly colunasKanban: StatusPedido[] = ['RECEBIDO', 'CONFIRMADO', 'EM_PREPARO', 'PRONTO', 'SAIU_ENTREGA'];

  constructor(private service: PedidoService, private dialog: MatDialog, private notify: NotifyService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.service.listarAdmin(this.filtro || undefined).subscribe(p => this.pedidos = p.content);
  }

  porStatus(status: StatusPedido): Pedido[] {
    return this.pedidos.filter(p => p.status === status);
  }

  proximo(status: StatusPedido): StatusPedido | null {
    const i = FLUXO.indexOf(status);
    return i >= 0 && i < FLUXO.length - 1 ? FLUXO[i + 1] : null;
  }

  podeCancelar(p: Pedido): boolean {
    return p.status !== 'CANCELADO' && p.status !== 'FINALIZADO';
  }

  avancar(p: Pedido): void {
    const next = this.proximo(p.status);
    if (!next) return;
    this.service.mudarStatus(p.id, next).subscribe({
      next: () => { this.notify.success(`Pedido #${p.id} → ${next.replace('_', ' ')}`); this.carregar(); }
    });
  }

  detalhe(p: Pedido): void {
    this.service.detalheAdmin(p.id).subscribe(full =>
      this.dialog.open(PedidoDetalheDialogComponent, { width: '560px', data: full }));
  }

  cancelar(p: Pedido): void {
    this.dialog.open(CancelarPedidoDialogComponent, { width: '420px', data: p })
      .afterClosed().subscribe(ok => { if (ok) this.carregar(); });
  }
}

@Component({
  selector: 'app-pedido-detalhe-dialog',
  template: `
    <h2 mat-dialog-title>Pedido #{{ p.id }}
      <span class="status-badge" [ngClass]="'status-' + p.status">{{ p.status.replace('_',' ') }}</span>
    </h2>
    <mat-dialog-content>
      <p><strong>Cliente:</strong> {{ p.clienteNome }}</p>
      <p *ngIf="p.enderecoEntrega"><strong>Entrega:</strong> {{ p.enderecoEntrega }}</p>
      <p *ngIf="p.observacoes"><strong>Obs:</strong> {{ p.observacoes }}</p>
      <p *ngIf="p.motivoCancelamento"><strong>Cancelamento:</strong> {{ p.motivoCancelamento }}</p>
      <p class="text-muted">{{ p.createdAt | date:'dd/MM/yyyy HH:mm' }}</p>
      <table class="det-table">
        <tr *ngFor="let it of p.itens">
          <td>{{ it.quantidade }}x {{ it.pratoNome }}<br><small class="text-muted" *ngIf="it.observacoes">{{ it.observacoes }}</small></td>
          <td class="r">{{ it.subtotal | currencyBr }}</td>
        </tr>
        <tr class="tot"><td><strong>Total</strong></td><td class="r"><strong>{{ p.valorTotal | currencyBr }}</strong></td></tr>
      </table>
    </mat-dialog-content>
    <mat-dialog-actions align="end"><button mat-button mat-dialog-close>Fechar</button></mat-dialog-actions>
  `,
  styles: ['.det-table{width:100%;margin-top:8px}.det-table td{padding:6px 0;border-bottom:1px dashed var(--color-border)}.det-table .r{text-align:right;white-space:nowrap}.det-table .tot td{border:none;padding-top:10px}']
})
export class PedidoDetalheDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public p: Pedido) {}
}

@Component({
  selector: 'app-cancelar-pedido-dialog',
  template: `
    <h2 mat-dialog-title>Cancelar pedido #{{ p.id }}</h2>
    <mat-dialog-content>
      <p class="text-muted">O estoque sera estornado se ja houve baixa.</p>
      <form [formGroup]="form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Motivo do cancelamento</mat-label>
          <textarea matInput rows="3" formControlName="motivo"></textarea>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Voltar</button>
      <button mat-raised-button color="warn" (click)="confirmar()" [disabled]="form.invalid">Cancelar pedido</button>
    </mat-dialog-actions>
  `
})
export class CancelarPedidoDialogComponent {
  form = this.fb.group({ motivo: ['', Validators.required] });
  constructor(
    private fb: FormBuilder, private service: PedidoService, private notify: NotifyService,
    private ref: MatDialogRef<CancelarPedidoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public p: Pedido
  ) {}

  confirmar(): void {
    this.service.cancelar(this.p.id, this.form.value.motivo!).subscribe(() => {
      this.notify.success('Pedido cancelado e estoque estornado');
      this.ref.close(true);
    });
  }
}
