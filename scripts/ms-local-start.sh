#!/usr/bin/env bash
set -e
cd "$(dirname "$0")/.."

PIDFILE=.ms-local-pids

if ! ls services/order-service/target/order-service.jar >/dev/null 2>&1; then
  echo "Jars missing — building (./mvnw package -DskipTests)..."
  ./mvnw -q package -DskipTests
fi

: > "$PIDFILE"
start() {
  SERVER_PORT="$2" SPRING_PROFILES_ACTIVE=local \
    nohup java -jar "services/$1/target/$1.jar" > "/tmp/$1.log" 2>&1 &
  echo "$!" >> "$PIDFILE"
  printf '  %-22s http://localhost:%s   (pid %s, log /tmp/%s.log)\n' "$1" "$2" "$!" "$1"
}

echo "Starting microservices..."
start auth-service          8081
start restaurant-service    8082
start payment-service       8083
start notification-service  8084
start delivery-service      8085
start order-service         8086

echo "Waiting for /actuator/health ..."
for p in 8081 8082 8083 8084 8085 8086; do
  ok=""
  for _ in $(seq 1 40); do
    if [ "$(curl -s -o /dev/null -w '%{http_code}' "http://localhost:$p/actuator/health" 2>/dev/null)" = "200" ]; then
      ok=1; break
    fi
    sleep 2
  done
  if [ -n "$ok" ]; then echo "  :$p UP"; else echo "  :$p NOT healthy — see /tmp log"; fi
done

echo "Ready. Stop with: scripts/ms-local-stop.sh"
