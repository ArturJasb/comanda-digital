import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EstoqueService } from '../../../core/services/estoque.service';
import { IngredienteService } from '../../../core/services/ingrediente.service';
import { NotifyService } from '../../../core/services/notify.service';
import { Ingrediente, Movimentacao, Saldo } from '../../../core/models/models';

@Component({
  selector: 'app-estoque',
  templateUrl: './estoque.component.html',
  styleUrl: './estoque.component.scss'
})
export class EstoqueComponent implements OnInit {
  saldos: Saldo[] = [];
  movimentacoes: Movimentacao[] = [];
  colsSaldo = ['nome', 'sku', 'saldo', 'minimo', 'status'];
  colsMov = ['data', 'ingrediente', 'tipo', 'qtd', 'motivo', 'ref', 'usuario'];

  constructor(private service: EstoqueService, private dialog: MatDialog, private notify: NotifyService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.service.saldo().subscribe(s => this.saldos = s);
    this.service.movimentacoes().subscribe(m => this.movimentacoes = m.content);
  }

  saidaManual(): void {
    this.dialog.open(SaidaManualDialogComponent, { width: '440px' })
      .afterClosed().subscribe(ok => { if (ok) this.carregar(); });
  }
}

@Component({
  selector: 'app-saida-manual-dialog',
  template: `
    <h2 mat-dialog-title>Saida manual de estoque</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="dialog-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Ingrediente</mat-label>
          <mat-select formControlName="ingredienteId">
            <mat-option *ngFor="let i of ingredientes" [value]="i.id">{{ i.nome }} ({{ i.saldoAtual }} {{ i.unidadePadrao }})</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Quantidade</mat-label><input matInput type="number" formControlName="quantidade"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Motivo</mat-label>
          <mat-select formControlName="motivo">
            <mat-option value="DESPERDICIO">Desperdicio</mat-option>
            <mat-option value="VENCIMENTO">Vencimento</mat-option>
            <mat-option value="USO_INTERNO">Uso interno</mat-option>
            <mat-option value="AJUSTE">Ajuste</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Lote (opcional)</mat-label><input matInput formControlName="lote"></mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="warn" (click)="salvar()" [disabled]="form.invalid">Registrar saida</button>
    </mat-dialog-actions>
  `
})
export class SaidaManualDialogComponent implements OnInit {
  ingredientes: Ingrediente[] = [];
  form = this.fb.group({
    ingredienteId: [null as number | null, Validators.required],
    quantidade: [0, [Validators.required, Validators.min(0.001)]],
    motivo: ['DESPERDICIO', Validators.required],
    lote: ['']
  });

  constructor(
    private fb: FormBuilder, private service: EstoqueService,
    private ingredienteService: IngredienteService, private notify: NotifyService,
    private ref: MatDialogRef<SaidaManualDialogComponent>
  ) {}

  ngOnInit(): void {
    this.ingredienteService.listar().subscribe(i => this.ingredientes = i.filter(x => x.status === 'ATIVO'));
  }

  salvar(): void {
    this.service.saidaManual(this.form.value as any).subscribe(() => {
      this.notify.success('Saida registrada');
      this.ref.close(true);
    });
  }
}
