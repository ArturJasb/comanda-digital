import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MaterialModule } from './material.module';
import { CurrencyBrPipe } from './pipes/currency-br.pipe';
import { FoodCostColorPipe } from './pipes/food-cost-color.pipe';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';

@NgModule({
  declarations: [CurrencyBrPipe, FoodCostColorPipe, ConfirmDialogComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, MaterialModule],
  exports: [
    CommonModule, FormsModule, ReactiveFormsModule, RouterModule, MaterialModule,
    CurrencyBrPipe, FoodCostColorPipe, ConfirmDialogComponent
  ]
})
export class SharedModule {}
