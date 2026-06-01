-- ============================================================
-- Comanda Digital - V4 - Mais pratos: variacoes de hamburguer,
-- batata frita (nova categoria Porcoes) e acai.
-- ============================================================

-- ----- Nova categoria: Porcoes (batatas) -----
INSERT INTO categoria (id, nome, descricao, ordem, status) VALUES
(5, 'Porcoes', 'Batatas e acompanhamentos', 5, 'ATIVO');

-- ----- Novos ingredientes (ids 11..17) -----
INSERT INTO ingrediente (id, nome, sku, unidade_padrao, estoque_minimo, custo_unitario, status) VALUES
(11, 'Bacon',            'BACON',      'G',  1000.000, 0.0400, 'ATIVO'),
(12, 'Batata palito',    'BATATA',     'G',  3000.000, 0.0150, 'ATIVO'),
(13, 'Peito de frango',  'FRANGO',     'G',  2000.000, 0.0350, 'ATIVO'),
(14, 'Granola',          'GRANOLA',    'G',  1000.000, 0.0300, 'ATIVO'),
(15, 'Morango',          'MORANGO',    'G',  1000.000, 0.0220, 'ATIVO'),
(16, 'Cheddar cremoso',  'CHED-CREM',  'ML', 1000.000, 0.0280, 'ATIVO'),
(17, 'Cebola',           'CEBOLA',     'G',  1000.000, 0.0090, 'ATIVO');

-- ----- Novos pratos (ids 6..14) -----
INSERT INTO prato (id, categoria_id, nome, descricao, foto_url, preco_venda, tempo_preparo_min, status) VALUES
(6,  1, 'X-Bacon',                 'Blend bovino, cheddar, bacon crocante e molho especial no pao brioche.',        'https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=600', 42.90, 20, 'ATIVO'),
(7,  1, 'X-Frango Crispy',         'Frango empanado crocante, cheddar, alface, tomate e molho.',                    'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=600', 38.90, 22, 'ATIVO'),
(8,  1, 'Smash Duplo Bacon',       'Dois smashes, dobro de cheddar, bacon e cebola caramelizada.',                  'https://images.unsplash.com/photo-1551782450-a2132b4ba21d?w=600', 45.90, 24, 'ATIVO'),
(9,  5, 'Batata Frita Tradicional','Porcao de batatas fritas crocantes.',                                           'https://images.unsplash.com/photo-1576107232684-1279f390859f?w=600', 18.90, 12, 'ATIVO'),
(10, 5, 'Batata Cheddar & Bacon',  'Batata frita coberta com cheddar cremoso e bacon.',                             'https://images.unsplash.com/photo-1585109649139-366815a0d713?w=600', 28.90, 15, 'ATIVO'),
(11, 5, 'Batata Rustica',          'Batata rustica com pele, crocante por fora.',                                   'https://images.unsplash.com/photo-1518013431117-eb1465fa5752?w=600', 22.90, 14, 'ATIVO'),
(12, 2, 'Acai 300ml',              'Acai cremoso na medida certa.',                                                 'https://images.unsplash.com/photo-1590080876351-9c3d3a3a3a3a?w=600', 16.90, 6,  'ATIVO'),
(13, 2, 'Acai com Morango',        'Acai com morango fresco e granola.',                                            'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=600', 27.90, 8,  'ATIVO'),
(14, 2, 'Acai com Granola e Banana','Acai com granola crocante e banana.',                                          'https://images.unsplash.com/photo-1611162616475-46b635cb6868?w=600', 26.90, 8,  'ATIVO');

-- ----- Fichas tecnicas (ids 6..14, 1 por prato) -----
INSERT INTO ficha_tecnica (id, prato_id, rendimento, modo_preparo) VALUES
(6,  6,  1, 'Grelhar o blend, montar com cheddar, bacon e molho no pao brioche.'),
(7,  7,  1, 'Empanar e fritar o frango, montar com cheddar e salada.'),
(8,  8,  1, 'Smash dos dois blends, dobrar cheddar, bacon e cebola caramelizada.'),
(9,  9,  1, 'Fritar as batatas ate dourar e salgar.'),
(10, 10, 1, 'Fritar as batatas, cobrir com cheddar cremoso e bacon.'),
(11, 11, 1, 'Assar/fritar as batatas rusticas com a pele.'),
(12, 12, 1, 'Bater a polpa e servir.'),
(13, 13, 1, 'Bater a polpa, montar com morango e granola.'),
(14, 14, 1, 'Bater a polpa, montar com granola e banana.');

-- ----- Itens das fichas tecnicas -----
INSERT INTO ficha_tecnica_item (ficha_tecnica_id, ingrediente_id, quantidade, unidade, fator_correcao) VALUES
-- X-Bacon (6)
(6, 1, 150.000, 'G',  1.00),
(6, 2,   1.000, 'UN', 1.00),
(6, 3,  40.000, 'G',  1.00),
(6, 11, 40.000, 'G',  1.00),
(6, 6,  25.000, 'ML', 1.00),
-- X-Frango Crispy (7)
(7, 13, 160.000, 'G', 1.00),
(7, 2,   1.000, 'UN', 1.00),
(7, 3,  30.000, 'G',  1.00),
(7, 4,  30.000, 'G',  1.40),
(7, 5,  40.000, 'G',  1.25),
(7, 6,  25.000, 'ML', 1.00),
-- Smash Duplo Bacon (8)
(8, 1, 180.000, 'G',  1.00),
(8, 2,   1.000, 'UN', 1.00),
(8, 3,  60.000, 'G',  1.00),
(8, 11, 50.000, 'G',  1.00),
(8, 17, 30.000, 'G',  1.20),
(8, 6,  20.000, 'ML', 1.00),
-- Batata Frita Tradicional (9)
(9, 12, 200.000, 'G', 1.00),
-- Batata Cheddar & Bacon (10)
(10, 12, 200.000, 'G',  1.00),
(10, 16,  60.000, 'ML', 1.00),
(10, 11,  50.000, 'G',  1.00),
-- Batata Rustica (11)
(11, 12, 250.000, 'G',  1.00),
(11, 6,   30.000, 'ML', 1.00),
-- Acai 300ml (12)
(12, 7, 200.000, 'G',  1.00),
(12, 8,  20.000, 'ML', 1.00),
-- Acai com Morango (13)
(13, 7, 350.000, 'G',  1.00),
(13, 8,  30.000, 'ML', 1.00),
(13, 15, 80.000, 'G',  1.20),
(13, 14, 40.000, 'G',  1.00),
-- Acai com Granola e Banana (14)
(14, 7, 350.000, 'G',  1.00),
(14, 8,  30.000, 'ML', 1.00),
(14, 14, 50.000, 'G',  1.00),
(14, 9,  60.000, 'G',  1.20);

-- ----- Catalogo dos fornecedores para os novos ingredientes -----
INSERT INTO fornecedor_produto (fornecedor_id, ingrediente_id, preco, unidade_venda) VALUES
(1, 11, 0.0400, 'G'),
(1, 12, 0.0150, 'G'),
(1, 13, 0.0350, 'G'),
(1, 16, 0.0280, 'ML'),
(1, 17, 0.0090, 'G'),
(2, 14, 0.0300, 'G'),
(2, 15, 0.0220, 'G');

-- ----- Entradas de estoque dos novos ingredientes -----
INSERT INTO estoque_movimentacao (ingrediente_id, tipo, quantidade, motivo, lote, custo_unitario, usuario_id) VALUES
(11, 'ENTRADA', 3000.000,  'COMPRA', 'L-BACON-01',  0.0400, 1),
(11, 'ENTRADA', 3000.000,  'COMPRA', 'L-BACON-02',  0.0400, 1),
(12, 'ENTRADA', 10000.000, 'COMPRA', 'L-BATATA-01', 0.0150, 1),
(12, 'ENTRADA', 10000.000, 'COMPRA', 'L-BATATA-02', 0.0150, 1),
(13, 'ENTRADA', 5000.000,  'COMPRA', 'L-FRANGO-01', 0.0350, 1),
(13, 'ENTRADA', 5000.000,  'COMPRA', 'L-FRANGO-02', 0.0350, 1),
(14, 'ENTRADA', 3000.000,  'COMPRA', 'L-GRAN-01',   0.0300, 1),
(15, 'ENTRADA', 2000.000,  'COMPRA', 'L-MOR-01',    0.0220, 1),
(16, 'ENTRADA', 3000.000,  'COMPRA', 'L-CHCR-01',   0.0280, 1),
(17, 'ENTRADA', 3000.000,  'COMPRA', 'L-CEB-01',    0.0090, 1);

-- ----- Ajusta AUTO_INCREMENT -----
ALTER TABLE categoria     AUTO_INCREMENT = 6;
ALTER TABLE ingrediente   AUTO_INCREMENT = 18;
ALTER TABLE prato         AUTO_INCREMENT = 15;
ALTER TABLE ficha_tecnica AUTO_INCREMENT = 15;
