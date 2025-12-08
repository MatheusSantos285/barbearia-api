-- 1. Atualizações na tabela CLIENTES
-- Adiciona a coluna email
ALTER TABLE clientes ADD COLUMN email VARCHAR(255);

-- Adiciona a restrição de unicidade no email (dois clientes não podem ter o mesmo email)
ALTER TABLE clientes ADD CONSTRAINT uc_clientes_email UNIQUE (email);

-- Adiciona a coluna senha_hash
ALTER TABLE clientes ADD COLUMN senha_hash VARCHAR(255);


-- 2. Atualizações na tabela BARBEIROS
ALTER TABLE barbeiros ADD COLUMN senha_hash VARCHAR(255);

-- DICA DE SÊNIOR:
-- Se o banco já tivesse dados reais, adicionar uma coluna "NOT NULL" quebraria o sistema.
-- Como estamos em dev, deixamos NULLABLE por enquanto.
-- No futuro, quando criarmos o fluxo de cadastro, o Java vai garantir que o email e senha sejam obrigatórios.