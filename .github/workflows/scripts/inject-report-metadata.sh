#!/usr/bin/env bash
set -euo pipefail

# inject-report-metadata.sh
# Replaces the contents between REPORT_METADATA_START and REPORT_METADATA_END
# markers in reports/index.html with an HTML fragment containing branch, PR
# and timestamp information.
#
# Environment variables used (set by GitHub Actions):
# - GITHUB_REF_NAME (branch name)
# - GITHUB_EVENT_NAME (event name, e.g. pull_request)
# - GITHUB_REPOSITORY (owner/repo)
# - GITHUB_TOKEN (optional, used to fetch PR body)
# - GITHUB_EVENT_PULL_REQUEST_NUMBER (optional, can be passed in)
#
# Usage (CI):
#   bash ./.github/workflows/scripts/inject-report-metadata.sh
#
# Usage (local test):
#   BRANCH_NAME=my-branch PR_NUMBER=123 GITHUB_TOKEN=... bash ./.github/workflows/scripts/inject-report-metadata.sh

REPO_ROOT="${REPO_ROOT:-$(cd "$(dirname "$0")/../.." && pwd)}"
META_FILE="$REPO_ROOT/reports/index.html"

BRANCH_NAME="${BRANCH_NAME:-${GITHUB_REF_NAME:-unknown}}"
EVENT_NAME="${EVENT_NAME:-${GITHUB_EVENT_NAME:-unknown}}"
REPO="${REPO:-${GITHUB_REPOSITORY:-}}"
GITHUB_TOKEN="${GITHUB_TOKEN:-}"
PR_NUMBER="${PR_NUMBER:-${GITHUB_EVENT_PULL_REQUEST_NUMBER:-}}"

# Helper: escape HTML special chars minimally
escape_html() {
  local s
  s="$1"
  s="${s//&/&amp;}"
  s="${s//</&lt;}"
  s="${s//>/&gt;}"
  s="${s//\"/&quot;}"
  s="${s//\'/&#39;}"
  printf '%s' "$s"
}

# Fetch PR title and author if event is pull_request and PR_NUMBER is known
PR_TITLE=""
PR_AUTHOR=""
PR_HTML_URL=""
if [ "$EVENT_NAME" = "pull_request" ] || [ -n "$PR_NUMBER" ]; then
  if [ -z "$PR_NUMBER" ]; then
    echo "No PR_NUMBER provided; skipping PR metadata fetch"
  else
    if [ -n "$GITHUB_TOKEN" ] && [ -n "$REPO" ]; then
      PR_JSON=$(curl -sSL -H "Authorization: Bearer $GITHUB_TOKEN" -H "Accept: application/vnd.github+json" \
        "https://api.github.com/repos/$REPO/pulls/$PR_NUMBER" 2>/dev/null || true)
      PR_TITLE_RAW=$(printf '%s' "$PR_JSON" | jq -r '.title // ""' 2>/dev/null || true)
      PR_AUTHOR_RAW=$(printf '%s' "$PR_JSON" | jq -r '.user.login // ""' 2>/dev/null || true)
      PR_HTML_URL=$(printf '%s' "$PR_JSON" | jq -r '.html_url // ""' 2>/dev/null || true)
      PR_TITLE=$(escape_html "${PR_TITLE_RAW}")
      PR_AUTHOR=$(escape_html "${PR_AUTHOR_RAW}")
    else
      echo "GITHUB_TOKEN or GITHUB_REPOSITORY not set; cannot fetch PR metadata"
    fi
  fi
fi

TIMESTAMP="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
PR_LABEL="${PR_NUMBER:-N/A}"

PR_EXTRA=""
if [ -n "$PR_TITLE" ]; then
  PR_URL_ESC=$(escape_html "$PR_HTML_URL")
  PR_EXTRA="<br/><strong>PR title</strong>: <a href=\"${PR_URL_ESC}\">${PR_TITLE}</a><br/><strong>Author</strong>: ${PR_AUTHOR}"
fi

METADATA_HTML="<div style=\"background:#f6f8fa;padding:12px;border-radius:6px;\">\n  <strong>Report generated</strong>: ${TIMESTAMP} UTC<br/>\n  <strong>Branch</strong>: $(escape_html \"$BRANCH_NAME\")<br/>\n  <strong>PR</strong>: ${PR_LABEL}${PR_EXTRA}\n</div>"

if [ ! -f "$META_FILE" ]; then
  echo "Warning: $META_FILE not found; creating a minimal index with metadata block"
  mkdir -p "$(dirname "$META_FILE")"
  printf '%s' "<!DOCTYPE html>\n<html><body>\n<!-- REPORT_METADATA_START: metadata injected by CI - do not edit manually -->\n<div id=\"report-metadata\">${METADATA_HTML}</div>\n<!-- REPORT_METADATA_END: metadata injected by CI - do not edit manually -->\n</body></html>" > "$META_FILE"
  exit 0
fi

# Replace content between REPORT_METADATA_START and REPORT_METADATA_END
# Use awk to preserve the rest of the file
awk -v meta="$METADATA_HTML" '
  BEGIN{RS=""; ORS="\n\n"}
  {
    start = "<!-- REPORT_METADATA_START: metadata injected by CI - do not edit manually -->"
    end = "<!-- REPORT_METADATA_END: metadata injected by CI - do not edit manually -->"
    if (index($0, start) && index($0, end)) {
      gsub(start "[\n\r\t ]*<div id=\"report-metadata\">[\s\S]*?<\/div>[\n\r\t ]*" end, start "\n<div id=\"report-metadata\">" meta "</div>\n" end)
    }
    print
  }
' "$META_FILE" > "$META_FILE.tmp" && mv "$META_FILE.tmp" "$META_FILE"

echo "Injected metadata into $META_FILE"
chmod +x "$META_FILE"

exit 0
