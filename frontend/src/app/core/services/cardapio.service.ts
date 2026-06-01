import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CardapioItem, Categoria } from '../models/models';

@Injectable({ providedIn: 'root' })
export class CardapioService {
  private readonly base = `${environment.apiUrl}/cardapio`;

  constructor(private http: HttpClient) {}

  listar(categoriaId?: number): Observable<CardapioItem[]> {
    let params = new HttpParams();
    if (categoriaId) params = params.set('categoriaId', categoriaId);
    return this.http.get<CardapioItem[]>(this.base, { params });
  }

  categorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${this.base}/categorias`);
  }

  detalhe(id: number): Observable<CardapioItem> {
    return this.http.get<CardapioItem>(`${this.base}/${id}`);
  }
}
