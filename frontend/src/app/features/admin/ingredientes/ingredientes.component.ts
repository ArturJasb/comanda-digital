import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { IngredienteService } from '../../../core/services/ingrediente.service';
import { NotifyService } from '../../../core/services/notify.service';
import { Ingrediente } from '../../../core/models/models';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

const UNIDADES = ['G', 'ML', 'UN', 'KG', 'L'];

@Component({
  selector: 'app-ingredientes',
  template: `
    <div class="page-header">
      <h1>Ingredientes</h1>
      <button mat-raised-button class="btn-primary" (click)="abrir()"><mat-icon>add</mat-icon> Novo ingrediente</button>
    </div>
    <div class="card">
      <table mat-table [dataSource]="ingredientes" class="full-width">
        <ng-container matColumnDef="nome"><th mat-header-cell *matHeaderCellDef>Nome</th>
          <td mat-cell *matCellDef="let i">{{ i.nome }}</td></ng-container>
        <ng-container matColumnDef="sku"><th mat-header-cell *matHeaderCellDef>SKU</th>
          <td mat-cell *matCellDef="let i" class="text-muted">{{ i.sku }}</td></ng-container>
        <ng-container matColumnDef="unidade"><th mat-header-cell *matHeaderCellDef>Un</th>
          <td mat-cell *matCellDef="let i">{{ i.unidadePadrao }}</td></ng-container>
        <ng-container matColumnDef="custo"><th mat-header-cell *matHeaderCellDef>Custo un.</th>
          <td mat-cell *matCellDef="let i">{{ i.custoUnitario | currencyBr }}</td></ng-container>
        <ng-container matColumnDef="saldo"><th mat-header-cell *matHeaderCellDef>Saldo</th>
          <td mat-cell *matCellDef="let i">
            <span [class.text-danger]="i.abaixoMinimo">{{ i.saldoAtual }} {{ i.unidadePadrao }}</span>
            <mat-icon *ngIf="i.abaixoMinimo" class="warn-ic" matTooltip="Abaixo do minimo">warning</mat-icon>
          </td></ng-container>
        <ng-container matColumnDef="acoes"><th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let i">
            <button mat-icon-button matTooltip="Editar" (click)="abrir(i)"><mat-icon>edit</mat-icon></button>
            <button mat-icon-button color="warn" matTooltip="Desativar" *ngIf="i.status === 'ATIVO'" (click)="desativar(i)"><mat-icon>block</mat-icon></button>
            <button mat-icon-button color="primary" matTooltip="Reativar" *ngIf="i.status !== 'ATIVO'" (click)="reativar(i)"><mat-icon>check_circle</mat-icon></button>
          </td></ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
      <div class="empty-state" *ngIf="ingredientes.length === 0"><mat-icon>eco</mat-icon><p>Nenhum ingrediente.</p></div>
    </div>
  `,
  styles: ['.warn-ic{color:var(--color-warning);font-size:18px;height:18px;width:18px;vertical-align:middle;margin-left:4px}']
})
export class IngredientesComponent implements OnInit {
  ingredientes: Ingrediente[] = [];
  cols = ['nome', 'sku', 'unidade', 'custo', 'saldo', 'acoes'];

  constructor(private service: IngredienteService, private dialog: MatDialog, private notify: NotifyService) {}

  ngOnInit(): void { this.carregar(); }
  carregar(): void { this.service.listar().subscribe(i => this.ingredientes = i); }

  abrir(ing?: Ingrediente): void {
    this.dialog.open(IngredienteDialogComponent, { width: '460px', data: ing })
      .afterClosed().subscribe(ok => { if (ok) this.carregar(); });
  }

  desativar(i: Ingrediente): void {
    this.dialog.open(ConfirmDialogComponent, { data: { titulo: 'Desativar ingrediente',
      mensagem: `Desativar "${i.nome}"?`, perigo: true, confirmar: 'Desativar' } })
      .afterClosed().subscribe(ok => {
        if (ok) this.service.desativar(i.id).subscribe(() => { this.notify.success('Ingrediente desativado'); this.carregar(); });
      });
  }

  reativar(i: Ingrediente): void {
    this.service.atualizar(i.id, {
      nome: i.nome, sku: i.sku, unidadePadrao: i.unidadePadrao,
      estoqueMinimo: i.estoqueMinimo, custoUnitario: i.custoUnitario, status: 'ATIVO'
    }).subscribe(() => { this.notify.success('Ingrediente reativado'); this.carregar(); });
  }
}

@Component({
  selector: 'app-ingrediente-dialog',
  template: `
    <h2 mat-dialog-title>{{ data ? 'Editar' : 'Novo' }} ingrediente</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="dialog-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome</mat-label><input matInput formControlName="nome"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>SKU</mat-label><input matInput formControlName="sku"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Unidade padrao</mat-label>
          <mat-select formControlName="unidadePadrao">
            <mat-option *ngFor="let u of unidades" [value]="u">{{ u }}</mat-option>
          </mat-select></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Estoque minimo</mat-label><input matInput type="number" formControlName="estoqueMinimo"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Custo unitario</mat-label><input matInput type="number" step="0.0001" formControlName="custoUnitario"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Status</mat-label>
          <mat-select formControlName="status"><mat-option value="ATIVO">ATIVO</mat-option><mat-option value="INATIVO">INATIVO</mat-option></mat-select>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button class="btn-primary" (click)="salvar()" [disabled]="form.invalid">Salvar</button>
    </mat-dialog-actions>
  `
})
export class IngredienteDialogComponent {
  unidades = UNIDADES;
  form = this.fb.group({
    nome: ['', Validators.required],
    sku: ['', Validators.required],
    unidadePadrao: ['G', Validators.required],
    estoqueMinimo: [0, Validators.required],
    custoUnitario: [0, Validators.required],
    status: ['ATIVO']
  });

  constructor(
    private fb: FormBuilder,
    private service: IngredienteService,
    private notify: NotifyService,
    private ref: MatDialogRef<IngredienteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Ingrediente | undefined
  ) {
    if (data) this.form.patchValue(data);
  }

  salvar(): void {
    const value = this.form.value as Partial<Ingrediente>;
    const obs = this.data ? this.service.atualizar(this.data.id, value) : this.service.criar(value);
    obs.subscribe(() => { this.notify.success('Ingrediente salvo'); this.ref.close(true); });
  }
}
