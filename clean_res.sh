#!/usr/bin/env bash
set -euo pipefail

# Módulos donde SÍ podemos borrar res/ y AndroidManifest.xml
CLEAN_MODULES=(
  "coreengine-api"
  "coreengine-runtime"
  "coreengine-render-canvas"
  "coreengine-render-gl"
  "coreengine-integration"
)

# Módulos que NO se tocan
KEEP_MODULES=(
  "coreengine-android-host"
  "miniendgine"
  "samples/demo-app"
  "coreengine"   # legacy hasta terminar migración
  "engine"       # sandbox; eliminar aparte cuando decidas
)

# Dry-run por defecto. Ejecuta con RUN=1 para aplicar cambios.
RUN=${RUN:-0}

clean_mod () {
  local m="$1"
  local res="$m/src/main/res"
  local man="$m/src/main/AndroidManifest.xml"

  if [[ -d "$res" ]]; then
    echo "[PLAN] rm -rf $res"
    [[ "$RUN" == "1" ]] && rm -rf "$res"
  fi
  if [[ -f "$man" ]]; then
    echo "[PLAN] rm $man"
    [[ "$RUN" == "1" ]] && rm -f "$man"
  fi
}

echo "== Limpieza segura de res/ y AndroidManifest.xml =="
echo "Se limpiarán módulos: ${CLEAN_MODULES[*]}"
echo "Se conservarán módulos: ${KEEP_MODULES[*]}"
echo "Modo: $([[ "$RUN" == "1" ]] && echo APPLY || echo DRY-RUN)"

for m in "${CLEAN_MODULES[@]}"; do
  if [[ -d "$m" ]]; then
    clean_mod "$m"
  else
    echo "[SKIP] $m no existe"
  fi
done

echo "Sugerido: confirmar cambios en git:"
echo "  git status"
echo "  git add -A && git commit -m 'chore(build): limpiar res y manifests en módulos core'"
