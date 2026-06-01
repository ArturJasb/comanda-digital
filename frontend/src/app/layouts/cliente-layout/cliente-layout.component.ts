import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CarrinhoService } from '../../core/services/carrinho.service';

@Component({
  selector: 'app-cliente-layout',
  templateUrl: './cliente-layout.component.html',
  styleUrl: './cliente-layout.component.scss'
})
export class ClienteLayoutComponent {
  user$ = this.auth.user$;

  constructor(public auth: AuthService, public carrinho: CarrinhoService, private router: Router) {}

  finalizar(): void {
    this.carrinho.fechar();
    if (this.auth.isLoggedIn()) {
      this.router.navigate(['/checkout']);
    } else {
      this.router.navigate(['/auth/login'], { queryParams: { redirect: '/checkout' } });
    }
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
