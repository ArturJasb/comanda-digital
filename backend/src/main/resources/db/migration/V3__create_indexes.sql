-- ============================================================
-- Comanda Digital - V3 - Indices (performance basica)
-- ============================================================

-- Pedidos: listagem por status e ordenacao por data
CREATE INDEX idx_pedido_status     ON pedido (status);
CREATE INDEX idx_pedido_created_at ON pedido (created_at);
CREATE INDEX idx_pedido_cliente    ON pedido (cliente_id);

-- Itens
CREATE INDEX idx_pedido_item_pedido ON pedido_item (pedido_id);
CREATE INDEX idx_pedido_item_prato  ON pedido_item (prato_id);

-- Pratos / fichas
CREATE INDEX idx_prato_categoria ON prato (categoria_id);
CREATE INDEX idx_prato_status    ON prato (status);
CREATE INDEX idx_fti_ficha       ON ficha_tecnica_item (ficha_tecnica_id);
CREATE INDEX idx_fti_ingrediente ON ficha_tecnica_item (ingrediente_id);

-- Estoque: saldo por ingrediente (soma de movimentacoes)
CREATE INDEX idx_mov_ingrediente ON estoque_movimentacao (ingrediente_id);
CREATE INDEX idx_mov_tipo        ON estoque_movimentacao (tipo);

-- Compras
CREATE INDEX idx_pc_fornecedor   ON pedido_compra (fornecedor_id);
CREATE INDEX idx_pc_status       ON pedido_compra (status);
CREATE INDEX idx_pci_pedido      ON pedido_compra_item (pedido_compra_id);

-- Fornecedor produto
CREATE INDEX idx_fp_ingrediente  ON fornecedor_produto (ingrediente_id);
