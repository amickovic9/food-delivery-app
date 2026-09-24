#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

VARIANT="${1:-}"
case "$VARIANT" in
  monolith)
    COMPOSE=docker/docker-compose.monolith.yml
    BASE_ARGS=(-DbaseUrl=http://localhost:8080)
    PORTS="8080" ;;
  microservices)
    COMPOSE=docker/docker-compose.microservices.yml
    BASE_ARGS=()
    PORTS="8081 8082 8083 8084 8085 8086" ;;
  *)
    echo "usage: scripts/run-experiments.sh <monolith|microservices>" >&2
    exit 2 ;;
esac

SIMS="${SIMS:-BrowseSimulation OrderSimulation SpikeSimulation StressSimulation}"
STAMP="$(date +%Y%m%d-%H%M%S)"
OUT="results/${VARIANT}-${STAMP}"
mkdir -p "$OUT/gatling"
exec > >(tee "$OUT/run.log") 2>&1

KNOBS=()
[ -n "${BROWSE_RATE:-}" ]  && KNOBS+=(-DbrowseRate="$BROWSE_RATE")
[ -n "${ORDER_RATE:-}" ]   && KNOBS+=(-DorderRate="$ORDER_RATE")
[ -n "${STRESS_PEAK:-}" ]  && KNOBS+=(-DstressPeak="$STRESS_PEAK")
[ -n "${DURATION:-}" ]     && KNOBS+=(-Dduration="$DURATION")
[ -n "${RAMP:-}" ]         && KNOBS+=(-Dramp="$RAMP")

echo "== $VARIANT :: $STAMP =="
echo ">> docker compose up --build"
docker compose -f "$COMPOSE" up -d --build

cleanup() {
  [ -n "${STATS_PID:-}" ] && kill "$STATS_PID" 2>/dev/null || true
  echo ">> docker compose down -v"
  docker compose -f "$COMPOSE" down -v
}
trap cleanup EXIT

for p in $PORTS; do
  printf '   waiting :%s ' "$p"
  ok=""
  for _ in $(seq 1 90); do
    if [ "$(curl -s -o /dev/null -w '%{http_code}' "http://localhost:$p/actuator/health" 2>/dev/null)" = "200" ]; then
      ok=1; echo "UP"; break
    fi
    sleep 2
  done
  [ -n "$ok" ] || { echo "NOT healthy"; exit 1; }
done

echo ">> resource sampler -> $OUT/docker-stats.csv"
echo "ts,name,cpu_pct,mem_usage,mem_pct" > "$OUT/docker-stats.csv"
(
  while true; do
    ts=$(date +%s)
    docker stats --no-stream --format '{{.Name}},{{.CPUPerc}},{{.MemUsage}},{{.MemPerc}}' \
      | sed "s/^/${ts},/" >> "$OUT/docker-stats.csv"
    sleep 5
  done
) &
STATS_PID=$!
sleep 20

for sim in $SIMS; do
  echo ">> $sim"
  ./mvnw -q -pl load-tests gatling:test \
    -Dgatling.simulationClass="loadtest.$sim" \
    "${BASE_ARGS[@]}" "${KNOBS[@]}" || echo "   (assertions failed for $sim — kept going)"
  cp -r load-tests/target/gatling/* "$OUT/gatling/" 2>/dev/null || true
  rm -rf load-tests/target/gatling/* 2>/dev/null || true
  sleep 15
done

echo ">> done -> $OUT"
