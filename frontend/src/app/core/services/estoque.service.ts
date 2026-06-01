import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Movimentacao, Page, Saldo } from '../models/models';

@Injectable({ providedIn: 'root' })
export class EstoqueService {
  private readonly base = `${environment.apiUrl}/admin/estoque`;

  constructor(private http: HttpClient) {}

  saldo(): Observable<Saldo[]> {
    return this.http.get<Saldo[]>(`${this.base}/saldo`);
  }

  alertas(): Observable<Saldo[]> {
    return this.http.get<Saldo[]>(`${this.base}/alertas`);
  }

  movimentacoes(ingredienteId?: number, page = 0, size = 30): Observable<Page<Movimentacao>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (ingredienteId) params = params.set('ingredienteId', ingredienteId);
    return this.http.get<Page<Movimentacao>>(`${this.base}/movimentacoes`, { params });
  }

  saidaManual(payload: { ingredienteId: number; quantidade: number; motivo: string; lote?: string; validade?: string }):
    Observable<Movimentacao> {
    return this.http.post<Movimentacao>(`${this.base}/movimentacao`, payload);
  }
}
