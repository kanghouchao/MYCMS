#!/usr/bin/env bash
# Replaces the literal string <branch_name> in reports/index.html with the actual branch name
# Branch name is provided by the GitHub Actions workflow via the BRANCH_NAME environment variable
set -euo pipefail

REPO_ROOT="${REPO_ROOT:-$(cd "$(dirname "$0")/../.." && pwd)}"
META_FILE="$REPO_ROOT/reports/index.html"

BRANCH_NAME="${BRANCH_NAME:-unknown}"

if [ ! -f "$META_FILE" ]; then
  echo "Error: $META_FILE not found"
  exit 1
fi

export BRANCH_NAME
perl -i -pe 's/<branch_name>/\Q$ENV{BRANCH_NAME}\E/g' "$META_FILE"

echo "Replaced <branch_name> with '$BRANCH_NAME' in $META_FILE"
exit 0
