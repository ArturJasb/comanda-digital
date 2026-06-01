import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Ingrediente } from '../models/models';

@Injectable({ providedIn: 'root' })
export class IngredienteService {
  private readonly base = `${environment.apiUrl}/admin/ingredientes`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Ingrediente[]> {
    return this.http.get<Ingrediente[]>(this.base);
  }

  buscar(id: number): Observable<Ingrediente> {
    return this.http.get<Ingrediente>(`${this.base}/${id}`);
  }

  criar(i: Partial<Ingrediente>): Observable<Ingrediente> {
    return this.http.post<Ingrediente>(this.base, i);
  }

  atualizar(id: number, i: Partial<Ingrediente>): Observable<Ingrediente> {
    return this.http.put<Ingrediente>(`${this.base}/${id}`, i);
  }

  desativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
