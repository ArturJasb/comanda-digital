import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface ConfirmData {
  titulo: string;
  mensagem: string;
  confirmar?: string;
  cancelar?: string;
  perigo?: boolean;
}

@Component({
  selector: 'app-confirm-dialog',
  template: `
    <h2 mat-dialog-title>{{ data.titulo }}</h2>
    <mat-dialog-content>{{ data.mensagem }}</mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>{{ data.cancelar || 'Cancelar' }}</button>
      <button mat-raised-button [color]="data.perigo ? 'warn' : 'primary'" [mat-dialog-close]="true">
        {{ data.confirmar || 'Confirmar' }}
      </button>
    </mat-dialog-actions>
  `
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmData
  ) {}
}
