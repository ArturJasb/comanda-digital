# 🎯 PROMPT MESTRE — Comanda Digital (Full-Stack)

> **Você é Claude Code.** Sua missão é construir o projeto **Comanda Digital** descrito no arquivo `SRS_Comanda_Digital_MySQL_FINAL.pdf` (que está anexado/no repositório). Este prompt é o **manual de execução**. O SRS é a **fonte da verdade dos requisitos**. Sempre consulte os dois.

---

## ⚠️ LEIA ANTES DE COMEÇAR

1. **Este é um trabalho de faculdade**, não um produto comercial. **Não faça overkill.**
2. Implemente **somente o que está no SRS**. Nada de features extras, nada de "boas práticas avançadas" que o SRS não pede.
3. Se está em dúvida entre "simples e funciona" vs "elegante e complexo", **escolha simples**.
4. O critério de sucesso é: **o professor clona, roda em <10min e tudo funciona conforme RF-01 a RF-43**.
5. Quando o SRS já especifica algo (tabela, endpoint, regra), **siga ao pé da letra**. Não invente variações.

### 🚫 NÃO FAZER (anti-overkill)

- ❌ MapStruct → use mapeamento manual em métodos `toDto()` / `fromDto()`
- ❌ NgRx, Redux, Akita → use Angular Services + RxJS BehaviorSubject
- ❌ SSR no Angular → build estático (`ng build`) é suficiente para Vercel
- ❌ Microserviços → monolito Spring Boot único
- ❌ Cache (Redis, Caffeine) → não pedido no SRS
- ❌ Message queues, eventos assíncronos → tudo síncrono com @Transactional
- ❌ Testes "100% coverage" → só os 3-4 testes críticos do Sprint 3
- ❌ Docker do backend → roda direto com `mvn spring-boot:run`
- ❌ Kubernetes, Helm, Terraform → nada disso
- ❌ Monitoramento (Prometheus, Grafana, Sentry) → não pedido
- ❌ i18n / multi-idioma → tudo em português
- ❌ Dark mode → só light theme
- ❌ PWA, Service Workers → SPA simples
- ❌ Custom design system from scratch → use Angular Material com customização leve
- ❌ Histórias de usuário em BDD/Gherkin → comentários simples bastam

### ✅ FAZER

- ✅ Todos os 43 RFs do SRS (seção 3)
- ✅ Todas as 10 RNs do SRS (seção 4) — **no Service, nunca no Controller**
- ✅ Todos os 12 RNFs do SRS (seção 5)
- ✅ As 14 tabelas exatas do SRS (seção 6)
- ✅ Todos os endpoints da seção 7 do SRS
- ✅ Os 3 fluxos da seção 8 do SRS

---

## 1. STACK (não negociável — está no SRS seção 2)

### Backend
- **Java 17** (LTS, mínimo)
- **Spring Boot 3.2.x** (gerado em start.spring.io)
- **Maven** (mais simples que Gradle para faculdade)
- Dependências:
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-security`
  - `spring-boot-starter-validation`
  - `mysql-connector-j`
  - `flyway-core` + `flyway-mysql`
  - `springdoc-openapi-starter-webmvc-ui` (versão 2.3.0+)
  - `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (versão 0.12.x)
  - `lombok` (reduz boilerplate, ok usar)

### Frontend
- **Angular 17+** (`ng new comanda-frontend --routing --style=scss --standalone=false`)
- Use **NgModule clássico** (não standalone) — é o que está nos materiais da disciplina
- **Angular Material** para componentes UI
- **Chart.js + ng2-charts** para o dashboard
- **HttpClient** + interceptor JWT
- **Reactive Forms** (especialmente para ficha técnica com FormArray)

### Banco
- **MySQL 8+**
- Local via **Docker** (mais fácil que instalar)
- Dev/Prod no **Aiven** (Free tier MySQL)

---

## 2. AMBIENTES (3 ambientes, configuração simples)

| Ambiente | Front | Back | DB | Profile Spring | Branch Git |
|---|---|---|---|---|---|
| **local** | `localhost:4200` | `localhost:8080` | MySQL Docker `localhost:3306` | `local` | qualquer feature/* |
| **dev** | Vercel (preview) | Render | Aiven `comanda_dev` | `dev` | `develop` |
| **prod** | Vercel (production) | Render | Aiven `comanda_prod` | `prod` | `main` |

### Arquivos de configuração

**Backend** — `backend/src/main/resources/`:
```
application.properties              # configs comuns (server.port=8080, springdoc, etc)
application-local.properties        # MySQL local, secret JWT fixo dev
application-dev.properties          # variáveis via ENV (Aiven)
application-prod.properties         # variáveis via ENV (Aiven)
```

**Regra**: profile `local` pode ter secrets em texto (é local). Profiles `dev` e `prod` **leem TUDO de variável de ambiente** com `${VAR_NAME}`.

Exemplo `application-prod.properties`:
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=28800000
app.cors.allowed-origins=${CORS_ORIGINS}
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```

**Frontend** — `frontend/src/environments/`:
```typescript
// environment.ts (local)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};

// environment.development.ts
export const environment = {
  production: false,
  apiUrl: 'https://comanda-api-dev.onrender.com/api'
};

// environment.production.ts
export const environment = {
  production: true,
  apiUrl: 'https://comanda-api.onrender.com/api'
};
```

Use `fileReplacements` no `angular.json` para trocar automaticamente.

---

## 3. ESTRUTURA DO REPOSITÓRIO (monorepo)

```
comanda-digital/
├── backend/
│   ├── src/main/java/br/com/unasp/comandadigital/
│   ├── src/main/resources/
│   ├── pom.xml
│   └── Dockerfile                  # só pro Render
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vercel.json                 # config de SPA routing
├── docs/
│   ├── der.dbml                    # fonte dbdiagram.io
│   ├── der.png                     # imagem exportada
│   └── postman_collection.json     # opcional
├── docker-compose.yml              # MySQL local
├── .gitignore
└── README.md
```

---

## 4. BACKEND — Spring Boot

### 4.1 Estrutura de pacotes (obrigatória, está no SRS seção 2.1)

```
br.com.unasp.comandadigital/
├── ComandaDigitalApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── CorsConfig.java             # ou dentro do SecurityConfig
│   └── OpenApiConfig.java
├── controller/                      # @RestController — só recebe/devolve
├── service/                         # @Service — TODA regra de negócio
├── repository/                      # interfaces JpaRepository
├── model/                           # @Entity (14 entidades do SRS seção 6)
├── dto/
│   ├── request/                     # *Request.java com @Valid
│   └── response/                    # *Response.java
├── security/
│   ├── JwtAuthFilter.java
│   ├── JwtService.java
│   ├── UserDetailsServiceImpl.java
│   └── CustomUserDetails.java
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── BusinessException.java
│   ├── InsufficientStockException.java
│   └── GlobalExceptionHandler.java # @RestControllerAdvice
└── util/
    └── CnpjValidator.java          # validador customizado @CNPJ
```

### 4.2 Entidades (14 — SRS seção 6, copie EXATAMENTE)

Para cada entidade:
- `@Entity` + `@Table(name = "snake_case")`
- ID `Long` com `@GeneratedValue(strategy = IDENTITY)`
- Campos com `@Column(name = "snake_case")`
- Status como `enum` com `@Enumerated(EnumType.STRING)`
- Datas como `LocalDateTime`
- Valores monetários como `BigDecimal` (precision 10, scale 2 para preços; scale 4 para custo_unitario)
- Use `@CreationTimestamp` e `@UpdateTimestamp` do Hibernate
- Relacionamentos com `FetchType.LAZY` por padrão

**Enums obrigatórios** (não invente outros):
- `Perfil`: ADMIN, GERENTE, COZINHEIRO, CLIENTE
- `StatusPrato`: ATIVO, INATIVO, PAUSADO
- `StatusGenerico`: ATIVO, INATIVO (para categoria, ingrediente, usuario, fornecedor)
- `UnidadePadrao`: G, ML, UN, KG, L
- `StatusPedido`: RECEBIDO, CONFIRMADO, EM_PREPARO, PRONTO, SAIU_ENTREGA, FINALIZADO, CANCELADO
- `StatusPedidoCompra`: RASCUNHO, ENVIADO, RECEBIDO, CANCELADO
- `TipoMovimentacao`: ENTRADA, SAIDA, ESTORNO
- `MotivoMovimentacao`: COMPRA, VENDA, DESPERDICIO, VENCIMENTO, AJUSTE, ESTORNO, USO_INTERNO

### 4.3 DTOs (padrão fixo)

- **Nunca** retornar `@Entity` de Controller. Sempre `*Response`.
- **Sempre** receber `@Valid *Request` em POST/PUT.
- Use `record` do Java 17 para Response (imutável, conciso):

```java
public record PratoResponse(
    Long id,
    String nome,
    String descricao,
    BigDecimal precoVenda,
    BigDecimal custo,
    BigDecimal foodCostPct,
    String status
) {}
```

- Request como classe com Lombok `@Data`:
```java
@Data
public class PratoRequest {
    @NotBlank private String nome;
    @NotNull @DecimalMin("0.01") private BigDecimal precoVenda;
    @NotNull private Long categoriaId;
    // ...
}
```

- Mapeamento manual no Service (sem MapStruct).

### 4.4 Services — onde mora a regra de negócio

Implemente as 10 RNs do SRS seção 4 nos Services correspondentes:

| RN | Service | Resumo |
|---|---|---|
| RN-01 | `PratoService` | Só ATIVA prato se tiver ficha com 1+ ingrediente |
| RN-02 | `FichaTecnicaService` | food cost >35% → adiciona warning no response (não bloqueia) |
| RN-03 | `PedidoService` | Antes de criar pedido, valida estoque. Se faltar → 422 com lista |
| RN-04 | `PedidoService` | Cancelamento: antes EM_PREPARO qualquer um; depois só ADMIN/GERENTE |
| RN-05 | `CompraService` | Ao receber compra, atualiza custo_unitario do ingrediente |
| RN-06 | TODOS | Soft delete via campo `status = INATIVO`. Nunca DELETE físico |
| RN-07 | `CnpjValidator` | Validador customizado, anotar campo CNPJ com `@CNPJ` |
| RN-08 | DTO | `fator_correcao` com `@DecimalMin("1.0")` |
| RN-09 | `CardapioService` | GET público filtra `status = ATIVO` |
| RN-10 | `UsuarioService` | Email único, retornar 409 Conflict |

**Fórmulas exatas (SRS seção 3.2):**
```
custo_item = qtd × fator_correcao × custo_unitario / rendimento
custo_total_prato = SUM(custo_item de todos ingredientes)
food_cost_pct = (custo_total_prato / preco_venda) × 100
```

Cores food cost (SRS RF-013):
- `<= 30%` → verde
- `31% a 35%` → amarelo
- `> 35%` → vermelho

### 4.5 Baixa automática de estoque (crítico — SRS RF-17, RN-03)

Ao mudar status de pedido para `CONFIRMADO`, o `PedidoService` deve, **em uma única transação** (`@Transactional`):

1. Para cada `pedido_item`:
   - Buscar a `ficha_tecnica` do prato
   - Para cada `ficha_tecnica_item`, calcular `qtd_total = qtd_pedido × qtd_ficha × fator_correcao / rendimento`
   - Criar `estoque_movimentacao` com `tipo=SAIDA`, `motivo=VENDA`, FK para o pedido
2. Validar saldo antes de cada saída — se insuficiente, lançar `InsufficientStockException` (rollback automático)

### 4.6 Saldo de estoque

Cálculo via query no Repository (não armazenar em coluna):
```sql
SELECT 
  COALESCE(SUM(CASE WHEN tipo IN ('ENTRADA','ESTORNO') THEN quantidade ELSE -quantidade END), 0)
FROM estoque_movimentacao
WHERE ingrediente_id = :id
```

### 4.7 Segurança (SRS 3.7 + RNF-01, 02)

`SecurityConfig.java`:
- Stateless (`SessionCreationPolicy.STATELESS`)
- BCrypt strength 10
- Endpoints públicos: `/api/auth/**`, `/api/cardapio/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- Todos os outros: autenticados
- `JwtAuthFilter` antes do `UsernamePasswordAuthenticationFilter`
- CORS lendo origins de propriedade `app.cors.allowed-origins` (split por vírgula)

`JwtService`:
- HS256
- Secret de `app.jwt.secret` (mínimo 64 chars no prod)
- Expiração de `app.jwt.expiration-ms` (28800000 = 8h)
- Payload: `sub` (email), `perfil`, `userId`, `nome`, `iat`, `exp`

RBAC nos Controllers com `@PreAuthorize`:
- `/api/admin/usuarios` → `hasRole('ADMIN')`
- `/api/admin/fornecedores`, `/api/admin/compras` → `hasAnyRole('ADMIN','GERENTE')`
- `/api/admin/pedidos/{id}/status` → `hasAnyRole('ADMIN','GERENTE','COZINHEIRO')`
- `/api/admin/pedidos/{id}/cancelar` → `hasAnyRole('ADMIN','GERENTE')` (RN-04 depois de EM_PREPARO)
- `/api/admin/**` (resto) → `hasAnyRole('ADMIN','GERENTE')`

Habilite `@EnableMethodSecurity(prePostEnabled = true)`.

### 4.8 Tratamento de erros (RNF-06)

`GlobalExceptionHandler` com `@RestControllerAdvice`. Formato único:
```json
{
  "timestamp": "2026-05-27T10:00:00",
  "status": 422,
  "error": "Estoque insuficiente",
  "message": "Ingredientes em falta",
  "detalhes": ["Blend bovino: precisa 180g, disponível 100g"],
  "path": "/api/pedidos"
}
```

Mapear:
- `ResourceNotFoundException` → 404
- `BusinessException` → 400
- `InsufficientStockException` → 422
- `MethodArgumentNotValidException` → 400 com lista de erros de campo
- `AccessDeniedException` → 403
- `AuthenticationException` → 401
- `DataIntegrityViolationException` (email duplicado) → 409
- `Exception` (catch-all) → 500 sem stack trace pro cliente

### 4.9 Flyway (SRS RNF-07)

Em `src/main/resources/db/migration/`:

**V1__create_tables.sql** — DDL das 14 tabelas conforme SRS seção 6. FKs com `ON DELETE RESTRICT` (proteger soft delete).

**V2__seed_initial_data.sql** — Mínimo:
- 1 admin: `admin@email.com` / senha `senha123` (BCrypt hash com strength 10)
- 4 categorias: Lanches, Açaí, Bebidas, Sobremesas
- 10 ingredientes com `estoque_minimo` e `custo_unitario`
- 2 fornecedores com `fornecedor_produto` ligando aos ingredientes
- 5 pratos com `ficha_tecnica` e `ficha_tecnica_item` completos
- 50+ movimentações iniciais de ENTRADA para ter saldo

**V3__create_indexes.sql** — Índices em FKs, `pedido.status`, `pedido.created_at`, `ingrediente.sku`.

⚠️ `spring.jpa.hibernate.ddl-auto=validate` (nunca `create`/`update`).

### 4.10 Endpoints (SRS seção 7)

Implemente os 25+ endpoints **exatamente como listados**. Documente todos com SpringDoc:

```java
@Operation(summary = "Criar pedido a partir do carrinho")
@ApiResponse(responseCode = "201", description = "Pedido criado")
@ApiResponse(responseCode = "422", description = "Estoque insuficiente")
@PostMapping
public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest req) { ... }
```

Paginação em todas as listagens admin (RNF-08):
```java
@GetMapping
public Page<PedidoResponse> listar(
    @RequestParam(required = false) StatusPedido status,
    @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable
) { ... }
```

---

## 5. FRONTEND — Angular

### 5.1 ⚡ INVOQUE A SKILL `frontend-design` ANTES DE GERAR QUALQUER COMPONENTE

Leia `/mnt/skills/public/frontend-design/SKILL.md` (se disponível no ambiente) ou siga estes princípios mínimos extraídos dela:
- Tipografia de no máximo 2 fontes (Inter para tudo é seguro)
- Espaçamento múltiplo de 8px
- Contraste mínimo AA (WCAG)
- Estados visuais: idle, hover, focus, active, disabled, loading, empty, error
- Nunca usar texto cinza claro sobre branco
- Sombras suaves (`0 2px 8px rgba(0,0,0,0.08)`)
- Bordas arredondadas consistentes (8px padrão)

### 5.2 Setup inicial

```bash
ng new frontend --routing --style=scss --skip-tests=false
cd frontend
ng add @angular/material  # escolha: Indigo/Pink ou custom
npm install chart.js ng2-charts
```

### 5.3 Estrutura de pastas

```
src/app/
├── core/
│   ├── guards/
│   │   ├── auth.guard.ts            # checa JWT no localStorage
│   │   └── role.guard.ts            # checa perfil exigido
│   ├── interceptors/
│   │   ├── jwt.interceptor.ts       # adiciona Bearer em toda req
│   │   └── error.interceptor.ts     # trata 401 → logout + redirect
│   ├── services/
│   │   ├── auth.service.ts
│   │   ├── cardapio.service.ts
│   │   ├── pedido.service.ts
│   │   ├── prato.service.ts
│   │   ├── ingrediente.service.ts
│   │   ├── fornecedor.service.ts
│   │   ├── compra.service.ts
│   │   ├── estoque.service.ts
│   │   ├── dashboard.service.ts
│   │   ├── usuario.service.ts
│   │   └── carrinho.service.ts      # state local com BehaviorSubject
│   └── models/                       # interfaces TS espelhando DTOs
├── shared/
│   ├── components/
│   │   ├── header/
│   │   ├── sidebar/
│   │   ├── confirm-dialog/
│   │   ├── empty-state/
│   │   ├── loading-skeleton/
│   │   └── status-badge/             # pedido status com cor
│   ├── pipes/
│   │   ├── currency-br.pipe.ts       # formata R$
│   │   └── food-cost-color.pipe.ts   # retorna classe css
│   └── shared.module.ts
├── features/
│   ├── auth/                         # login, register
│   ├── cliente/
│   │   ├── cardapio/
│   │   ├── carrinho/                 # drawer lateral
│   │   ├── checkout/
│   │   ├── pedido-status/
│   │   └── meus-pedidos/
│   └── admin/
│       ├── dashboard/
│       ├── pedidos/                  # lista + detalhe + kanban
│       ├── pratos/                   # lista + form (ficha técnica)
│       ├── categorias/
│       ├── ingredientes/
│       ├── fornecedores/
│       ├── compras/
│       ├── estoque/
│       └── usuarios/
├── layouts/
│   ├── cliente-layout/               # header simples, sem sidebar
│   └── admin-layout/                 # sidebar + topbar
├── app-routing.module.ts
└── app.module.ts
```

### 5.4 Roteamento (lazy load por feature)

```typescript
const routes: Routes = [
  { path: '', loadChildren: () => import('./features/cliente/cliente.module').then(m => m.ClienteModule) },
  { path: 'auth', loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule) },
  {
    path: 'admin',
    loadChildren: () => import('./features/admin/admin.module').then(m => m.AdminModule),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN', 'GERENTE', 'COZINHEIRO'] }
  },
  { path: '**', redirectTo: '' }
];
```

### 5.5 Design tokens (SCSS global)

`src/styles.scss`:
```scss
:root {
  --color-primary: #FF6B35;
  --color-primary-dark: #E55A2B;
  --color-secondary: #2D3047;
  --color-success: #06A77D;
  --color-warning: #F18F01;
  --color-danger: #D62828;
  --color-bg: #FAFAFA;
  --color-surface: #FFFFFF;
  --color-text: #1A1A1A;
  --color-text-muted: #6B7280;
  --color-border: #E5E7EB;

  --space-1: 8px;
  --space-2: 16px;
  --space-3: 24px;
  --space-4: 32px;
  --space-5: 48px;

  --radius: 8px;
  --radius-lg: 12px;
  --shadow-sm: 0 1px 3px rgba(0,0,0,0.06);
  --shadow: 0 2px 8px rgba(0,0,0,0.08);
  --shadow-lg: 0 8px 24px rgba(0,0,0,0.12);
}

body { font-family: 'Inter', system-ui, sans-serif; background: var(--color-bg); }
```

Importe Inter no `index.html`:
```html
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
```

### 5.6 Componentes-chave (UX importante)

**Cardápio público** (`features/cliente/cardapio`):
- Grid responsivo de cards de prato (1 col mobile, 2 tablet, 3-4 desktop)
- Cada card: foto (cover 200px), nome, descrição truncada, preço, botão "Adicionar"
- Filtro de categoria como chips clicáveis no topo
- Loading skeleton enquanto carrega
- Empty state se categoria vazia

**Carrinho** (drawer lateral, sempre acessível):
- Aberto/fechado via service compartilhado
- Lista de itens com qtd (botões +/-), preço, remover
- Subtotal por item, total geral em destaque
- Botão "Finalizar Pedido" fixo no rodapé
- Empty state com ilustração simples

**Checkout**:
- Card com resumo (itens + total)
- Card com endereço (do cadastro, editável)
- Campo de observações
- Botão "Confirmar Pedido" grande, primário
- Feedback de loading no submit

**Status do pedido**:
- Timeline visual horizontal/vertical (stepper do Material)
- Etapas: Recebido → Confirmado → Em Preparo → Pronto → Saiu Entrega
- Etapa atual destacada, anteriores com check verde
- Polling a cada 10s (`interval(10000).pipe(switchMap(...))`)

**Admin — Pedidos**:
- View "lista" (tabela paginada) E view "kanban" (colunas por status)
- Botão para alternar entre as views
- Clique no card → modal/drawer com detalhe completo

**Admin — Form de Prato**:
- ReactiveForm com `FormGroup` aninhado para ficha técnica
- `FormArray` para itens da ficha (add/remove dinâmico)
- Cada linha do array: select ingrediente, input qtd, select unidade, input fator_correcao
- Coluna calculada de custo do item (display)
- Total de custo e food cost % calculados em tempo real (mas valor final vem do backend)
- Badge colorido de food cost (verde/amarelo/vermelho)

**Admin — Dashboard**:
- 4 cards de KPI no topo (faturamento dia, total pedidos, ticket médio, food cost médio)
- Gráfico de barras: top 5 pratos
- Gráfico de linha: vendas por período
- Lista de alertas de estoque (cards vermelhos pequenos)

### 5.7 Interceptor JWT

```typescript
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  if (token && !req.url.includes('/auth/')) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(req);
};
```

### 5.8 Error interceptor (401 → logout)

```typescript
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  return next(req).pipe(
    catchError(err => {
      if (err.status === 401) {
        localStorage.removeItem('token');
        router.navigate(['/auth/login']);
      }
      return throwError(() => err);
    })
  );
};
```

### 5.9 Feedback visual (RNF-10) — obrigatório

- **Loading**: `MatProgressBar` no topo do layout durante chamadas; skeleton em listas
- **Sucesso**: `MatSnackBar` verde, 3s
- **Erro**: `MatSnackBar` vermelho, 5s, com a mensagem do `detalhes` ou `message` do erro
- **Confirmação**: `MatDialog` antes de deletar/cancelar

### 5.10 Responsivo (RNF-09)

Mínimo: desktop (1280px) + tablet (768px). Use breakpoints do Material CDK ou media queries SCSS.

---

## 6. DEPLOY

### 6.1 Backend no Render

**`backend/Dockerfile`** (multi-stage, simples):
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","app.jar"]
```

No Render:
1. Novo Web Service → conecta repo, root directory `backend/`
2. Runtime: Docker
3. Health check path: `/actuator/health` (se incluir actuator) ou `/swagger-ui.html`
4. Environment variables:
   - `DB_URL=jdbc:mysql://aiven-host:port/comanda_prod?useSSL=true&requireSSL=true`
   - `DB_USER=...`
   - `DB_PASSWORD=...`
   - `JWT_SECRET=...` (gerar com `openssl rand -base64 64`)
   - `CORS_ORIGINS=https://comanda-digital.vercel.app`
   - `SPRING_PROFILES_ACTIVE=prod`

⚠️ **Render Free dorme após 15min.** Documentar isso no README. Primeiro request demora 30-50s.

### 6.2 Frontend no Vercel

**`frontend/vercel.json`**:
```json
{
  "rewrites": [{ "source": "/(.*)", "destination": "/index.html" }]
}
```

No Vercel:
1. Import repo, root directory `frontend/`
2. Framework preset: Angular
3. Build command: `npm run build -- --configuration=production`
4. Output directory: `dist/frontend/browser` (Angular 17+) ou `dist/frontend` (versão anterior)
5. Sem environment variables necessárias (apiUrl está no environment.ts buildado)

Dois ambientes:
- Branch `main` → production deployment
- Branch `develop` → preview deployment automático

### 6.3 Banco no Aiven

1. Criar 2 services MySQL: `comanda-dev` e `comanda-prod` (ou 2 databases num único service para economizar)
2. Pegar connection string (formato `mysql://user:pass@host:port/db`)
3. Converter para JDBC: `jdbc:mysql://host:port/db?useSSL=true&requireSSL=true`
4. Setar no Render como `DB_URL`

### 6.4 Docker Compose local (banco apenas)

**`docker-compose.yml`** na raiz:
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8
    container_name: comanda_mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: comanda_digital
    ports:
      - "3306:3306"
    volumes:
      - comanda_mysql_data:/var/lib/mysql

volumes:
  comanda_mysql_data:
```

---

## 7. README.md (na raiz — direto e funcional)

Estrutura **obrigatória** e nessa ordem:

```markdown
# Comanda Digital
Sistema de pedidos para Dark Kitchen — Trabalho Full-Stack UNASP SP 2026/1

## 🌐 Demo online
- App: https://comanda-digital.vercel.app
- API + Swagger: https://comanda-api.onrender.com/swagger-ui.html
- Login admin: `admin@email.com` / `senha123`

> ⚠️ A API roda em Render Free e dorme após 15min. Primeiro acesso demora 30-50s.

## 🚀 Rodar local (5 passos)

### Pré-requisitos
- Java 17+ — `java -version`
- Node 20+ — `node -v`
- Docker — `docker -v`
- Git

### 1. Clonar
\`\`\`bash
git clone https://github.com/SEU-USER/comanda-digital.git
cd comanda-digital
\`\`\`

### 2. Subir o banco
\`\`\`bash
docker-compose up -d
\`\`\`
MySQL fica em `localhost:3306`, user `root`, senha `root`, db `comanda_digital`.

### 3. Rodar o backend
\`\`\`bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
\`\`\`
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Flyway cria tabelas e seed automaticamente

### 4. Rodar o frontend (em outro terminal)
\`\`\`bash
cd frontend
npm install
npm start
\`\`\`
App: http://localhost:4200

### 5. Testar
- Acesse http://localhost:4200 — cardápio público carrega
- Faça login com `admin@email.com` / `senha123` em `/auth/login`
- Painel admin em `/admin`

## 🔧 Troubleshooting

| Problema | Solução |
|---|---|
| Erro de CORS | Confira `app.cors.allowed-origins=http://localhost:4200` em `application-local.properties` |
| Flyway falhou | `docker-compose down -v && docker-compose up -d` (reseta banco) |
| Porta 8080 ocupada | Mude `server.port` no properties ou mate o processo |
| `npm install` travado | Apague `node_modules` e `package-lock.json`, rode `npm install` |
| 401 no Angular | Limpe `localStorage` no DevTools e faça login de novo |

## 📁 Estrutura
\`\`\`
comanda-digital/
├── backend/      # Spring Boot 3 + MySQL
├── frontend/     # Angular 17
├── docs/         # DER e diagramas
└── docker-compose.yml
\`\`\`

## 🧱 Stack
- Backend: Java 17, Spring Boot 3, Spring Security, JWT, JPA, Flyway, MySQL 8
- Frontend: Angular 17, Angular Material, Chart.js, RxJS
- Deploy: Vercel (front) + Render (back) + Aiven (DB)

## 📚 Documentação
- Endpoints: ver Swagger em `/swagger-ui.html`
- DER: `docs/der.png`
- SRS completo: `docs/SRS_Comanda_Digital.pdf`

## 👤 Autor
Nome — RA xxxxxxx
```

**Regras do README:**
- Nada de "boas vindas", "introdução", "sobre o projeto" extenso
- Comandos direto, em blocos copiáveis
- Tabela de troubleshooting com os erros mais prováveis
- Se o professor lê e não consegue rodar, o README falhou

---

## 8. CHECKLIST DE REVISÃO (antes de cada commit em main/develop)

### Funcional (cole no commit message ou PR)
- [ ] Roda `./mvnw spring-boot:run` sem erro
- [ ] Roda `npm start` sem erro
- [ ] Login admin funciona
- [ ] Cardápio público carrega (sem login)
- [ ] Criar pedido como cliente funciona
- [ ] Mudar status do pedido baixa estoque
- [ ] Swagger acessível
- [ ] Migrations Flyway aplicaram

### Técnico
- [ ] Nenhum Controller tem `if` de regra de negócio
- [ ] Nenhuma `@Entity` retornada em response
- [ ] Senha em response = ❌ NUNCA
- [ ] Paginação nas listagens admin
- [ ] `@PreAuthorize` em todos os endpoints `/admin/**`
- [ ] DTOs com `@Valid`
- [ ] `application-local.properties` não tem credenciais reais
- [ ] `.env*` no `.gitignore`

### UX
- [ ] Loading visível em chamadas async
- [ ] Toast em sucesso/erro
- [ ] Confirm dialog em delete/cancelar
- [ ] Funciona em 768px
- [ ] Food cost com cor correta

---

## 9. ROADMAP DE SPRINTS (alinhado ao SRS seção 9)

### Sprint 1 (26/03) — Front mockado
- [ ] `ng new` + Material + roteamento + interceptor estrutural
- [ ] Layouts cliente e admin
- [ ] Cardápio com dados mockados (array hardcoded)
- [ ] Carrinho funcional (state local)
- [ ] Login/cadastro (form sem chamar API ainda)
- [ ] CRUDs admin de pratos e categorias com dados mockados
- [ ] Responsivo desktop+tablet
- [ ] Deploy Vercel funcionando

### Sprint 2 (30/04) — API real
- [ ] Spring Boot scaffold com profiles
- [ ] Flyway V1 (tabelas) + V2 (seed básico)
- [ ] Security + JWT + login + register
- [ ] CRUD pratos, categorias, ingredientes
- [ ] Ficha técnica com cálculo de custo no Service
- [ ] CRUD fornecedores + catálogo + cotação
- [ ] Swagger documentado
- [ ] Angular consumindo API real (mocks removidos)
- [ ] Interceptor JWT + error handler
- [ ] Deploy Render + Aiven funcionando

### Sprint 3 (24/06) — Sistema completo
- [ ] Fluxo cliente: cardápio → pedido → status (com polling)
- [ ] Baixa automática de estoque ao confirmar pedido (@Transactional)
- [ ] Pedidos no painel admin (lista + kanban + detalhe + status update)
- [ ] Cancelamento com estorno + RN-04
- [ ] Estoque: saldo em tempo real, alertas, saída manual, histórico
- [ ] Compras: pedido → recebimento → entrada → atualiza custo
- [ ] Dashboard com 4 KPIs + 2 gráficos + alertas
- [ ] CRUD usuários internos (admin only)
- [ ] README finalizado e testado em máquina limpa
- [ ] Deploy prod completo

---

## 10. ORDEM DE EXECUÇÃO RECOMENDADA (dia a dia)

Quando começar um sprint, siga essa ordem:

1. **Backend primeiro** dentro do sprint (Sprint 2 e 3): API → testar via Swagger → depois conectar front
2. **Migrations antes de entidades**: escreva o SQL no Flyway, então mapeie as `@Entity` para ele
3. **Service antes de Controller**: implemente a regra e teste com print/debug, depois expõe via endpoint
4. **DTOs antes de Service**: defina o contrato (input/output) primeiro
5. **No front, services antes de components**: tenha o `pedido.service.ts` funcionando antes de fazer a tela

---

## 11. QUANDO PEDIR AJUDA AO HUMANO

Pare e pergunte se:
- Uma decisão de UI não está coberta acima (ex: layout específico)
- Encontrar conflito entre o SRS e este prompt → **SRS prevalece sempre**
- Precisar de credenciais de Aiven/Vercel/Render (humano gera)
- Algo no SRS estiver ambíguo — não invente, pergunte

---

## 12. ENTREGA FINAL — DEFINIÇÃO DE PRONTO

O projeto está pronto quando:
1. ✅ Os 43 RFs estão implementados e testáveis manualmente
2. ✅ As 10 RNs são respeitadas (rode os cenários do SRS seção 8)
3. ✅ Roda em `localhost` seguindo o README, sem ajuda extra
4. ✅ Deploy prod funciona em Vercel + Render + Aiven
5. ✅ Swagger documenta todos os endpoints
6. ✅ Seed cria admin + dados de exemplo
7. ✅ README < 10min para o professor rodar
8. ✅ DER em `docs/der.png`

---

**FIM DO PROMPT MESTRE.** A fonte da verdade dos requisitos é o SRS (`SRS_Comanda_Digital_MySQL_FINAL.pdf`). Este prompt é o **como**; o SRS é o **o quê**. Consulte sempre os dois.


---

# 🎨 DIRETRIZ VISUAL OBRIGATÓRIA — REFERÊNCIA MADERO

## Objetivo

Todo o frontend deve seguir uma identidade visual inspirada no site do restaurante Madero, mantendo aparência moderna, premium, limpa e focada em conversão de pedidos.

## Características Visuais Obrigatórias

### Layout Geral

- Header fixo no topo com logo à esquerda e ações à direita.
- Hero section ampla com imagem de destaque ocupando boa parte da largura da tela.
- Containers centralizados com largura máxima de 1280px.
- Muito espaço em branco entre seções.
- Visual sofisticado e minimalista.
- Navegação simples e intuitiva.
- Cards grandes com imagens de alta qualidade.

### Paleta de Cores

```scss
:root {
  --color-primary: #C8102E;
  --color-primary-dark: #A60D25;
  --color-secondary: #1E1E1E;
  --color-gold: #D4AF37;
  --color-bg: #FFFFFF;
  --color-surface: #F8F8F8;
  --color-text: #1A1A1A;
  --color-text-muted: #6B7280;
  --color-border: #E5E7EB;
}
```

### Tipografia

- Fonte principal: Inter.
- Títulos grandes e impactantes.
- Hierarquia visual semelhante a restaurantes premium.
- Peso 600–700 para títulos.
- Peso 400–500 para textos.

### Página Pública do Cardápio

A página principal deve lembrar a experiência visual do site do Madero:

#### Hero Banner

- Banner grande no topo.
- Imagem gastronômica em alta resolução.
- Título principal chamativo.
- Subtítulo curto.
- Botão CTA "Peça Agora".

#### Categorias

- Categorias exibidas em formato horizontal.
- Chips modernos.
- Scroll horizontal em mobile.

#### Cards de Produtos

Cada card deve conter:

- Foto grande.
- Nome do prato.
- Descrição curta.
- Preço em destaque.
- Botão adicionar ao carrinho.

Efeitos:

- Hover com leve elevação.
- Transição suave.
- Zoom discreto na imagem.

### Carrinho

- Drawer lateral moderno.
- Visual semelhante a plataformas de delivery premium.
- Total sempre visível.
- Botão de checkout fixo.

### Checkout

- Layout dividido em cartões.
- Resumo do pedido em destaque.
- Fluxo simples com poucos cliques.

### Painel Administrativo

Embora o painel seja administrativo, manter consistência visual:

- Cards com bordas suaves.
- Sombras discretas.
- Dashboard moderno.
- Gráficos responsivos.
- Visual corporativo limpo.

### Responsividade

Obrigatório:

- Mobile (360px+)
- Tablet (768px+)
- Desktop (1280px+)

### Experiência do Usuário

Prioridades:

1. Facilidade para fazer pedidos.
2. Velocidade de navegação.
3. Aparência premium.
4. Clareza das informações.
5. Conversão de vendas.

## Regra Final

Sempre que gerar componentes Angular, páginas, SCSS ou layouts:

- Use o design do Madero como principal referência visual.
- Não utilizar aparência genérica de CRUD.
- Priorizar experiência de restaurante premium.
- Manter Angular Material apenas como base técnica.
- Customizar componentes para se aproximar visualmente do padrão Madero.
