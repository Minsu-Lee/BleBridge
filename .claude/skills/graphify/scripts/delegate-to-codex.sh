#!/usr/bin/env bash
set -euo pipefail

project_dir="${CLAUDE_PROJECT_DIR:-$PWD}"
request="${1:-}"
prompt='$graphify'

if [[ -n "$request" ]]; then
  prompt+=" $request"
fi

exec codex exec \
  --cd "$project_dir" \
  --sandbox workspace-write \
  --ephemeral \
  "$prompt"
