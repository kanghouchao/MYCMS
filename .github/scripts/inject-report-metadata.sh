#!/usr/bin/env bash
# Replaces the literal string <branch_name> in reports/index.html with the actual branch name
# Branch name is provided by the GitHub Actions workflow via the BRANCH_NAME environment variable
set -euo pipefail

REPO_ROOT="${REPO_ROOT:-$(cd "$(dirname "$0")/../.." && pwd)}"
META_FILE="$REPO_ROOT/reports/index.html"
NOW=$(date -u +"%Y-%m-%d %H:%M:%S")

BRANCH_NAME="${BRANCH_NAME:-unknown}"
PR_NUMBER="${PR_NUMBER:-N/A}"

if [ ! -f "$META_FILE" ]; then
  echo "Error: $META_FILE not found"
  exit 1
fi

export NOW
perl -i -pe 's/<report_generated_time>/$ENV{NOW}/g' "$META_FILE"
export BRANCH_NAME
perl -i -pe 's/<branch_name>/$ENV{BRANCH_NAME}/g' "$META_FILE"
export PR_NUMBER
perl -i -pe 's/<pr_number>/$ENV{PR_NUMBER}/g' "$META_FILE"

echo "Replaced <branch_name> with '$BRANCH_NAME' in $META_FILE"
exit 0
