import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { authGuard } from '../../core/guards/auth.guard';
import { ClienteLayoutComponent } from '../../layouts/cliente-layout/cliente-layout.component';
import { CardapioComponent } from './cardapio/cardapio.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { PedidoStatusComponent } from './pedido-status/pedido-status.component';
import { MeusPedidosComponent } from './meus-pedidos/meus-pedidos.component';

const routes: Routes = [
  {
    path: '',
    component: ClienteLayoutComponent,
    children: [
      { path: '', component: CardapioComponent },
      { path: 'checkout', component: CheckoutComponent, canActivate: [authGuard] },
      { path: 'pedido/:id', component: PedidoStatusComponent, canActivate: [authGuard] },
      { path: 'meus-pedidos', component: MeusPedidosComponent, canActivate: [authGuard] }
    ]
  }
];

@NgModule({
  declarations: [
    ClienteLayoutComponent,
    CardapioComponent,
    CheckoutComponent,
    PedidoStatusComponent,
    MeusPedidosComponent
  ],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class ClienteModule {}
