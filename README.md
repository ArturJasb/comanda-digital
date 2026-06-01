# 🍔 Comanda Digital

> Sistema de pedidos para **Dark Kitchen** (cozinha de delivery, sem salão).
> Trabalho Full-Stack (G01371.1) — UNASP SP · 2026/1.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F)
![Angular](https://img.shields.io/badge/Angular-17-DD0031)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1)

O cliente acessa o cardápio, monta o carrinho e acompanha o pedido em tempo real; a cozinha/admin
gerencia pratos com ficha técnica, controla estoque (com baixa automática), fornecedores, compras e
um dashboard com KPIs.

---

## 📑 Sumário
- [Stack](#-stack)
- [Pré-requisitos](#-pré-requisitos)
- [Como rodar (Docker)](#-como-rodar-passo-a-passo-docker--recomendado)
- [Como rodar sem Docker](#-rodar-sem-docker-mysql--mariadb-local)
- [Usuários / login](#-usuários-do-seed)
- [Estrutura](#-estrutura-do-projeto)
- [Arquitetura](#-arquitetura)
- [Banco: migrations e seed](#-banco-de-dados-migrations--seed)
- [Principais endpoints](#-principais-endpoints-da-api)
- [Troubleshooting](#-troubleshooting)
- [Deploy](#-deploy-opcional)

---

## 🧱 Stack
| Camada | Tecnologias |
|---|---|
| **Backend** | Java 17, Spring Boot 3.2, Spring Security + **JWT**, Spring Data JPA/Hibernate, **Flyway**, Bean Validation, SpringDoc (Swagger), Maven |
| **Frontend** | Angular 17 (NgModule), Angular Material, **Chart.js** (ng2-charts), RxJS, Reactive Forms |
| **Banco** | MySQL 8 |

---

## ✅ Pré-requisitos

| Ferramenta | Versão | Como conferir |
|---|---|---|
| Java (JDK) | 17+ | `java -version` |
| Node.js | 20+ | `node -v` |
| Docker | qualquer recente | `docker -v` |
| Git | qualquer | `git --version` |

> Não precisa instalar Maven nem Angular CLI: o projeto usa o **Maven Wrapper** (`./mvnw`) e o
> Angular vem como dependência local (`npm start` já funciona).

---

## 🚀 Como rodar (passo a passo, Docker — recomendado)

### 1. Clonar o repositório
```bash
git clone https://github.com/ArturJasb/comanda-digital.git
cd comanda-digital
```

### 2. Subir o banco (MySQL 8 via Docker)
```bash
docker compose up -d
```
Sobe um MySQL em `localhost:3306` — usuário `root`, senha `root`, database `comanda_digital`.
Confira com `docker compose ps` (deve estar **healthy**).

### 3. Rodar o backend
```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
Na primeira vez o Maven baixa as dependências. Quando terminar você verá
`Started ComandaDigitalApplication`. O **Flyway cria as tabelas e popula o seed automaticamente**.

- API: <http://localhost:8080>
- **Swagger (documentação dos endpoints):** <http://localhost:8080/swagger-ui.html>

### 4. Rodar o frontend (em outro terminal)
```bash
cd frontend
npm install      # só na primeira vez
npm start
```
- App: <http://localhost:4200>

### 5. Testar o fluxo completo
1. Abra <http://localhost:4200> — o **cardápio público** carrega sem login.
2. Adicione itens ao carrinho → **Finalizar Pedido** → faça login/cadastro → **Confirmar**.
3. Acompanhe o status do pedido (atualiza sozinho a cada 10s).
4. Entre como **admin** (`/auth/login`) e veja o pedido em **Pedidos** (lista ou kanban),
   mude o status (em CONFIRMADO o **estoque baixa automaticamente**) e explore o **Dashboard**.

---

## 🐳 Rodar sem Docker (MySQL / MariaDB local)

Se você já tem um MySQL/MariaDB na máquina, **não precisa do Docker**. O profile `local` aponta para
`localhost:3306`. Crie o banco e um usuário dedicado:

```bash
# MySQL:    mysql -u root -p   < docs/db-setup-mariadb.sql
# MariaDB:  sudo mariadb       < docs/db-setup-mariadb.sql
```

E rode o backend sobrescrevendo as credenciais (se não usar `root/root`):
```bash
cd backend
SPRING_DATASOURCE_USERNAME=comanda SPRING_DATASOURCE_PASSWORD=comanda \
  ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

> Se a porta **3306 já estiver ocupada** por um banco local, use este caminho (sem Docker) ou
> mapeie outra porta no `docker-compose.yml`.

---

## 🔑 Usuários do seed
Todos com a senha **`senha123`**:

| Perfil | Email | O que acessa |
|---|---|---|
| **ADMIN** | `admin@email.com` | Tudo (dashboard, usuários, fornecedores, etc.) |
| **GERENTE** | `gerente@email.com` | Operação (pedidos, estoque, compras, dashboard) |
| **COZINHEIRO** | `cozinheiro@email.com` | Apenas pedidos (avançar status) |
| **CLIENTE** | `cliente@email.com` | Cardápio, carrinho, meus pedidos |

---

## 📁 Estrutura do projeto
```
comanda-digital/
├── backend/                # Spring Boot 3
│   ├── src/main/java/br/com/unasp/comandadigital/
│   │   ├── config/         # SecurityConfig, OpenApiConfig
│   │   ├── controller/     # @RestController (só recebe/devolve)
│   │   ├── service/        # regras de negócio (RN-01..RN-10)
│   │   ├── repository/     # Spring Data JPA
│   │   ├── model/          # @Entity + enums
│   │   ├── dto/            # request/ e response/
│   │   ├── security/       # JWT (filtro, service, userdetails)
│   │   ├── exception/      # @RestControllerAdvice
│   │   └── util/           # validador @CNPJ, cálculo de food cost
│   ├── src/main/resources/db/migration/   # Flyway V1..V6
│   └── Dockerfile          # build para o Render
├── frontend/               # Angular 17
│   └── src/app/
│       ├── core/           # services, guards, interceptors, models
│       ├── shared/         # MaterialModule, pipes, componentes comuns
│       ├── layouts/        # cliente-layout, admin-layout
│       └── features/       # auth, cliente, admin
├── docs/                   # DER (der.png/der.dbml), SRS, script de banco
├── docker-compose.yml
└── README.md
```

---

## 🏛 Arquitetura

- **Camadas:** `Controller` (entrada/saída) → `Service` (toda a regra de negócio) → `Repository` (JPA).
- **Segurança:** Spring Security **stateless** + **JWT** (HS256, expira em 8h), senha com **BCrypt**,
  RBAC por `@PreAuthorize` (ADMIN / GERENTE / COZINHEIRO / CLIENTE). Interceptor JWT no Angular.
- **Cálculo de custo (ficha técnica):** `custo = SUM(qtd × fator_correção × custo_unitário) / rendimento`;
  `food_cost = custo / preço × 100` → **verde ≤30%**, **amarelo 31–35%**, **vermelho >35%**.
- **Estoque:** o saldo é **calculado** pela soma das movimentações (ENTRADA/ESTORNO − SAÍDA), nunca
  guardado em coluna. Ao **confirmar** um pedido, a baixa é automática e transacional (`@Transactional`);
  se faltar ingrediente, retorna **422** com a lista do que falta. Cancelamento **estorna** o estoque.
- **Erros:** `@RestControllerAdvice` com formato JSON único (timestamp, status, error, message, detalhes, path).

---

## 🗄 Banco de dados (migrations & seed)

Tudo versionado com **Flyway** em `backend/src/main/resources/db/migration/`:

| Migration | O que faz |
|---|---|
| `V1__create_tables.sql` | 13 tabelas (enums como `ENUM` nativo do MySQL) |
| `V2__seed_initial_data.sql` | admin + perfis, categorias, 10 ingredientes, 5 pratos com ficha, 2 fornecedores, estoque inicial |
| `V3__create_indexes.sql` | índices de performance |
| `V4__more_pratos.sql` | + variações de hambúrguer, batata frita (categoria Porções) e açaí |
| `V5__fix_acai_fotos.sql` / `V6__fix_batata_cheddar_foto.sql` | ajuste das imagens dos pratos |

> `spring.jpa.hibernate.ddl-auto=validate` — o Hibernate **nunca** altera o schema; quem manda é o Flyway.

**DER do banco:** [`docs/der.png`](docs/der.png) (fonte editável em `docs/der.dbml`).

---

## 🔌 Principais endpoints da API
A documentação completa e testável fica no **Swagger** (`/swagger-ui.html`). Resumo:

| Método | Endpoint | Acesso |
|---|---|---|
| POST | `/api/auth/login`, `/api/auth/register` | Público |
| GET | `/api/cardapio`, `/api/cardapio/{id}`, `/api/cardapio/categorias` | Público |
| POST/GET | `/api/pedidos`, `/api/pedidos/meus`, `/api/pedidos/{id}/status` | CLIENTE |
| GET/PATCH | `/api/admin/pedidos`, `/api/admin/pedidos/{id}/status`, `.../cancelar` | ADMIN/GERENTE/COZINHEIRO |
| CRUD | `/api/admin/pratos` (+ `/{id}/ficha`, `/{id}/custo`), `/api/admin/categorias`, `/api/admin/ingredientes` | ADMIN/GERENTE |
| CRUD | `/api/admin/fornecedores`, `/api/admin/cotacao/{ingredienteId}`, `/api/admin/compras` (+ `/{id}/receber`) | ADMIN/GERENTE |
| GET/POST | `/api/admin/estoque/saldo`, `/alertas`, `/movimentacao`, `/movimentacoes` | ADMIN/GERENTE |
| GET | `/api/admin/dashboard/resumo`, `/top-pratos` | ADMIN/GERENTE |
| CRUD | `/api/admin/usuarios` | ADMIN |

---

## 🔧 Troubleshooting

| Problema | Solução |
|---|---|
| Erro de **CORS** no Angular | Confira `app.cors.allowed-origins=http://localhost:4200` em `backend/src/main/resources/application-local.properties` |
| **Flyway** falhou / banco sujo | `docker compose down -v && docker compose up -d` (reseta o banco) |
| Porta **8080** ocupada | Mude `server.port` no `application.properties` ou finalize o processo |
| Porta **3306** ocupada | Já existe um MySQL/MariaDB local? Use a [seção sem Docker](#-rodar-sem-docker-mysql--mariadb-local) |
| `npm install` travado | Apague `frontend/node_modules` e `frontend/package-lock.json` e rode de novo |
| **401** no Angular | Limpe o `localStorage` no DevTools e faça login novamente |
| Não conecta na API | A API roda em `localhost:8080`; confira se o backend subiu sem erros |

---

## ☁️ Deploy (opcional)
- **Frontend (Vercel):** root `frontend/`, build `npm run build`, output `dist/frontend/browser`
  (`vercel.json` já configura o roteamento SPA).
- **Backend (Render):** root `backend/`, runtime **Docker** (`backend/Dockerfile`), profile `prod`
  lendo tudo de variáveis de ambiente (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ORIGINS`).
- **Banco:** MySQL (ex.: Aiven free tier).

---

## 👤 Autores
- **Artur Brito** — Análise e Desenvolvimento de Sistemas, UNASP SP.
- **Luigi Sapucaia** — Análise e Desenvolvimento de Sistemas, UNASP SP.

Trabalho Full-Stack (G01371.1) — Prof. Thiago Silva · 2026/1.

---

> **Colaboradores:** Artur Brito · Luigi Sapucaia
