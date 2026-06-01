import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page, Usuario } from '../models/models';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly base = `${environment.apiUrl}/admin/usuarios`;

  constructor(private http: HttpClient) {}

  listar(page = 0, size = 50): Observable<Page<Usuario>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Usuario>>(this.base, { params });
  }

  criar(u: Partial<Usuario>): Observable<Usuario> {
    return this.http.post<Usuario>(this.base, u);
  }

  atualizar(id: number, u: Partial<Usuario>): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.base}/${id}`, u);
  }

  desativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
