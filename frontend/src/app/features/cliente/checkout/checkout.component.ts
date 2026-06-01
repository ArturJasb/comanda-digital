import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { CarrinhoService } from '../../../core/services/carrinho.service';
import { PedidoService } from '../../../core/services/pedido.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotifyService } from '../../../core/services/notify.service';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss'
})
export class CheckoutComponent implements OnInit {
  form = this.fb.group({
    enderecoEntrega: [''],
    observacoes: ['']
  });
  enviando = false;

  constructor(
    private fb: FormBuilder,
    public carrinho: CarrinhoService,
    private pedidoService: PedidoService,
    private auth: AuthService,
    private notify: NotifyService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.carrinho.items.length === 0) {
      this.router.navigate(['/']);
      return;
    }
    const user = this.auth.currentUser;
    if (user) {
      // endereco vem do cadastro (busca no proprio token nao tem endereco; deixa em branco para preencher)
      this.form.patchValue({ enderecoEntrega: '' });
    }
  }

  confirmar(): void {
    this.enviando = true;
    const itens = this.carrinho.items.map(i => ({
      pratoId: i.prato.id,
      quantidade: i.quantidade,
      observacoes: i.observacoes
    }));
    this.pedidoService.criar({
      enderecoEntrega: this.form.value.enderecoEntrega || undefined,
      observacoes: this.form.value.observacoes || undefined,
      itens
    }).subscribe({
      next: pedido => {
        this.carrinho.limpar();
        this.notify.success('Pedido confirmado!');
        this.router.navigate(['/pedido', pedido.id]);
      },
      error: () => { this.enviando = false; }
    });
  }
}
