#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   scripts/upload_app_distribution.sh <apk_path> <app_id> <testers_or_groups>
# Example:
#   scripts/upload_app_distribution.sh \
#     app/build/outputs/apk/debug/app-debug.apk \
#     1:1234567890:android:abcdef1234567890 \
#     internal-testers

apk_path="${1:-}"
app_id="${2:-}"
testers_or_groups="${3:-}"

if [[ -z "$apk_path" || -z "$app_id" || -z "$testers_or_groups" ]]; then
  echo "Missing args."
  echo "Usage: scripts/upload_app_distribution.sh <apk_path> <app_id> <testers_or_groups>"
  exit 1
fi

if [[ ! -f "$apk_path" ]]; then
  echo "APK not found: $apk_path"
  exit 1
fi

firebase appdistribution:distribute "$apk_path" \
  --app "$app_id" \
  --release-notes-file app_distribution/release_notes.txt \
  --groups "$testers_or_groups"

