# DimDim - JSON das operações (GET, POST, PUT, DELETE)

Base URL: `https://dimdim-webapp-<RM>.azurewebsites.net/api`

A aplicação tem front-end (Thymeleaf) e também expõe o mesmo CRUD em REST.

## Clientes

### POST /api/clientes
```json
{
  "nome": "Ana Souza",
  "cpf": "123.456.789-00",
  "email": "ana.souza@dimdim.com",
  "telefone": "(11) 98888-7777"
}
```
Resposta 201:
```json
{
  "id": 1,
  "nome": "Ana Souza",
  "cpf": "123.456.789-00",
  "email": "ana.souza@dimdim.com",
  "telefone": "(11) 98888-7777",
  "dataCadastro": "2026-10-06T21:40:12.512"
}
```

### GET /api/clientes  |  GET /api/clientes/1
Retorna a lista ou o cliente de id 1 (mesmo formato acima).

### PUT /api/clientes/1
```json
{
  "nome": "Ana Souza Lima",
  "cpf": "123.456.789-00",
  "email": "ana.lima@dimdim.com",
  "telefone": "(11) 97777-6666"
}
```

### DELETE /api/clientes/1
Resposta 204 (sem corpo). Retorna 409 se o cliente ainda tiver contas.

## Contas

### POST /api/contas
```json
{
  "agencia": "0001",
  "numero": "12345-6",
  "tipo": "CORRENTE",
  "saldo": 1500.00,
  "cliente": { "id": 1 }
}
```
Resposta 201:
```json
{
  "id": 1,
  "numero": "12345-6",
  "agencia": "0001",
  "tipo": "CORRENTE",
  "saldo": 1500.00,
  "dataAbertura": "2026-10-06T21:42:03.104",
  "cliente": {
    "id": 1,
    "nome": "Ana Souza",
    "cpf": "123.456.789-00",
    "email": "ana.souza@dimdim.com",
    "telefone": "(11) 98888-7777",
    "dataCadastro": "2026-10-06T21:40:12.512"
  }
}
```
Valores aceitos em `tipo`: `CORRENTE`, `POUPANCA`, `SALARIO`.

### GET /api/contas  |  GET /api/contas/1

### PUT /api/contas/1
```json
{
  "agencia": "0001",
  "numero": "12345-6",
  "tipo": "POUPANCA",
  "saldo": 2750.50,
  "cliente": { "id": 1 }
}
```

### DELETE /api/contas/1
Resposta 204 (sem corpo).

## Exemplos com curl
```bash
URL=https://dimdim-webapp-<RM>.azurewebsites.net/api
curl -X POST $URL/clientes -H "Content-Type: application/json" \
  -d '{"nome":"Ana Souza","cpf":"123.456.789-00","email":"ana.souza@dimdim.com","telefone":"(11) 98888-7777"}'
curl $URL/clientes
curl -X PUT $URL/clientes/1 -H "Content-Type: application/json" \
  -d '{"nome":"Ana Souza Lima","cpf":"123.456.789-00","email":"ana.lima@dimdim.com","telefone":"(11) 97777-6666"}'
curl -X DELETE $URL/clientes/1
```
