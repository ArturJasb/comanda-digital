import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({ providedIn: 'root' })
export class NotifyService {
  constructor(private snack: MatSnackBar) {}

  success(msg: string): void {
    this.snack.open(msg, 'OK', { duration: 3000, panelClass: 'snack-success' });
  }

  error(msg: string): void {
    this.snack.open(msg, 'Fechar', { duration: 5000, panelClass: 'snack-error' });
  }
}
