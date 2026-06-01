-- ============================================================
-- Comanda Digital - V1 - Criacao das tabelas (SRS secao 6)
-- Enums como ENUM nativo do MySQL (valores na ordem dos enums Java)
-- FKs com ON DELETE RESTRICT para proteger o soft delete (RN-06)
-- ============================================================

CREATE TABLE usuario (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(150) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    senha_hash  VARCHAR(255) NOT NULL,
    perfil      ENUM('ADMIN','GERENTE','COZINHEIRO','CLIENTE') NOT NULL,
    telefone    VARCHAR(20),
    endereco    VARCHAR(255),
    status      ENUM('ATIVO','INATIVO') NOT NULL DEFAULT 'ATIVO',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE categoria (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    descricao   VARCHAR(255),
    ordem       INT          NOT NULL DEFAULT 0,
    status      ENUM('ATIVO','INATIVO') NOT NULL DEFAULT 'ATIVO',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE prato (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    categoria_id     BIGINT       NOT NULL,
    nome             VARCHAR(150) NOT NULL,
    descricao        VARCHAR(500),
    foto_url         VARCHAR(500),
    preco_venda      DECIMAL(10,2) NOT NULL,
    tempo_preparo_min INT,
    status           ENUM('ATIVO','INATIVO','PAUSADO') NOT NULL DEFAULT 'INATIVO',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prato_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE ingrediente (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    sku             VARCHAR(60)  NOT NULL UNIQUE,
    unidade_padrao  ENUM('G','ML','UN','KG','L') NOT NULL,
    estoque_minimo  DECIMAL(12,3) NOT NULL DEFAULT 0,
    custo_unitario  DECIMAL(10,4) NOT NULL DEFAULT 0,
    status          ENUM('ATIVO','INATIVO') NOT NULL DEFAULT 'ATIVO',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE ficha_tecnica (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    prato_id     BIGINT NOT NULL UNIQUE,
    rendimento   INT    NOT NULL DEFAULT 1,
    modo_preparo TEXT,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ficha_prato FOREIGN KEY (prato_id) REFERENCES prato(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE ficha_tecnica_item (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    ficha_tecnica_id BIGINT NOT NULL,
    ingrediente_id   BIGINT NOT NULL,
    quantidade       DECIMAL(12,3) NOT NULL,
    unidade          ENUM('G','ML','UN','KG','L') NOT NULL,
    fator_correcao   DECIMAL(6,2)  NOT NULL DEFAULT 1.00,
    CONSTRAINT fk_fti_ficha FOREIGN KEY (ficha_tecnica_id) REFERENCES ficha_tecnica(id) ON DELETE RESTRICT,
    CONSTRAINT fk_fti_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingrediente(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE fornecedor (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    razao_social VARCHAR(200) NOT NULL,
    cnpj         VARCHAR(18)  NOT NULL UNIQUE,
    telefone     VARCHAR(20),
    email        VARCHAR(150),
    status       ENUM('ATIVO','INATIVO') NOT NULL DEFAULT 'ATIVO',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE fornecedor_produto (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    fornecedor_id  BIGINT NOT NULL,
    ingrediente_id BIGINT NOT NULL,
    preco          DECIMAL(10,4) NOT NULL,
    unidade_venda  ENUM('G','ML','UN','KG','L') NOT NULL,
    CONSTRAINT fk_fp_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id) ON DELETE RESTRICT,
    CONSTRAINT fk_fp_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingrediente(id) ON DELETE RESTRICT,
    CONSTRAINT uq_fornecedor_ingrediente UNIQUE (fornecedor_id, ingrediente_id)
) ENGINE=InnoDB;

CREATE TABLE pedido_compra (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    fornecedor_id BIGINT NOT NULL,
    status        ENUM('RASCUNHO','ENVIADO','RECEBIDO','CANCELADO') NOT NULL DEFAULT 'RASCUNHO',
    valor_total   DECIMAL(12,2) NOT NULL DEFAULT 0,
    usuario_id    BIGINT NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pc_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id) ON DELETE RESTRICT,
    CONSTRAINT fk_pc_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE pedido_compra_item (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_compra_id BIGINT NOT NULL,
    ingrediente_id   BIGINT NOT NULL,
    quantidade       DECIMAL(12,3) NOT NULL,
    preco_unitario   DECIMAL(10,4) NOT NULL,
    subtotal         DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_pci_pedido_compra FOREIGN KEY (pedido_compra_id) REFERENCES pedido_compra(id) ON DELETE RESTRICT,
    CONSTRAINT fk_pci_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingrediente(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE pedido (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id          BIGINT NOT NULL,
    status              ENUM('RECEBIDO','CONFIRMADO','EM_PREPARO','PRONTO','SAIU_ENTREGA','FINALIZADO','CANCELADO') NOT NULL DEFAULT 'RECEBIDO',
    valor_total         DECIMAL(12,2) NOT NULL DEFAULT 0,
    endereco_entrega    VARCHAR(255),
    observacoes         VARCHAR(500),
    motivo_cancelamento VARCHAR(255),
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES usuario(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE pedido_item (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id      BIGINT NOT NULL,
    prato_id       BIGINT NOT NULL,
    quantidade     INT    NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    observacoes    VARCHAR(255),
    CONSTRAINT fk_pi_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE RESTRICT,
    CONSTRAINT fk_pi_prato FOREIGN KEY (prato_id) REFERENCES prato(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE estoque_movimentacao (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    ingrediente_id   BIGINT NOT NULL,
    tipo             ENUM('ENTRADA','SAIDA','ESTORNO') NOT NULL,
    quantidade       DECIMAL(12,3) NOT NULL,
    motivo           ENUM('COMPRA','VENDA','DESPERDICIO','VENCIMENTO','AJUSTE','ESTORNO','USO_INTERNO') NOT NULL,
    lote             VARCHAR(60),
    validade         DATE,
    custo_unitario   DECIMAL(10,4),
    pedido_compra_id BIGINT NULL,
    pedido_id        BIGINT NULL,
    usuario_id       BIGINT NOT NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mov_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingrediente(id) ON DELETE RESTRICT,
    CONSTRAINT fk_mov_pedido_compra FOREIGN KEY (pedido_compra_id) REFERENCES pedido_compra(id) ON DELETE RESTRICT,
    CONSTRAINT fk_mov_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE RESTRICT,
    CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
