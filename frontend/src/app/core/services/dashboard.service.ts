import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DashboardResumo, TopPrato } from '../models/models';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly base = `${environment.apiUrl}/admin/dashboard`;

  constructor(private http: HttpClient) {}

  resumo(): Observable<DashboardResumo> {
    return this.http.get<DashboardResumo>(`${this.base}/resumo`);
  }

  topPratos(dias = 30): Observable<TopPrato[]> {
    const params = new HttpParams().set('dias', dias);
    return this.http.get<TopPrato[]>(`${this.base}/top-pratos`, { params });
  }
}
