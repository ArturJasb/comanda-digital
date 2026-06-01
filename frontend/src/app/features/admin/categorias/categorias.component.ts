import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CategoriaService } from '../../../core/services/categoria.service';
import { NotifyService } from '../../../core/services/notify.service';
import { Categoria } from '../../../core/models/models';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-categorias',
  template: `
    <div class="page-header">
      <h1>Categorias</h1>
      <button mat-raised-button class="btn-primary" (click)="abrir()"><mat-icon>add</mat-icon> Nova categoria</button>
    </div>

    <div class="card">
      <table mat-table [dataSource]="categorias" class="full-width">
        <ng-container matColumnDef="nome"><th mat-header-cell *matHeaderCellDef>Nome</th>
          <td mat-cell *matCellDef="let c">{{ c.nome }}</td></ng-container>
        <ng-container matColumnDef="descricao"><th mat-header-cell *matHeaderCellDef>Descricao</th>
          <td mat-cell *matCellDef="let c" class="text-muted">{{ c.descricao }}</td></ng-container>
        <ng-container matColumnDef="ordem"><th mat-header-cell *matHeaderCellDef>Ordem</th>
          <td mat-cell *matCellDef="let c">{{ c.ordem }}</td></ng-container>
        <ng-container matColumnDef="status"><th mat-header-cell *matHeaderCellDef>Status</th>
          <td mat-cell *matCellDef="let c"><span class="status-badge" [ngClass]="'status-' + (c.status === 'ATIVO' ? 'PRONTO' : 'CANCELADO')">{{ c.status }}</span></td></ng-container>
        <ng-container matColumnDef="acoes"><th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let c">
            <button mat-icon-button matTooltip="Editar" (click)="abrir(c)"><mat-icon>edit</mat-icon></button>
            <button mat-icon-button color="warn" matTooltip="Desativar" *ngIf="c.status === 'ATIVO'" (click)="desativar(c)"><mat-icon>block</mat-icon></button>
            <button mat-icon-button color="primary" matTooltip="Reativar" *ngIf="c.status !== 'ATIVO'" (click)="reativar(c)"><mat-icon>check_circle</mat-icon></button>
          </td></ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
      <div class="empty-state" *ngIf="categorias.length === 0"><mat-icon>category</mat-icon><p>Nenhuma categoria.</p></div>
    </div>
  `
})
export class CategoriasComponent implements OnInit {
  categorias: Categoria[] = [];
  cols = ['nome', 'descricao', 'ordem', 'status', 'acoes'];

  constructor(private service: CategoriaService, private dialog: MatDialog, private notify: NotifyService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void { this.service.listar().subscribe(c => this.categorias = c); }

  abrir(categoria?: Categoria): void {
    this.dialog.open(CategoriaDialogComponent, { width: '440px', data: categoria })
      .afterClosed().subscribe(ok => { if (ok) this.carregar(); });
  }

  desativar(c: Categoria): void {
    this.dialog.open(ConfirmDialogComponent, { data: { titulo: 'Desativar categoria',
      mensagem: `Desativar "${c.nome}"?`, perigo: true, confirmar: 'Desativar' } })
      .afterClosed().subscribe(ok => {
        if (ok) this.service.desativar(c.id).subscribe(() => { this.notify.success('Categoria desativada'); this.carregar(); });
      });
  }

  reativar(c: Categoria): void {
    this.service.atualizar(c.id, { nome: c.nome, descricao: c.descricao, ordem: c.ordem, status: 'ATIVO' })
      .subscribe(() => { this.notify.success('Categoria reativada'); this.carregar(); });
  }
}

@Component({
  selector: 'app-categoria-dialog',
  template: `
    <h2 mat-dialog-title>{{ data ? 'Editar' : 'Nova' }} categoria</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="dialog-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome</mat-label><input matInput formControlName="nome"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descricao</mat-label><input matInput formControlName="descricao"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Ordem</mat-label><input matInput type="number" formControlName="ordem"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Status</mat-label>
          <mat-select formControlName="status">
            <mat-option value="ATIVO">ATIVO</mat-option><mat-option value="INATIVO">INATIVO</mat-option>
          </mat-select></mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button class="btn-primary" (click)="salvar()" [disabled]="form.invalid">Salvar</button>
    </mat-dialog-actions>
  `
})
export class CategoriaDialogComponent {
  form = this.fb.group({
    nome: ['', Validators.required],
    descricao: [''],
    ordem: [0],
    status: ['ATIVO']
  });

  constructor(
    private fb: FormBuilder,
    private service: CategoriaService,
    private notify: NotifyService,
    private ref: MatDialogRef<CategoriaDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Categoria | undefined
  ) {
    if (data) this.form.patchValue(data);
  }

  salvar(): void {
    const value = this.form.value as Partial<Categoria>;
    const obs = this.data ? this.service.atualizar(this.data.id, value) : this.service.criar(value);
    obs.subscribe(() => { this.notify.success('Categoria salva'); this.ref.close(true); });
  }
}
