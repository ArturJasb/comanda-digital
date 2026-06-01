import { NgModule } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatStepperModule } from '@angular/material/stepper';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

const MODULES = [
  MatToolbarModule, MatButtonModule, MatIconModule, MatCardModule, MatFormFieldModule,
  MatInputModule, MatSelectModule, MatTableModule, MatPaginatorModule, MatSortModule,
  MatDialogModule, MatSnackBarModule, MatSidenavModule, MatListModule, MatMenuModule,
  MatChipsModule, MatProgressBarModule, MatProgressSpinnerModule, MatTabsModule,
  MatStepperModule, MatBadgeModule, MatTooltipModule, MatDividerModule, MatExpansionModule,
  MatSlideToggleModule, MatButtonToggleModule, MatDatepickerModule, MatNativeDateModule
];

@NgModule({ exports: MODULES })
export class MaterialModule {}
