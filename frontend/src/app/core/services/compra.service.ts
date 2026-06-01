import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Compra, CompraItem, StatusCompra } from '../models/models';

@Injectable({ providedIn: 'root' })
export class CompraService {
  private readonly base = `${environment.apiUrl}/admin/compras`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Compra[]> {
    return this.http.get<Compra[]>(this.base);
  }

  buscar(id: number): Observable<Compra> {
    return this.http.get<Compra>(`${this.base}/${id}`);
  }

  criar(payload: { fornecedorId: number; itens: CompraItem[] }): Observable<Compra> {
    return this.http.post<Compra>(this.base, payload);
  }

  atualizar(id: number, payload: { fornecedorId: number; itens: CompraItem[] }): Observable<Compra> {
    return this.http.put<Compra>(`${this.base}/${id}`, payload);
  }

  mudarStatus(id: number, status: StatusCompra): Observable<Compra> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<Compra>(`${this.base}/${id}/status`, null, { params });
  }

  receber(id: number): Observable<Compra> {
    return this.http.post<Compra>(`${this.base}/${id}/receber`, null);
  }
}
