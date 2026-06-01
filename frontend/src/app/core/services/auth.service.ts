import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, Perfil } from '../models/models';

const TOKEN_KEY = 'comanda_token';
const USER_KEY = 'comanda_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly base = `${environment.apiUrl}/auth`;
  private userSubject = new BehaviorSubject<AuthResponse | null>(this.loadUser());
  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, senha: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/login`, { email, senha })
      .pipe(tap(res => this.persist(res)));
  }

  register(payload: { nome: string; email: string; senha: string; telefone?: string; endereco?: string }):
    Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/register`, payload)
      .pipe(tap(res => this.persist(res)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.userSubject.next(null);
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  get currentUser(): AuthResponse | null {
    return this.userSubject.value;
  }

  isLoggedIn(): boolean {
    return !!this.token;
  }

  hasRole(...roles: Perfil[]): boolean {
    const u = this.currentUser;
    return !!u && roles.includes(u.perfil);
  }

  isAdminArea(): boolean {
    return this.hasRole('ADMIN', 'GERENTE', 'COZINHEIRO');
  }

  private persist(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, JSON.stringify(res));
    this.userSubject.next(res);
  }

  private loadUser(): AuthResponse | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) as AuthResponse : null;
  }
}
