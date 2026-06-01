import { Component, Inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FornecedorService } from '../../../core/services/fornecedor.service';
import { IngredienteService } from '../../../core/services/ingrediente.service';
import { NotifyService } from '../../../core/services/notify.service';
import { Fornecedor, Ingrediente } from '../../../core/models/models';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-fornecedores',
  templateUrl: './fornecedores.component.html',
  styleUrl: './fornecedores.component.scss'
})
export class FornecedoresComponent implements OnInit {
  fornecedores: Fornecedor[] = [];
  cols = ['razao', 'cnpj', 'contato', 'produtos', 'status', 'acoes'];

  constructor(private service: FornecedorService, private dialog: MatDialog, private notify: NotifyService) {}

  ngOnInit(): void { this.carregar(); }
  carregar(): void { this.service.listar().subscribe(f => this.fornecedores = f); }

  abrir(f?: Fornecedor): void {
    this.dialog.open(FornecedorDialogComponent, { width: '640px', maxWidth: '95vw', data: f })
      .afterClosed().subscribe(ok => { if (ok) this.carregar(); });
  }

  cotacao(): void {
    this.dialog.open(CotacaoDialogComponent, { width: '560px' });
  }

  desativar(f: Fornecedor): void {
    this.dialog.open(ConfirmDialogComponent, { data: { titulo: 'Desativar fornecedor',
      mensagem: `Desativar "${f.razaoSocial}"?`, perigo: true, confirmar: 'Desativar' } })
      .afterClosed().subscribe(ok => {
        if (ok) this.service.desativar(f.id).subscribe(() => { this.notify.success('Fornecedor desativado'); this.carregar(); });
      });
  }

  reativar(f: Fornecedor): void {
    const payload = {
      razaoSocial: f.razaoSocial,
      cnpj: f.cnpj,
      telefone: f.telefone,
      email: f.email,
      status: 'ATIVO',
      produtos: f.produtos.map(p => ({
        ingredienteId: p.ingredienteId,
        preco: p.preco,
        unidadeVenda: p.unidadeVenda
      }))
    };
    this.service.atualizar(f.id, payload as Partial<Fornecedor>)
      .subscribe(() => { this.notify.success('Fornecedor reativado'); this.carregar(); });
  }
}

// ---------- Dialog: fornecedor + catalogo ----------
@Component({
  selector: 'app-fornecedor-dialog',
  templateUrl: './fornecedor-dialog.component.html',
  styleUrl: './fornecedor-dialog.component.scss'
})
export class FornecedorDialogComponent implements OnInit {
  ingredientes: Ingrediente[] = [];
  form = this.fb.group({
    razaoSocial: ['', Validators.required],
    cnpj: ['', Validators.required],
    telefone: [''],
    email: [''],
    status: ['ATIVO'],
    produtos: this.fb.array([])
  });

  constructor(
    private fb: FormBuilder, private service: FornecedorService,
    private ingredienteService: IngredienteService, private notify: NotifyService,
    private ref: MatDialogRef<FornecedorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Fornecedor | undefined
  ) {}

  get produtos(): FormArray { return this.form.get('produtos') as FormArray; }

  ngOnInit(): void {
    this.ingredienteService.listar().subscribe(i => this.ingredientes = i.filter(x => x.status === 'ATIVO'));
    if (this.data) {
      this.form.patchValue(this.data);
      this.data.produtos.forEach(p => this.addProduto(p.ingredienteId, p.preco, p.unidadeVenda));
    }
  }

  addProduto(ingredienteId: number | null = null, preco = 0, unidadeVenda = 'G'): void {
    this.produtos.push(this.fb.group({
      ingredienteId: [ingredienteId, Validators.required],
      preco: [preco, [Validators.required, Validators.min(0.0001)]],
      unidadeVenda: [unidadeVenda, Validators.required]
    }));
  }

  removerProduto(i: number): void { this.produtos.removeAt(i); }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const value = this.form.value as Partial<Fornecedor>;
    const obs = this.data ? this.service.atualizar(this.data.id, value) : this.service.criar(value);
    obs.subscribe(() => { this.notify.success('Fornecedor salvo'); this.ref.close(true); });
  }
}

// ---------- Dialog: cotacao comparativa ----------
@Component({
  selector: 'app-cotacao-dialog',
  template: `
    <h2 mat-dialog-title>Cotacao comparativa</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Ingrediente</mat-label>
        <mat-select [(value)]="ingredienteId" (selectionChange)="buscar()">
          <mat-option *ngFor="let i of ingredientes" [value]="i.id">{{ i.nome }}</mat-option>
        </mat-select>
      </mat-form-field>

      <table class="cot-table" *ngIf="cotacao && cotacao.opcoes.length">
        <thead><tr><th>Fornecedor</th><th>Preco</th><th>Un</th></tr></thead>
        <tbody>
          <tr *ngFor="let o of cotacao.opcoes; let first = first" [class.best]="first">
            <td>{{ o.fornecedorNome }} <span class="best-tag" *ngIf="first">melhor</span></td>
            <td>{{ o.preco | currencyBr }}</td>
            <td>{{ o.unidadeVenda }}</td>
          </tr>
        </tbody>
      </table>
      <p class="text-muted" *ngIf="cotacao && cotacao.opcoes.length === 0">Nenhum fornecedor vende este ingrediente.</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end"><button mat-button mat-dialog-close>Fechar</button></mat-dialog-actions>
  `,
  styles: [`
    .cot-table{width:100%;border-collapse:collapse;margin-top:8px}
    .cot-table th{text-align:left;font-size:12px;color:var(--color-text-muted);padding:4px 8px}
    .cot-table td{padding:10px 8px;border-bottom:1px solid var(--color-border)}
    .cot-table tr.best td{background:#E3F6EF}
    .best-tag{background:var(--color-success);color:#fff;font-size:11px;padding:1px 7px;border-radius:999px;margin-left:6px}
    .full-width{width:100%}
  `]
})
export class CotacaoDialogComponent implements OnInit {
  ingredientes: Ingrediente[] = [];
  ingredienteId: number | null = null;
  cotacao?: { ingredienteNome: string; opcoes: any[] };

  constructor(private service: FornecedorService, private ingredienteService: IngredienteService) {}

  ngOnInit(): void {
    this.ingredienteService.listar().subscribe(i => this.ingredientes = i);
  }

  buscar(): void {
    if (!this.ingredienteId) return;
    this.service.cotacao(this.ingredienteId).subscribe(c => this.cotacao = c);
  }
}
