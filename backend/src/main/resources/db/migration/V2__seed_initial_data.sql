-- ============================================================
-- Comanda Digital - V2 - Dados iniciais (seed)
-- Senha de todos os usuarios de exemplo: senha123  (BCrypt strength 10)
-- ============================================================

-- ----- Usuarios -----
-- hash BCrypt de "senha123"
INSERT INTO usuario (id, nome, email, senha_hash, perfil, telefone, endereco, status) VALUES
(1, 'Administrador',     'admin@email.com',     '$2b$10$JDsyhUx9FsP.R1REndCCOet.kXUjvFL.724e2umG3U2wxIY6O1Ce.', 'ADMIN',      '11999990001', 'Rua da Cozinha, 100 - Sao Paulo/SP', 'ATIVO'),
(2, 'Gerente Operacoes', 'gerente@email.com',   '$2b$10$JDsyhUx9FsP.R1REndCCOet.kXUjvFL.724e2umG3U2wxIY6O1Ce.', 'GERENTE',    '11999990002', 'Rua da Cozinha, 100 - Sao Paulo/SP', 'ATIVO'),
(3, 'Cozinheiro Chefe',  'cozinheiro@email.com','$2b$10$JDsyhUx9FsP.R1REndCCOet.kXUjvFL.724e2umG3U2wxIY6O1Ce.', 'COZINHEIRO', '11999990003', 'Rua da Cozinha, 100 - Sao Paulo/SP', 'ATIVO'),
(4, 'Cliente Demo',      'cliente@email.com',   '$2b$10$JDsyhUx9FsP.R1REndCCOet.kXUjvFL.724e2umG3U2wxIY6O1Ce.', 'CLIENTE',    '11988887777', 'Av. Paulista, 1000, ap 51 - Sao Paulo/SP', 'ATIVO');

-- ----- Categorias -----
INSERT INTO categoria (id, nome, descricao, ordem, status) VALUES
(1, 'Lanches',    'Hamburgueres artesanais e lanches',  1, 'ATIVO'),
(2, 'Acai',       'Acai e complementos',                2, 'ATIVO'),
(3, 'Bebidas',    'Refrigerantes, sucos e aguas',       3, 'ATIVO'),
(4, 'Sobremesas', 'Doces e sobremesas',                 4, 'ATIVO');

-- ----- Ingredientes -----
INSERT INTO ingrediente (id, nome, sku, unidade_padrao, estoque_minimo, custo_unitario, status) VALUES
(1,  'Blend bovino',        'BLEND-BOV',  'G',  5000.000, 0.0450, 'ATIVO'),
(2,  'Pao brioche',         'PAO-BRIO',   'UN',   50.000, 2.8000, 'ATIVO'),
(3,  'Queijo cheddar',      'QJO-CHED',   'G',  2000.000, 0.0650, 'ATIVO'),
(4,  'Alface',              'ALFACE',     'G',  1000.000, 0.0120, 'ATIVO'),
(5,  'Tomate',              'TOMATE',     'G',  1000.000, 0.0080, 'ATIVO'),
(6,  'Molho especial',      'MOLHO-ESP',  'ML', 1000.000, 0.0320, 'ATIVO'),
(7,  'Polpa de acai',       'ACAI-POL',   'G',  3000.000, 0.0250, 'ATIVO'),
(8,  'Leite condensado',    'LEITE-COND', 'ML', 1000.000, 0.0150, 'ATIVO'),
(9,  'Banana',              'BANANA',     'G',  2000.000, 0.0060, 'ATIVO'),
(10, 'Refrigerante lata',   'REFRI-LATA', 'UN',   48.000, 3.5000, 'ATIVO');

-- ----- Pratos -----
INSERT INTO prato (id, categoria_id, nome, descricao, foto_url, preco_venda, tempo_preparo_min, status) VALUES
(1, 1, 'Hamburguer Artesanal',        'Blend bovino 180g, queijo cheddar, alface, tomate e molho especial no pao brioche.', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600', 39.90, 20, 'ATIVO'),
(2, 1, 'Cheeseburger Duplo',          'Dois blends bovinos, dobro de cheddar e molho especial no pao brioche.',               'https://images.unsplash.com/photo-1550547660-d9450f859349?w=600', 36.90, 22, 'ATIVO'),
(3, 2, 'Acai 500ml',                  'Acai cremoso com leite condensado e banana.',                                          'https://images.unsplash.com/photo-1590080876351-9c3d3a3a3a3a?w=600', 24.90, 8,  'ATIVO'),
(4, 3, 'Refrigerante Lata 350ml',     'Refrigerante gelado lata 350ml.',                                                      'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=600', 8.50,  2,  'ATIVO'),
(5, 4, 'Acai Especial com Banana',    'Acai 700ml com leite condensado e banana caramelizada.',                               'https://images.unsplash.com/photo-1601004890684-d8cbf643f5f2?w=600', 29.90, 10, 'ATIVO');

-- ----- Fichas tecnicas (1 por prato) -----
INSERT INTO ficha_tecnica (id, prato_id, rendimento, modo_preparo) VALUES
(1, 1, 1, 'Grelhar o blend, montar com queijo, salada e molho no pao brioche.'),
(2, 2, 1, 'Grelhar os dois blends, dobrar o cheddar e finalizar com molho.'),
(3, 3, 1, 'Bater a polpa, servir com leite condensado e banana.'),
(4, 4, 1, 'Servir gelado.'),
(5, 5, 1, 'Bater a polpa, montar com leite condensado e banana caramelizada.');

-- ----- Itens das fichas tecnicas -----
INSERT INTO ficha_tecnica_item (ficha_tecnica_id, ingrediente_id, quantidade, unidade, fator_correcao) VALUES
-- Hamburguer Artesanal (ficha 1) - exemplo do SRS
(1, 1, 180.000, 'G',  1.00),
(1, 2,   1.000, 'UN', 1.00),
(1, 3,  40.000, 'G',  1.00),
(1, 4,  30.000, 'G',  1.40),
(1, 5,  50.000, 'G',  1.25),
(1, 6,  25.000, 'ML', 1.00),
-- Cheeseburger Duplo (ficha 2)
(2, 1, 180.000, 'G',  1.00),
(2, 2,   1.000, 'UN', 1.00),
(2, 3,  60.000, 'G',  1.00),
(2, 6,  20.000, 'ML', 1.00),
-- Acai 500ml (ficha 3)
(3, 7, 300.000, 'G',  1.00),
(3, 8,  30.000, 'ML', 1.00),
(3, 9,  60.000, 'G',  1.20),
-- Refrigerante (ficha 4)
(4, 10,  1.000, 'UN', 1.00),
-- Acai Especial (ficha 5)
(5, 7, 400.000, 'G',  1.00),
(5, 8,  40.000, 'ML', 1.00),
(5, 9, 100.000, 'G',  1.20);

-- ----- Fornecedores -----
INSERT INTO fornecedor (id, razao_social, cnpj, telefone, email, status) VALUES
(1, 'Distribuidora Alimentos SP LTDA', '11.222.333/0001-81', '1133334444', 'vendas@distalimentos.com.br', 'ATIVO'),
(2, 'Bebidas e Insumos Brasil ME',     '45.448.325/0001-70', '1144445555', 'comercial@bebidasbrasil.com.br', 'ATIVO');

-- ----- Catalogo dos fornecedores (fornecedor_produto) -----
INSERT INTO fornecedor_produto (fornecedor_id, ingrediente_id, preco, unidade_venda) VALUES
(1, 1, 0.0450, 'G'),
(1, 2, 2.8000, 'UN'),
(1, 3, 0.0650, 'G'),
(1, 4, 0.0120, 'G'),
(1, 5, 0.0080, 'G'),
(1, 6, 0.0320, 'ML'),
(1, 9, 0.0065, 'G'),
(2, 7, 0.0250, 'G'),
(2, 8, 0.0150, 'ML'),
(2, 9, 0.0058, 'G'),
(2, 10, 3.5000, 'UN');

-- ----- Movimentacoes iniciais de ENTRADA (saldo inicial) -----
INSERT INTO estoque_movimentacao (ingrediente_id, tipo, quantidade, motivo, lote, custo_unitario, usuario_id) VALUES
(1, 'ENTRADA', 5000.000, 'COMPRA', 'L-BLEND-01', 0.0450, 1),
(1, 'ENTRADA', 5000.000, 'COMPRA', 'L-BLEND-02', 0.0450, 1),
(1, 'ENTRADA', 5000.000, 'COMPRA', 'L-BLEND-03', 0.0450, 1),
(1, 'ENTRADA', 5000.000, 'COMPRA', 'L-BLEND-04', 0.0450, 1),
(1, 'ENTRADA', 5000.000, 'COMPRA', 'L-BLEND-05', 0.0450, 1),
(2, 'ENTRADA',   40.000, 'COMPRA', 'L-PAO-01',   2.8000, 1),
(2, 'ENTRADA',   40.000, 'COMPRA', 'L-PAO-02',   2.8000, 1),
(2, 'ENTRADA',   40.000, 'COMPRA', 'L-PAO-03',   2.8000, 1),
(2, 'ENTRADA',   40.000, 'COMPRA', 'L-PAO-04',   2.8000, 1),
(2, 'ENTRADA',   40.000, 'COMPRA', 'L-PAO-05',   2.8000, 1),
(3, 'ENTRADA', 2000.000, 'COMPRA', 'L-QJO-01',   0.0650, 1),
(3, 'ENTRADA', 2000.000, 'COMPRA', 'L-QJO-02',   0.0650, 1),
(3, 'ENTRADA', 2000.000, 'COMPRA', 'L-QJO-03',   0.0650, 1),
(3, 'ENTRADA', 2000.000, 'COMPRA', 'L-QJO-04',   0.0650, 1),
(3, 'ENTRADA', 2000.000, 'COMPRA', 'L-QJO-05',   0.0650, 1),
(4, 'ENTRADA', 1000.000, 'COMPRA', 'L-ALF-01',   0.0120, 1),
(4, 'ENTRADA', 1000.000, 'COMPRA', 'L-ALF-02',   0.0120, 1),
(4, 'ENTRADA', 1000.000, 'COMPRA', 'L-ALF-03',   0.0120, 1),
(4, 'ENTRADA', 1000.000, 'COMPRA', 'L-ALF-04',   0.0120, 1),
(4, 'ENTRADA', 1000.000, 'COMPRA', 'L-ALF-05',   0.0120, 1),
(5, 'ENTRADA', 1000.000, 'COMPRA', 'L-TOM-01',   0.0080, 1),
(5, 'ENTRADA', 1000.000, 'COMPRA', 'L-TOM-02',   0.0080, 1),
(5, 'ENTRADA', 1000.000, 'COMPRA', 'L-TOM-03',   0.0080, 1),
(5, 'ENTRADA', 1000.000, 'COMPRA', 'L-TOM-04',   0.0080, 1),
(5, 'ENTRADA', 1000.000, 'COMPRA', 'L-TOM-05',   0.0080, 1),
(6, 'ENTRADA', 1000.000, 'COMPRA', 'L-MOL-01',   0.0320, 1),
(6, 'ENTRADA', 1000.000, 'COMPRA', 'L-MOL-02',   0.0320, 1),
(6, 'ENTRADA', 1000.000, 'COMPRA', 'L-MOL-03',   0.0320, 1),
(6, 'ENTRADA', 1000.000, 'COMPRA', 'L-MOL-04',   0.0320, 1),
(6, 'ENTRADA', 1000.000, 'COMPRA', 'L-MOL-05',   0.0320, 1),
(7, 'ENTRADA', 4000.000, 'COMPRA', 'L-ACAI-01',  0.0250, 1),
(7, 'ENTRADA', 4000.000, 'COMPRA', 'L-ACAI-02',  0.0250, 1),
(7, 'ENTRADA', 4000.000, 'COMPRA', 'L-ACAI-03',  0.0250, 1),
(7, 'ENTRADA', 4000.000, 'COMPRA', 'L-ACAI-04',  0.0250, 1),
(7, 'ENTRADA', 4000.000, 'COMPRA', 'L-ACAI-05',  0.0250, 1),
(8, 'ENTRADA', 1000.000, 'COMPRA', 'L-LC-01',    0.0150, 1),
(8, 'ENTRADA', 1000.000, 'COMPRA', 'L-LC-02',    0.0150, 1),
(8, 'ENTRADA', 1000.000, 'COMPRA', 'L-LC-03',    0.0150, 1),
(8, 'ENTRADA', 1000.000, 'COMPRA', 'L-LC-04',    0.0150, 1),
(8, 'ENTRADA', 1000.000, 'COMPRA', 'L-LC-05',    0.0150, 1),
(9, 'ENTRADA', 2000.000, 'COMPRA', 'L-BAN-01',   0.0060, 1),
(9, 'ENTRADA', 2000.000, 'COMPRA', 'L-BAN-02',   0.0060, 1),
(9, 'ENTRADA', 2000.000, 'COMPRA', 'L-BAN-03',   0.0060, 1),
(9, 'ENTRADA', 2000.000, 'COMPRA', 'L-BAN-04',   0.0060, 1),
(9, 'ENTRADA', 2000.000, 'COMPRA', 'L-BAN-05',   0.0060, 1),
(10, 'ENTRADA',  48.000, 'COMPRA', 'L-REF-01',   3.5000, 1),
(10, 'ENTRADA',  48.000, 'COMPRA', 'L-REF-02',   3.5000, 1),
(10, 'ENTRADA',  48.000, 'COMPRA', 'L-REF-03',   3.5000, 1),
(10, 'ENTRADA',  48.000, 'COMPRA', 'L-REF-04',   3.5000, 1),
(10, 'ENTRADA',  48.000, 'COMPRA', 'L-REF-05',   3.5000, 1);

-- Ajusta o AUTO_INCREMENT das tabelas com ids fixos
ALTER TABLE usuario     AUTO_INCREMENT = 5;
ALTER TABLE categoria   AUTO_INCREMENT = 5;
ALTER TABLE ingrediente AUTO_INCREMENT = 11;
ALTER TABLE prato       AUTO_INCREMENT = 6;
ALTER TABLE ficha_tecnica AUTO_INCREMENT = 6;
ALTER TABLE fornecedor  AUTO_INCREMENT = 3;
