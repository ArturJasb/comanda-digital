import { Injectable } from '@angular/core';
import {
  HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest
} from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { NotifyService } from '../services/notify.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService, private router: Router, private notify: NotifyService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401 && !req.url.includes('/auth/')) {
          this.auth.logout();
          this.router.navigate(['/auth/login']);
        } else if (err.status !== 401) {
          this.notify.error(this.mensagem(err));
        }
        return throwError(() => err);
      })
    );
  }

  private mensagem(err: HttpErrorResponse): string {
    const body = err.error;
    if (body?.detalhes?.length) return body.detalhes.join(' | ');
    if (body?.message) return body.message;
    if (err.status === 0) return 'Nao foi possivel conectar ao servidor (API offline?)';
    return 'Ocorreu um erro inesperado';
  }
}
