#!/usr/bin/env bash
# =====================================================================
# DimDim - Deploy automatizado via Azure CLI (az webapp deploy)
# Uso: export RM=557123 && ./scripts/02-deploy-az-webapp.sh
# =====================================================================
set -euo pipefail
: "${RM:?Defina a variavel RM. Ex: export RM=557123}"

RG="rg-dimdim-${RM}"
WEBAPP="dimdim-webapp-${RM}"

echo ">> Build do projeto"
mvn -B clean package -DskipTests

echo ">> Deploy do JAR"
az webapp deploy \
  --resource-group "$RG" \
  --name "$WEBAPP" \
  --src-path target/dimdim.jar \
  --type jar

az webapp restart --resource-group "$RG" --name "$WEBAPP"
echo ">> Pronto: https://${WEBAPP}.azurewebsites.net"
