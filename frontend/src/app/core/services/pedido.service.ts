import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page, Pedido, PedidoItem, StatusPedido } from '../models/models';

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private readonly base = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  // ----- Cliente -----
  criar(payload: { enderecoEntrega?: string; observacoes?: string; itens: PedidoItem[] }): Observable<Pedido> {
    return this.http.post<Pedido>(`${this.base}/pedidos`, payload);
  }

  meusPedidos(page = 0, size = 20): Observable<Page<Pedido>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Pedido>>(`${this.base}/pedidos/meus`, { params });
  }

  status(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.base}/pedidos/${id}/status`);
  }

  // ----- Admin -----
  listarAdmin(status?: StatusPedido, page = 0, size = 50): Observable<Page<Pedido>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<Page<Pedido>>(`${this.base}/admin/pedidos`, { params });
  }

  detalheAdmin(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.base}/admin/pedidos/${id}`);
  }

  mudarStatus(id: number, status: StatusPedido): Observable<Pedido> {
    return this.http.patch<Pedido>(`${this.base}/admin/pedidos/${id}/status`, { status });
  }

  cancelar(id: number, motivo: string): Observable<Pedido> {
    return this.http.patch<Pedido>(`${this.base}/admin/pedidos/${id}/cancelar`, { motivo });
  }
}
