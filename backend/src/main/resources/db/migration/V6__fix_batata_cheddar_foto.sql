-- ============================================================
-- Comanda Digital - V6 - Corrige a foto da "Batata Cheddar & Bacon"
-- (estava com foto de batata simples; agora mostra cheddar + bacon).
-- ============================================================

UPDATE prato SET foto_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Cheese_Fries_and_Bacon.JPG/500px-Cheese_Fries_and_Bacon.JPG'
WHERE id = 10;  -- Batata Cheddar & Bacon
