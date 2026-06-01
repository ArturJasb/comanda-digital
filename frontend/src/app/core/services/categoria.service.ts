import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Categoria } from '../models/models';

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private readonly base = `${environment.apiUrl}/admin/categorias`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.base);
  }

  criar(c: Partial<Categoria>): Observable<Categoria> {
    return this.http.post<Categoria>(this.base, c);
  }

  atualizar(id: number, c: Partial<Categoria>): Observable<Categoria> {
    return this.http.put<Categoria>(`${this.base}/${id}`, c);
  }

  desativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
