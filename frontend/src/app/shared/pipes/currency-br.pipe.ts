import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'currencyBr' })
export class CurrencyBrPipe implements PipeTransform {
  private fmt = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

  transform(value: number | null | undefined): string {
    if (value === null || value === undefined || isNaN(value)) return 'R$ 0,00';
    return this.fmt.format(value);
  }
}
