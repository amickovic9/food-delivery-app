#!/usr/bin/env bash
cd "$(dirname "$0")/.."

PIDFILE=.ms-local-pids
[ -f "$PIDFILE" ] || { echo "No $PIDFILE — nothing to stop."; exit 0; }

while read -r pid; do
  [ -n "$pid" ] || continue
  if kill "$pid" 2>/dev/null || taskkill //PID "$pid" //F >/dev/null 2>&1; then
    echo "stopped $pid"
  fi
done < "$PIDFILE"
rm -f "$PIDFILE"
