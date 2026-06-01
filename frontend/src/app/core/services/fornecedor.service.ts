import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cotacao, Fornecedor } from '../models/models';

@Injectable({ providedIn: 'root' })
export class FornecedorService {
  private readonly base = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Fornecedor[]> {
    return this.http.get<Fornecedor[]>(`${this.base}/fornecedores`);
  }

  buscar(id: number): Observable<Fornecedor> {
    return this.http.get<Fornecedor>(`${this.base}/fornecedores/${id}`);
  }

  criar(f: Partial<Fornecedor>): Observable<Fornecedor> {
    return this.http.post<Fornecedor>(`${this.base}/fornecedores`, f);
  }

  atualizar(id: number, f: Partial<Fornecedor>): Observable<Fornecedor> {
    return this.http.put<Fornecedor>(`${this.base}/fornecedores/${id}`, f);
  }

  desativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/fornecedores/${id}`);
  }

  cotacao(ingredienteId: number): Observable<Cotacao> {
    return this.http.get<Cotacao>(`${this.base}/cotacao/${ingredienteId}`);
  }
}
