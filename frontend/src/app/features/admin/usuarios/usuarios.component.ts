import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UsuarioService } from '../../../core/services/usuario.service';
import { NotifyService } from '../../../core/services/notify.service';
import { Usuario } from '../../../core/models/models';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-usuarios',
  template: `
    <div class="page-header">
      <h1>Usuarios internos</h1>
      <button mat-raised-button class="btn-primary" (click)="abrir()"><mat-icon>person_add</mat-icon> Novo usuario</button>
    </div>
    <div class="card">
      <table mat-table [dataSource]="usuarios" class="full-width">
        <ng-container matColumnDef="nome"><th mat-header-cell *matHeaderCellDef>Nome</th>
          <td mat-cell *matCellDef="let u">{{ u.nome }}</td></ng-container>
        <ng-container matColumnDef="email"><th mat-header-cell *matHeaderCellDef>Email</th>
          <td mat-cell *matCellDef="let u" class="text-muted">{{ u.email }}</td></ng-container>
        <ng-container matColumnDef="perfil"><th mat-header-cell *matHeaderCellDef>Perfil</th>
          <td mat-cell *matCellDef="let u"><span class="perfil-tag">{{ u.perfil }}</span></td></ng-container>
        <ng-container matColumnDef="status"><th mat-header-cell *matHeaderCellDef>Status</th>
          <td mat-cell *matCellDef="let u"><span class="status-badge" [ngClass]="'status-' + (u.status === 'ATIVO' ? 'PRONTO' : 'CANCELADO')">{{ u.status }}</span></td></ng-container>
        <ng-container matColumnDef="acoes"><th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let u">
            <button mat-icon-button matTooltip="Editar" (click)="abrir(u)"><mat-icon>edit</mat-icon></button>
            <button mat-icon-button color="warn" matTooltip="Desativar" *ngIf="u.status === 'ATIVO'" (click)="desativar(u)"><mat-icon>block</mat-icon></button>
            <button mat-icon-button color="primary" matTooltip="Reativar" *ngIf="u.status !== 'ATIVO'" (click)="reativar(u)"><mat-icon>check_circle</mat-icon></button>
          </td></ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
      <div class="empty-state" *ngIf="usuarios.length === 0"><mat-icon>group</mat-icon><p>Nenhum usuario.</p></div>
    </div>
  `,
  styles: ['.perfil-tag{background:#EDE9FE;color:#6D28D9;font-weight:600;font-size:12px;padding:3px 10px;border-radius:999px}']
})
export class UsuariosComponent implements OnInit {
  usuarios: Usuario[] = [];
  cols = ['nome', 'email', 'perfil', 'status', 'acoes'];

  constructor(private service: UsuarioService, private dialog: MatDialog, private notify: NotifyService) {}

  ngOnInit(): void { this.carregar(); }
  carregar(): void { this.service.listar().subscribe(p => this.usuarios = p.content); }

  abrir(u?: Usuario): void {
    this.dialog.open(UsuarioDialogComponent, { width: '460px', data: u })
      .afterClosed().subscribe(ok => { if (ok) this.carregar(); });
  }

  desativar(u: Usuario): void {
    this.dialog.open(ConfirmDialogComponent, { data: { titulo: 'Desativar usuario',
      mensagem: `Desativar "${u.nome}"?`, perigo: true, confirmar: 'Desativar' } })
      .afterClosed().subscribe(ok => {
        if (ok) this.service.desativar(u.id).subscribe(() => { this.notify.success('Usuario desativado'); this.carregar(); });
      });
  }

  reativar(u: Usuario): void {
    // sem senha no payload -> backend mantem a senha atual
    this.service.atualizar(u.id, {
      nome: u.nome, email: u.email, perfil: u.perfil, telefone: u.telefone, status: 'ATIVO'
    }).subscribe(() => { this.notify.success('Usuario reativado'); this.carregar(); });
  }
}

@Component({
  selector: 'app-usuario-dialog',
  template: `
    <h2 mat-dialog-title>{{ data ? 'Editar' : 'Novo' }} usuario</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="dialog-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome</mat-label><input matInput formControlName="nome"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Email</mat-label><input matInput type="email" formControlName="email"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Senha {{ data ? '(deixe em branco para manter)' : '' }}</mat-label>
          <input matInput type="password" formControlName="senha"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Perfil</mat-label>
          <mat-select formControlName="perfil">
            <mat-option value="GERENTE">GERENTE</mat-option>
            <mat-option value="COZINHEIRO">COZINHEIRO</mat-option>
            <mat-option value="ADMIN">ADMIN</mat-option>
          </mat-select></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Telefone</mat-label><input matInput formControlName="telefone"></mat-form-field>
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
export class UsuarioDialogComponent {
  form = this.fb.group({
    nome: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    senha: [''],
    perfil: ['GERENTE', Validators.required],
    telefone: [''],
    status: ['ATIVO']
  });

  constructor(
    private fb: FormBuilder,
    private service: UsuarioService,
    private notify: NotifyService,
    private ref: MatDialogRef<UsuarioDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Usuario | undefined
  ) {
    if (data) this.form.patchValue({ ...data, senha: '' });
  }

  salvar(): void {
    const value: any = { ...this.form.value };
    if (!value.senha) delete value.senha;
    const obs = this.data ? this.service.atualizar(this.data.id, value) : this.service.criar(value);
    obs.subscribe(() => { this.notify.success('Usuario salvo'); this.ref.close(true); });
  }
}
