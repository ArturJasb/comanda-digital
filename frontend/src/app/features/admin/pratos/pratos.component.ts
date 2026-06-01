import { Component, Inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PratoService } from '../../../core/services/prato.service';
import { CategoriaService } from '../../../core/services/categoria.service';
import { IngredienteService } from '../../../core/services/ingrediente.service';
import { NotifyService } from '../../../core/services/notify.service';
import { Categoria, FichaTecnica, Ingrediente, Prato } from '../../../core/models/models';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-pratos',
  templateUrl: './pratos.component.html',
  styleUrl: './pratos.component.scss'
})
export class PratosComponent implements OnInit {
  pratos: Prato[] = [];
  cols = ['foto', 'nome', 'categoria', 'preco', 'foodcost', 'status', 'acoes'];

  constructor(private service: PratoService, private dialog: MatDialog, private notify: NotifyService) {}

  ngOnInit(): void { this.carregar(); }
  carregar(): void { this.service.listar().subscribe(p => this.pratos = p.content); }

  badgeClass(status: string): string {
    return status === 'ATIVO' ? 'status-PRONTO' : status === 'PAUSADO' ? 'status-EM_PREPARO' : 'status-CANCELADO';
  }

  abrir(prato?: Prato): void {
    this.dialog.open(PratoDialogComponent, { width: '520px', data: prato })
      .afterClosed().subscribe(ok => { if (ok) this.carregar(); });
  }

  editarFicha(prato: Prato): void {
    this.dialog.open(FichaDialogComponent, { width: '760px', maxWidth: '95vw', data: prato })
      .afterClosed().subscribe(ok => { if (ok) this.carregar(); });
  }

  desativar(p: Prato): void {
    this.dialog.open(ConfirmDialogComponent, { data: { titulo: 'Desativar prato',
      mensagem: `Desativar "${p.nome}"?`, perigo: true, confirmar: 'Desativar' } })
      .afterClosed().subscribe(ok => {
        if (ok) this.service.desativar(p.id).subscribe(() => { this.notify.success('Prato desativado'); this.carregar(); });
      });
  }

  reativar(p: Prato): void {
    // RN-01: so ativa com ficha tecnica (botao ja fica desabilitado sem ficha)
    this.service.atualizar(p.id, {
      nome: p.nome, categoriaId: p.categoriaId, descricao: p.descricao, fotoUrl: p.fotoUrl,
      precoVenda: p.precoVenda, tempoPreparoMin: p.tempoPreparoMin, status: 'ATIVO'
    }).subscribe(() => { this.notify.success('Prato reativado'); this.carregar(); });
  }
}

// ----------------- Dialog: dados do prato -----------------
@Component({
  selector: 'app-prato-dialog',
  template: `
    <h2 mat-dialog-title>{{ data ? 'Editar' : 'Novo' }} prato</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="dialog-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome</mat-label><input matInput formControlName="nome"></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Categoria</mat-label>
          <mat-select formControlName="categoriaId">
            <mat-option *ngFor="let c of categorias" [value]="c.id">{{ c.nome }}</mat-option>
          </mat-select></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descricao</mat-label><textarea matInput rows="2" formControlName="descricao"></textarea></mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>URL da foto</mat-label><input matInput formControlName="fotoUrl"></mat-form-field>
        <div class="two-col">
          <mat-form-field appearance="outline">
            <mat-label>Preco de venda</mat-label><input matInput type="number" step="0.01" formControlName="precoVenda"></mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Tempo preparo (min)</mat-label><input matInput type="number" formControlName="tempoPreparoMin"></mat-form-field>
        </div>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Status</mat-label>
          <mat-select formControlName="status">
            <mat-option value="ATIVO">ATIVO</mat-option>
            <mat-option value="PAUSADO">PAUSADO</mat-option>
            <mat-option value="INATIVO">INATIVO</mat-option>
          </mat-select>
          <mat-hint>So ativa com ficha tecnica (RN-01)</mat-hint>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button class="btn-primary" (click)="salvar()" [disabled]="form.invalid">Salvar</button>
    </mat-dialog-actions>
  `,
  styles: ['.two-col{display:grid;grid-template-columns:1fr 1fr;gap:8px}']
})
export class PratoDialogComponent implements OnInit {
  categorias: Categoria[] = [];
  form = this.fb.group({
    nome: ['', Validators.required],
    categoriaId: [null as number | null, Validators.required],
    descricao: [''],
    fotoUrl: [''],
    precoVenda: [0, [Validators.required, Validators.min(0.01)]],
    tempoPreparoMin: [10],
    status: ['INATIVO']
  });

  constructor(
    private fb: FormBuilder,
    private service: PratoService,
    private categoriaService: CategoriaService,
    private notify: NotifyService,
    private ref: MatDialogRef<PratoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Prato | undefined
  ) {}

  ngOnInit(): void {
    this.categoriaService.listar().subscribe(c => this.categorias = c.filter(x => x.status === 'ATIVO'));
    if (this.data) this.form.patchValue(this.data);
  }

  salvar(): void {
    const value = this.form.value as Partial<Prato>;
    const obs = this.data ? this.service.atualizar(this.data.id, value) : this.service.criar(value);
    obs.subscribe(() => { this.notify.success('Prato salvo'); this.ref.close(true); });
  }
}

// ----------------- Dialog: ficha tecnica -----------------
@Component({
  selector: 'app-ficha-dialog',
  templateUrl: './ficha-dialog.component.html',
  styleUrl: './ficha-dialog.component.scss'
})
export class FichaDialogComponent implements OnInit {
  ingredientes: Ingrediente[] = [];
  form = this.fb.group({
    rendimento: [1, [Validators.required, Validators.min(1)]],
    modoPreparo: [''],
    itens: this.fb.array([])
  });

  constructor(
    private fb: FormBuilder,
    private pratoService: PratoService,
    private ingredienteService: IngredienteService,
    private notify: NotifyService,
    private ref: MatDialogRef<FichaDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public prato: Prato
  ) {}

  get itens(): FormArray { return this.form.get('itens') as FormArray; }

  ngOnInit(): void {
    this.ingredienteService.listar().subscribe(list => {
      this.ingredientes = list.filter(i => i.status === 'ATIVO');
    });
    if (this.prato.temFicha) {
      this.pratoService.buscarFicha(this.prato.id).subscribe(f => {
        this.form.patchValue({ rendimento: f.rendimento, modoPreparo: f.modoPreparo });
        f.itens.forEach(it => this.addItem(it.ingredienteId, it.quantidade, it.unidade, it.fatorCorrecao));
      });
    } else {
      this.addItem();
    }
  }

  addItem(ingredienteId: number | null = null, quantidade = 0, unidade = 'G', fatorCorrecao = 1): void {
    this.itens.push(this.fb.group({
      ingredienteId: [ingredienteId, Validators.required],
      quantidade: [quantidade, [Validators.required, Validators.min(0.001)]],
      unidade: [unidade, Validators.required],
      fatorCorrecao: [fatorCorrecao, [Validators.required, Validators.min(1)]]
    }));
  }

  removerItem(i: number): void { this.itens.removeAt(i); }

  custoIngrediente(id: number): number {
    return this.ingredientes.find(x => x.id === id)?.custoUnitario ?? 0;
  }

  custoItem(ctrl: any): number {
    const v = ctrl.value;
    return (v.quantidade || 0) * (v.fatorCorrecao || 1) * this.custoIngrediente(v.ingredienteId);
  }

  get custoTotal(): number {
    const rend = this.form.value.rendimento || 1;
    const soma = this.itens.controls.reduce((acc, c) => acc + this.custoItem(c), 0);
    return soma / rend;
  }

  get foodCostPct(): number {
    if (!this.prato.precoVenda) return 0;
    return (this.custoTotal / this.prato.precoVenda) * 100;
  }

  salvar(): void {
    if (this.form.invalid || this.itens.length === 0) {
      this.notify.error('Adicione ao menos 1 ingrediente com dados validos');
      this.form.markAllAsTouched();
      return;
    }
    const ficha = this.form.value as unknown as FichaTecnica;
    this.pratoService.salvarFicha(this.prato.id, ficha).subscribe(() => {
      this.notify.success('Ficha tecnica salva');
      this.ref.close(true);
    });
  }
}
