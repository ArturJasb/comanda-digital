import { Component, Inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CompraService } from '../../../core/services/compra.service';
import { FornecedorService } from '../../../core/services/fornecedor.service';
import { IngredienteService } from '../../../core/services/ingrediente.service';
import { NotifyService } from '../../../core/services/notify.service';
import { Compra, Fornecedor, Ingrediente } from '../../../core/models/models';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrl: './compras.component.scss'
})
export class ComprasComponent implements OnInit {
  compras: Compra[] = [];
  cols = ['id', 'fornecedor', 'data', 'itens', 'total', 'status', 'acoes'];

  constructor(private service: CompraService, private dialog: MatDialog, private notify: NotifyService) {}

  ngOnInit(): void { this.carregar(); }
  carregar(): void { this.service.listar().subscribe(c => this.compras = c); }

  abrir(): void {
    this.dialog.open(CompraDialogComponent, { width: '680px', maxWidth: '95vw' })
      .afterClosed().subscribe(ok => { if (ok) this.carregar(); });
  }

  enviar(c: Compra): void {
    this.service.mudarStatus(c.id, 'ENVIADO').subscribe(() => { this.notify.success('Pedido enviado'); this.carregar(); });
  }

  receber(c: Compra): void {
    this.dialog.open(ConfirmDialogComponent, { data: { titulo: 'Receber mercadoria',
      mensagem: `Receber a compra #${c.id}? Isso cria entrada no estoque e atualiza o custo dos ingredientes.`,
      confirmar: 'Receber' } })
      .afterClosed().subscribe(ok => {
        if (ok) this.service.receber(c.id).subscribe(() => { this.notify.success('Mercadoria recebida e estoque atualizado'); this.carregar(); });
      });
  }

  cancelar(c: Compra): void {
    this.service.mudarStatus(c.id, 'CANCELADO').subscribe(() => { this.notify.success('Compra cancelada'); this.carregar(); });
  }
}

@Component({
  selector: 'app-compra-dialog',
  templateUrl: './compra-dialog.component.html',
  styleUrl: './compra-dialog.component.scss'
})
export class CompraDialogComponent implements OnInit {
  fornecedores: Fornecedor[] = [];
  ingredientes: Ingrediente[] = [];
  form = this.fb.group({
    fornecedorId: [null as number | null, Validators.required],
    itens: this.fb.array([])
  });

  constructor(
    private fb: FormBuilder, private service: CompraService,
    private fornecedorService: FornecedorService, private ingredienteService: IngredienteService,
    private notify: NotifyService, private ref: MatDialogRef<CompraDialogComponent>
  ) {}

  get itens(): FormArray { return this.form.get('itens') as FormArray; }

  ngOnInit(): void {
    this.fornecedorService.listar().subscribe(f => this.fornecedores = f.filter(x => x.status === 'ATIVO'));
    this.ingredienteService.listar().subscribe(i => this.ingredientes = i.filter(x => x.status === 'ATIVO'));
    this.addItem();
  }

  addItem(): void {
    this.itens.push(this.fb.group({
      ingredienteId: [null, Validators.required],
      quantidade: [0, [Validators.required, Validators.min(0.001)]],
      precoUnitario: [0, [Validators.required, Validators.min(0.0001)]]
    }));
  }

  removerItem(i: number): void { this.itens.removeAt(i); }

  subtotal(ctrl: any): number {
    const v = ctrl.value;
    return (v.quantidade || 0) * (v.precoUnitario || 0);
  }

  get total(): number {
    return this.itens.controls.reduce((acc, c) => acc + this.subtotal(c), 0);
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.service.criar(this.form.value as any).subscribe(() => {
      this.notify.success('Pedido de compra criado');
      this.ref.close(true);
    });
  }
}
