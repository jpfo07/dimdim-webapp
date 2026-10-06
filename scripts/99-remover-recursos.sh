#!/usr/bin/env bash
# DimDim - Remove todos os recursos criados (rodar depois da correcao)
set -euo pipefail
: "${RM:?Defina a variavel RM. Ex: export RM=557123}"
az group delete --name "rg-dimdim-${RM}" --yes --no-wait
echo ">> Exclusao do resource group rg-dimdim-${RM} iniciada"
