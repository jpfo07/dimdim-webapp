#!/usr/bin/env bash
# =====================================================================
# DimDim - Prepara o deploy automatizado via GitHub Actions
# 1) Habilita credenciais de publicacao no Web App
# 2) Baixa o Publish Profile
# 3) Grava os secrets no repositorio (precisa do GitHub CLI logado: gh auth login)
# Uso: export RM=557123 && ./scripts/03-configurar-github-actions.sh
# =====================================================================
set -euo pipefail
: "${RM:?Defina a variavel RM. Ex: export RM=557123}"

RG="rg-dimdim-${RM}"
WEBAPP="dimdim-webapp-${RM}"

echo ">> Habilitando basic auth de publicacao (exigido pelo publish profile)"
for tipo in scm ftp; do
  az resource update \
    --resource-group "$RG" \
    --namespace Microsoft.Web \
    --resource-type basicPublishingCredentialsPolicies \
    --parent "sites/${WEBAPP}" \
    --name "$tipo" \
    --set properties.allow=true -o none
done

echo ">> Gerando publish profile"
az webapp deployment list-publishing-profiles \
  --resource-group "$RG" --name "$WEBAPP" --xml > /tmp/publish-profile.xml

if command -v gh >/dev/null 2>&1; then
  echo ">> Gravando secrets no GitHub"
  gh secret set AZURE_WEBAPP_NAME --body "$WEBAPP"
  gh secret set AZURE_WEBAPP_PUBLISH_PROFILE < /tmp/publish-profile.xml
  rm -f /tmp/publish-profile.xml
  echo ">> Secrets criados. Faca um push na main (ou rode o workflow manualmente) para disparar o deploy."
else
  echo ">> GitHub CLI nao encontrado. Crie manualmente em Settings > Secrets and variables > Actions:"
  echo "   AZURE_WEBAPP_NAME            = ${WEBAPP}"
  echo "   AZURE_WEBAPP_PUBLISH_PROFILE = conteudo de /tmp/publish-profile.xml"
  echo "   Depois APAGUE o arquivo: rm /tmp/publish-profile.xml"
fi
