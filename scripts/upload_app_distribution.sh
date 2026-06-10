#!/usr/bin/env bash
set -euo pipefail

# Sube una build (APK/AAB) a Firebase App Distribution usando la Firebase CLI.
#
# Uso:
#   scripts/upload_app_distribution.sh [apk_o_aab] [app_id] [grupo]
#
# Todos los argumentos son opcionales y tienen valores por defecto:
#   apk_o_aab : ruta al artefacto. Por defecto: app/build/outputs/apk/debug/app-debug.apk
#   app_id    : Firebase App ID. Si se omite, se autodetecta desde app/google-services.json
#               para el paquete com.santiago.soberpath.
#   grupo     : grupo de testers. Por defecto: internal-testers
#
# Ejemplos:
#   scripts/upload_app_distribution.sh
#   scripts/upload_app_distribution.sh app/build/outputs/apk/release/app-release.apk
#   scripts/upload_app_distribution.sh app/build/outputs/bundle/release/app-release.aab "" qa-team

PACKAGE="com.santiago.soberpath"
GOOGLE_SERVICES="app/google-services.json"
RELEASE_NOTES="app_distribution/release_notes.txt"

apk_path="${1:-app/build/outputs/apk/debug/app-debug.apk}"
app_id="${2:-}"
group="${3:-internal-testers}"

# --- Validaciones de entorno -------------------------------------------------
if ! command -v firebase >/dev/null 2>&1; then
  echo "ERROR: la Firebase CLI no esta instalada."
  echo "Instalala con:  npm install -g firebase-tools   y luego:  firebase login"
  exit 1
fi

if [[ ! -f "$apk_path" ]]; then
  echo "ERROR: artefacto no encontrado: $apk_path"
  echo "Genera primero la build, por ejemplo:  ./gradlew assembleDebug"
  exit 1
fi

# --- Autodeteccion del App ID ------------------------------------------------
if [[ -z "$app_id" ]]; then
  if [[ ! -f "$GOOGLE_SERVICES" ]]; then
    echo "ERROR: no se encontro $GOOGLE_SERVICES y no se paso un app_id."
    exit 1
  fi
  if command -v python3 >/dev/null 2>&1; then
    app_id="$(python3 - "$GOOGLE_SERVICES" "$PACKAGE" <<'PY'
import json, sys
with open(sys.argv[1]) as f:
    data = json.load(f)
pkg = sys.argv[2]
for client in data.get("client", []):
    info = client.get("client_info", {})
    if info.get("android_client_info", {}).get("package_name") == pkg:
        print(info.get("mobilesdk_app_id", ""))
        break
PY
)"
  fi
  if [[ -z "$app_id" ]]; then
    echo "ERROR: no se pudo autodetectar el App ID para $PACKAGE."
    echo "Pasalo manualmente como segundo argumento (1:NUMERO:android:HASH)."
    exit 1
  fi
  echo "App ID autodetectado: $app_id"
fi

# --- Subida ------------------------------------------------------------------
echo "Subiendo $apk_path"
echo "  app:    $app_id"
echo "  grupo:  $group"
echo "  notas:  $RELEASE_NOTES"

firebase appdistribution:distribute "$apk_path" \
  --app "$app_id" \
  --release-notes-file "$RELEASE_NOTES" \
  --groups "$group"

echo "Listo. Revisa Firebase Console > App Distribution para ver la release."
