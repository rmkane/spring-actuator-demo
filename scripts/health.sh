#!/usr/bin/env bash

set -euo pipefail

HOST="${HOST:-localhost}"
PORT="${PORT:-8080}"

curl --fail --silent \
    "http://${HOST}:${PORT}/actuator/health" | jq
echo
