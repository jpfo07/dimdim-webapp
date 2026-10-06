# Roteiro do vídeo (mínimo 720p, com explicação falada)

Penalidades que o roteiro evita: vídeo ruim ou mudo (-3), não mostrar cada operação no banco (-3), sem Application Insights (-1,5).

1. **Abertura (30s)**: grupo, integrantes, proposta da DimDim e o desenho da arquitetura (docs/arquitetura.png).
2. **Repositório (1 min)**: mostrar README, pasta `scripts` (DDL + scripts CLI), workflow em `.github/workflows` e o código.
3. **Criação dos recursos (3 min)**: `az login`, `export RM=...`, `export SQL_ADMIN_PASSWORD=...` e rodar `./scripts/01-criar-infra.sh`. Mostrar os recursos criados no portal (Resource Group).
4. **DDL (1 min)**: abrir o banco `dimdimdb` > Query editor, rodar `scripts/ddl.sql` e mostrar as duas tabelas e a FK.
5. **Deploy (2 min)**: rodar `./scripts/03-configurar-github-actions.sh`, fazer um push na main e mostrar o workflow verde no Actions. Abrir a URL do Web App.
6. **CRUD com persistência (5 min)**, sempre alternando aplicação e Query Editor rodando `scripts/consultas-evidencias.sql`:
   - Cliente: criar 2 → SELECT; editar 1 → SELECT; (excluir no fim)
   - Conta: abrir 2 contas → SELECT + JOIN; editar saldo → SELECT; excluir 1 → SELECT
   - Cliente: excluir o cliente sem conta → SELECT
7. **Application Insights (2 min)**: Live Metrics durante o uso, Transaction search, Application map (Web App → SQL), aba Performance e Failures. Mostrar as dependências SQL com as queries.
8. **Encerramento (20s)**: link do repositório.
