import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { EstoqueService } from '../../core/services/estoque.service';
import { Perfil } from '../../core/models/models';

interface NavItem { label: string; icon: string; link: string; roles?: Perfil[]; }

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.scss'
})
export class AdminLayoutComponent implements OnInit {
  user$ = this.auth.user$;
  alertasCount = 0;

  readonly nav: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', link: 'dashboard', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Pedidos', icon: 'receipt_long', link: 'pedidos' },
    { label: 'Pratos', icon: 'lunch_dining', link: 'pratos', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Categorias', icon: 'category', link: 'categorias', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Ingredientes', icon: 'eco', link: 'ingredientes', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Estoque', icon: 'inventory_2', link: 'estoque', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Fornecedores', icon: 'local_shipping', link: 'fornecedores', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Compras', icon: 'shopping_bag', link: 'compras', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Usuarios', icon: 'group', link: 'usuarios', roles: ['ADMIN'] }
  ];

  constructor(public auth: AuthService, private estoque: EstoqueService, private router: Router) {}

  ngOnInit(): void {
    if (this.auth.hasRole('ADMIN', 'GERENTE')) {
      this.estoque.alertas().subscribe(a => this.alertasCount = a.length);
    }
  }

  visivel(item: NavItem): boolean {
    return !item.roles || this.auth.hasRole(...item.roles);
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
