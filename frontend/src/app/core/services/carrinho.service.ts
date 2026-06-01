import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CardapioItem, CartItem } from '../models/models';

const CART_KEY = 'comanda_cart';

@Injectable({ providedIn: 'root' })
export class CarrinhoService {
  private itemsSubject = new BehaviorSubject<CartItem[]>(this.load());
  items$ = this.itemsSubject.asObservable();

  private abertoSubject = new BehaviorSubject<boolean>(false);
  aberto$ = this.abertoSubject.asObservable();

  get items(): CartItem[] {
    return this.itemsSubject.value;
  }

  abrir(): void { this.abertoSubject.next(true); }
  fechar(): void { this.abertoSubject.next(false); }
  toggle(): void { this.abertoSubject.next(!this.abertoSubject.value); }

  adicionar(prato: CardapioItem, quantidade = 1, observacoes?: string): void {
    const items = [...this.items];
    const existente = items.find(i => i.prato.id === prato.id && i.observacoes === observacoes);
    if (existente) {
      existente.quantidade += quantidade;
    } else {
      items.push({ prato, quantidade, observacoes });
    }
    this.commit(items);
  }

  alterarQtd(index: number, delta: number): void {
    const items = [...this.items];
    items[index].quantidade += delta;
    if (items[index].quantidade <= 0) {
      items.splice(index, 1);
    }
    this.commit(items);
  }

  remover(index: number): void {
    const items = [...this.items];
    items.splice(index, 1);
    this.commit(items);
  }

  limpar(): void {
    this.commit([]);
  }

  get total(): number {
    return this.items.reduce((acc, i) => acc + i.prato.precoVenda * i.quantidade, 0);
  }

  get totalItens(): number {
    return this.items.reduce((acc, i) => acc + i.quantidade, 0);
  }

  private commit(items: CartItem[]): void {
    localStorage.setItem(CART_KEY, JSON.stringify(items));
    this.itemsSubject.next(items);
  }

  private load(): CartItem[] {
    const raw = localStorage.getItem(CART_KEY);
    return raw ? JSON.parse(raw) as CartItem[] : [];
  }
}
