-- ============================================================
-- Comanda Digital - V5 - Corrige as fotos dos pratos de acai
-- (a seed usava URLs invalidas/que nao eram acai).
-- Imagens reais de acai (Wikimedia Commons, hotlink estavel).
-- ============================================================

UPDATE prato SET foto_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Berries_Galore_Acai_Bowl_%2830276166867%29.jpg/500px-Berries_Galore_Acai_Bowl_%2830276166867%29.jpg'
WHERE id = 3;   -- Acai 500ml

UPDATE prato SET foto_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/Acai_bowl_-_A%C3%A7a%C3%AD_na_tigela.jpg/500px-Acai_bowl_-_A%C3%A7a%C3%AD_na_tigela.jpg'
WHERE id = 5;   -- Acai Especial com Banana

UPDATE prato SET foto_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Acai_bowl_%2843779425762%29.jpg/500px-Acai_bowl_%2843779425762%29.jpg'
WHERE id = 12;  -- Acai 300ml

UPDATE prato SET foto_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/A%C3%A7a%C3%AD_na_tigela_-_Acai_bowl.jpg/500px-A%C3%A7a%C3%AD_na_tigela_-_Acai_bowl.jpg'
WHERE id = 13;  -- Acai com Morango

UPDATE prato SET foto_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/A%C3%A7a%C3%AD_na_tigela.jpeg/500px-A%C3%A7a%C3%AD_na_tigela.jpeg'
WHERE id = 14;  -- Acai com Granola e Banana
