-- =====================================================================
-- Consultas para mostrar a persistencia no video (rodar apos cada operacao)
-- =====================================================================

-- Clientes
SELECT * FROM dbo.cliente ORDER BY id;

-- Contas
SELECT * FROM dbo.conta ORDER BY id;

-- Relacionamento (JOIN cliente x conta)
SELECT c.id AS cliente_id, c.nome, c.cpf,
       ct.id AS conta_id, ct.agencia, ct.numero, ct.tipo, ct.saldo
FROM dbo.cliente c
LEFT JOIN dbo.conta ct ON ct.cliente_id = c.id
ORDER BY c.id, ct.id;
