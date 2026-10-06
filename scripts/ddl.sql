-- =====================================================================
-- DimDim - DDL das tabelas (Azure SQL Database)
-- Executar no Query Editor do portal Azure ou via sqlcmd
-- Relacionamento: cliente (1) ---- (N) conta
-- =====================================================================

IF OBJECT_ID('dbo.conta', 'U') IS NOT NULL DROP TABLE dbo.conta;
IF OBJECT_ID('dbo.cliente', 'U') IS NOT NULL DROP TABLE dbo.cliente;
GO

CREATE TABLE dbo.cliente (
    id             BIGINT IDENTITY(1,1) NOT NULL,
    nome           NVARCHAR(100)        NOT NULL,
    cpf            VARCHAR(14)          NOT NULL,
    email          NVARCHAR(120)        NOT NULL,
    telefone       VARCHAR(20)          NULL,
    data_cadastro  DATETIME2            NOT NULL CONSTRAINT df_cliente_data DEFAULT SYSDATETIME(),
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT uk_cliente_cpf UNIQUE (cpf)
);
GO

CREATE TABLE dbo.conta (
    id             BIGINT IDENTITY(1,1) NOT NULL,
    numero         VARCHAR(20)          NOT NULL,
    agencia        VARCHAR(10)          NOT NULL,
    tipo           VARCHAR(20)          NOT NULL,
    saldo          DECIMAL(15,2)        NOT NULL CONSTRAINT df_conta_saldo DEFAULT 0,
    data_abertura  DATETIME2            NOT NULL CONSTRAINT df_conta_data DEFAULT SYSDATETIME(),
    cliente_id     BIGINT               NOT NULL,
    CONSTRAINT pk_conta PRIMARY KEY (id),
    CONSTRAINT uk_conta_numero UNIQUE (numero),
    CONSTRAINT ck_conta_tipo CHECK (tipo IN ('CORRENTE', 'POUPANCA', 'SALARIO')),
    CONSTRAINT ck_conta_saldo CHECK (saldo >= 0),
    CONSTRAINT fk_conta_cliente FOREIGN KEY (cliente_id) REFERENCES dbo.cliente (id)
);
GO

CREATE INDEX ix_conta_cliente ON dbo.conta (cliente_id);
GO
