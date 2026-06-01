import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NgChartsModule } from 'ng2-charts';
import { SharedModule } from '../../shared/shared.module';
import { roleGuard } from '../../core/guards/role.guard';
import { AdminLayoutComponent } from '../../layouts/admin-layout/admin-layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { PedidosComponent, PedidoDetalheDialogComponent, CancelarPedidoDialogComponent } from './pedidos/pedidos.component';
import { PratosComponent, PratoDialogComponent, FichaDialogComponent } from './pratos/pratos.component';
import { CategoriasComponent, CategoriaDialogComponent } from './categorias/categorias.component';
import { IngredientesComponent, IngredienteDialogComponent } from './ingredientes/ingredientes.component';
import { FornecedoresComponent, FornecedorDialogComponent, CotacaoDialogComponent } from './fornecedores/fornecedores.component';
import { ComprasComponent, CompraDialogComponent } from './compras/compras.component';
import { EstoqueComponent, SaidaManualDialogComponent } from './estoque/estoque.component';
import { UsuariosComponent, UsuarioDialogComponent } from './usuarios/usuarios.component';

const GER = { roles: ['ADMIN', 'GERENTE'] };

const routes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      { path: '', redirectTo: 'pedidos', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent, canActivate: [roleGuard], data: GER },
      { path: 'pedidos', component: PedidosComponent },
      { path: 'pratos', component: PratosComponent, canActivate: [roleGuard], data: GER },
      { path: 'categorias', component: CategoriasComponent, canActivate: [roleGuard], data: GER },
      { path: 'ingredientes', component: IngredientesComponent, canActivate: [roleGuard], data: GER },
      { path: 'estoque', component: EstoqueComponent, canActivate: [roleGuard], data: GER },
      { path: 'fornecedores', component: FornecedoresComponent, canActivate: [roleGuard], data: GER },
      { path: 'compras', component: ComprasComponent, canActivate: [roleGuard], data: GER },
      { path: 'usuarios', component: UsuariosComponent, canActivate: [roleGuard], data: { roles: ['ADMIN'] } }
    ]
  }
];

@NgModule({
  declarations: [
    AdminLayoutComponent,
    DashboardComponent,
    PedidosComponent, PedidoDetalheDialogComponent, CancelarPedidoDialogComponent,
    PratosComponent, PratoDialogComponent, FichaDialogComponent,
    CategoriasComponent, CategoriaDialogComponent,
    IngredientesComponent, IngredienteDialogComponent,
    FornecedoresComponent, FornecedorDialogComponent, CotacaoDialogComponent,
    ComprasComponent, CompraDialogComponent,
    EstoqueComponent, SaidaManualDialogComponent,
    UsuariosComponent, UsuarioDialogComponent
  ],
  imports: [SharedModule, NgChartsModule, RouterModule.forChild(routes)]
})
export class AdminModule {}
