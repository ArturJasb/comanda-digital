import { Pipe, PipeTransform } from '@angular/core';

/** RF-13: retorna a classe css de cor do food cost. verde <=30, amarelo 31-35, vermelho >35. */
@Pipe({ name: 'foodCostColor' })
export class FoodCostColorPipe implements PipeTransform {
  transform(pct: number | null | undefined): string {
    if (pct === null || pct === undefined) return 'verde';
    if (pct <= 30) return 'verde';
    if (pct <= 35) return 'amarelo';
    return 'vermelho';
  }
}
