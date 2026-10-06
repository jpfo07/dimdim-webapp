#!/usr/bin/env bash
# =====================================================================
# DimDim - Criacao da infraestrutura no Azure via Azure CLI
# Cria: Resource Group, Azure SQL Server + Database, Log Analytics,
#       Application Insights, App Service Plan (Linux) e Web App Java 17
# Uso:
#   export RM=557123                         # RM de um integrante (deixa os nomes unicos)
#   export SQL_ADMIN_PASSWORD='SuaSenhaForte@2026'
#   ./scripts/01-criar-infra.sh
# =====================================================================
set -euo pipefail

: "${RM:?Defina a variavel RM. Ex: export RM=557123}"

LOCATION="${LOCATION:-brazilsouth}"
RG="rg-dimdim-${RM}"
SQL_SERVER="sql-dimdim-${RM}"
SQL_DB="dimdimdb"
SQL_ADMIN="${SQL_ADMIN:-dimdimadmin}"
LAW="log-dimdim-${RM}"
APPINSIGHTS="ai-dimdim-${RM}"
PLAN="plan-dimdim-${RM}"
WEBAPP="dimdim-webapp-${RM}"

# Senha nunca fica no repositorio: vem do ambiente ou e pedida no terminal
if [ -z "${SQL_ADMIN_PASSWORD:-}" ]; then
  read -r -s -p "Senha do admin do SQL Server: " SQL_ADMIN_PASSWORD
  echo
fi

# Instala extensoes da CLI (ex: application-insights) sem perguntar
az config set extension.use_dynamic_install=yes_without_prompt -o none

echo ">> Registrando providers"
for p in Microsoft.Web Microsoft.Sql Microsoft.Insights Microsoft.OperationalInsights; do
  az provider register --namespace "$p" --wait
done

echo ">> Resource Group"
az group create --name "$RG" --location "$LOCATION" -o table

echo ">> Azure SQL Server (PaaS)"
az sql server create \
  --name "$SQL_SERVER" \
  --resource-group "$RG" \
  --location "$LOCATION" \
  --admin-user "$SQL_ADMIN" \
  --admin-password "$SQL_ADMIN_PASSWORD" -o table

echo ">> Azure SQL Database"
az sql db create \
  --resource-group "$RG" \
  --server "$SQL_SERVER" \
  --name "$SQL_DB" \
  --service-objective Basic \
  --backup-storage-redundancy Local -o table

echo ">> Firewall: liberar servicos do Azure (Web App) e o IP da sua maquina"
az sql server firewall-rule create \
  --resource-group "$RG" --server "$SQL_SERVER" \
  --name AllowAzureServices \
  --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0 -o table

MEU_IP="$(curl -s https://api.ipify.org || true)"
if [ -n "$MEU_IP" ]; then
  az sql server firewall-rule create \
    --resource-group "$RG" --server "$SQL_SERVER" \
    --name AllowMeuIP \
    --start-ip-address "$MEU_IP" --end-ip-address "$MEU_IP" -o table
fi

echo ">> Log Analytics Workspace + Application Insights"
az monitor log-analytics workspace create \
  --resource-group "$RG" --workspace-name "$LAW" --location "$LOCATION" -o table

LAW_ID="$(az monitor log-analytics workspace show -g "$RG" -n "$LAW" --query id -o tsv)"

az monitor app-insights component create \
  --app "$APPINSIGHTS" \
  --resource-group "$RG" \
  --location "$LOCATION" \
  --application-type web \
  --workspace "$LAW_ID" -o table

AI_CONN="$(az monitor app-insights component show -g "$RG" --app "$APPINSIGHTS" --query connectionString -o tsv)"

echo ">> App Service Plan (Linux) + Web App Java 17"
az appservice plan create \
  --name "$PLAN" --resource-group "$RG" --location "$LOCATION" \
  --is-linux --sku B1 -o table

az webapp create \
  --name "$WEBAPP" --resource-group "$RG" --plan "$PLAN" \
  --runtime "JAVA:17-java17" -o table

echo ">> App Settings (conexao do banco e Application Insights ficam aqui, fora do codigo)"
JDBC_URL="jdbc:sqlserver://${SQL_SERVER}.database.windows.net:1433;database=${SQL_DB};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

az webapp config appsettings set \
  --name "$WEBAPP" --resource-group "$RG" \
  --settings \
    SERVER_PORT=80 \
    SPRING_DATASOURCE_URL="$JDBC_URL" \
    SPRING_DATASOURCE_USERNAME="$SQL_ADMIN" \
    SPRING_DATASOURCE_PASSWORD="$SQL_ADMIN_PASSWORD" \
    APPLICATIONINSIGHTS_CONNECTION_STRING="$AI_CONN" \
    APPLICATIONINSIGHTS_ROLE_NAME="$WEBAPP" -o none

az webapp log config --name "$WEBAPP" --resource-group "$RG" \
  --application-logging filesystem --level information -o none

echo
echo "================================================================"
echo " Infra criada!"
echo " Web App : https://${WEBAPP}.azurewebsites.net"
echo " SQL     : ${SQL_SERVER}.database.windows.net / ${SQL_DB}"
echo " Proximo : rode scripts/ddl.sql no Query Editor do banco"
echo "           depois faca o deploy (02 ou 03)"
echo "================================================================"
