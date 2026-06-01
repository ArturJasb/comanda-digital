import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Custo, FichaTecnica, Page, Prato } from '../models/models';

@Injectable({ providedIn: 'root' })
export class PratoService {
  private readonly base = `${environment.apiUrl}/admin/pratos`;

  constructor(private http: HttpClient) {}

  listar(page = 0, size = 50): Observable<Page<Prato>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Prato>>(this.base, { params });
  }

  buscar(id: number): Observable<Prato> {
    return this.http.get<Prato>(`${this.base}/${id}`);
  }

  criar(p: Partial<Prato>): Observable<Prato> {
    return this.http.post<Prato>(this.base, p);
  }

  atualizar(id: number, p: Partial<Prato>): Observable<Prato> {
    return this.http.put<Prato>(`${this.base}/${id}`, p);
  }

  desativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  custo(id: number): Observable<Custo> {
    return this.http.get<Custo>(`${this.base}/${id}/custo`);
  }

  buscarFicha(id: number): Observable<FichaTecnica> {
    return this.http.get<FichaTecnica>(`${this.base}/${id}/ficha`);
  }

  salvarFicha(id: number, ficha: FichaTecnica): Observable<FichaTecnica> {
    return this.http.post<FichaTecnica>(`${this.base}/${id}/ficha`, ficha);
  }
}
