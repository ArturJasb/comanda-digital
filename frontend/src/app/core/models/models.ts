// Interfaces espelhando os DTOs do backend

export type Perfil = 'ADMIN' | 'GERENTE' | 'COZINHEIRO' | 'CLIENTE';

export type StatusPedido =
  | 'RECEBIDO' | 'CONFIRMADO' | 'EM_PREPARO' | 'PRONTO' | 'SAIU_ENTREGA' | 'FINALIZADO' | 'CANCELADO';

export type StatusCompra = 'RASCUNHO' | 'ENVIADO' | 'RECEBIDO' | 'CANCELADO';

export type Unidade = 'G' | 'ML' | 'UN' | 'KG' | 'L';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface AuthResponse {
  token: string;
  tipo: string;
  id: number;
  nome: string;
  email: string;
  perfil: Perfil;
}

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  perfil: Perfil;
  telefone?: string;
  endereco?: string;
  status: string;
  createdAt?: string;
  senha?: string;
}

export interface Categoria {
  id: number;
  nome: string;
  descricao?: string;
  ordem?: number;
  status?: string;
}

export interface CardapioItem {
  id: number;
  categoriaId: number;
  categoriaNome: string;
  nome: string;
  descricao?: string;
  fotoUrl?: string;
  precoVenda: number;
  tempoPreparoMin?: number;
}

export interface Prato {
  id: number;
  categoriaId: number;
  categoriaNome: string;
  nome: string;
  descricao?: string;
  fotoUrl?: string;
  precoVenda: number;
  tempoPreparoMin?: number;
  status: string;
  custo?: number;
  foodCostPct?: number;
  foodCostCor?: string;
  temFicha?: boolean;
}

export interface FichaItem {
  id?: number;
  ingredienteId: number;
  ingredienteNome?: string;
  quantidade: number;
  unidade: Unidade;
  fatorCorrecao: number;
  custoUnitario?: number;
  custoItem?: number;
}

export interface FichaTecnica {
  id?: number;
  pratoId?: number;
  pratoNome?: string;
  rendimento: number;
  modoPreparo?: string;
  itens: FichaItem[];
  custoTotal?: number;
  precoVenda?: number;
  foodCostPct?: number;
  foodCostCor?: string;
  warning?: string;
}

export interface Custo {
  pratoId: number;
  custoTotal: number;
  precoVenda: number;
  foodCostPct: number;
  foodCostCor: string;
  warning?: string;
}

export interface Ingrediente {
  id: number;
  nome: string;
  sku: string;
  unidadePadrao: Unidade;
  estoqueMinimo: number;
  custoUnitario: number;
  status: string;
  saldoAtual?: number;
  abaixoMinimo?: boolean;
}

export interface FornecedorProduto {
  id?: number;
  ingredienteId: number;
  ingredienteNome?: string;
  preco: number;
  unidadeVenda: Unidade;
}

export interface Fornecedor {
  id: number;
  razaoSocial: string;
  cnpj: string;
  telefone?: string;
  email?: string;
  status?: string;
  produtos: FornecedorProduto[];
}

export interface CotacaoOpcao {
  fornecedorProdutoId: number;
  fornecedorId: number;
  fornecedorNome: string;
  preco: number;
  unidadeVenda: string;
}

export interface Cotacao {
  ingredienteId: number;
  ingredienteNome: string;
  opcoes: CotacaoOpcao[];
}

export interface PedidoItem {
  id?: number;
  pratoId: number;
  pratoNome?: string;
  quantidade: number;
  precoUnitario?: number;
  subtotal?: number;
  observacoes?: string;
}

export interface Pedido {
  id: number;
  clienteId: number;
  clienteNome: string;
  status: StatusPedido;
  valorTotal: number;
  enderecoEntrega?: string;
  observacoes?: string;
  motivoCancelamento?: string;
  createdAt: string;
  updatedAt: string;
  itens: PedidoItem[];
}

export interface Saldo {
  ingredienteId: number;
  nome: string;
  sku: string;
  unidadePadrao: string;
  saldoAtual: number;
  estoqueMinimo: number;
  abaixoMinimo: boolean;
}

export interface Movimentacao {
  id: number;
  ingredienteId: number;
  ingredienteNome: string;
  tipo: string;
  quantidade: number;
  motivo: string;
  lote?: string;
  validade?: string;
  custoUnitario?: number;
  pedidoId?: number;
  pedidoCompraId?: number;
  usuarioNome?: string;
  createdAt: string;
}

export interface CompraItem {
  id?: number;
  ingredienteId: number;
  ingredienteNome?: string;
  quantidade: number;
  precoUnitario: number;
  subtotal?: number;
}

export interface Compra {
  id: number;
  fornecedorId: number;
  fornecedorNome: string;
  status: StatusCompra;
  valorTotal: number;
  usuarioNome?: string;
  createdAt: string;
  itens: CompraItem[];
}

export interface TopPrato {
  pratoId: number;
  nome: string;
  quantidade: number;
  total: number;
}

export interface VendaDia {
  dia: string;
  total: number;
}

export interface DashboardResumo {
  faturamentoDia: number;
  totalPedidosDia: number;
  ticketMedio: number;
  foodCostMedio: number;
  alertasEstoque: Saldo[];
  vendasPorDia: VendaDia[];
}

// ---- Estado local do carrinho (frontend) ----
export interface CartItem {
  prato: CardapioItem;
  quantidade: number;
  observacoes?: string;
}
