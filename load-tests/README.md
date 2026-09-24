# load-tests — Gatling suite (Faza 2)

Puts the **monolith** and the **microservice** build under an identical customer
journey and records latency, throughput, error rate and (via `docker stats`)
CPU/RAM. All simulations drive the same endpoints through
`src/test/java/loadtest/support/Journeys.java`; only the injection profile differs.

## The journey

`GET /restaurants` → `GET /restaurants/{id}` → `POST /auth/login` →
`POST /orders` → `GET /orders/{id}`

Seeded identities (all password `password`): customers `user1..20@fink.dev`
(ids 7–26), couriers `courier1..5@fink.dev` (ids 2–6). A simulated ~3 % payment
decline still returns **HTTP 200** with status `PAYMENT_FAILED` — a business
outcome, not a failed request.

## Targeting

| Variant | how |
|---|---|
| Monolith | `-DbaseUrl=http://localhost:8080` (one app, every endpoint) |
| Microservices | omit `baseUrl`; defaults `auth :8081`, `restaurant :8082`, `order :8086` |
| Override one | `-DauthUrl=` / `-DrestaurantUrl=` / `-DorderUrl=` |

## Simulations

| Class | Injection | What it measures |
|---|---|---|
| `SmokeSimulation` | ramp 20 users / 10 s | wiring sanity before a real run |
| `BrowseSimulation` | ramp → `constantUsersPerSec` | read-only baseline (restaurant-service only in micro) |
| `OrderSimulation` | ramp → `constantUsersPerSec` | **core comparison** — 1 local tx vs 3 network hops |
| `StressSimulation` | `rampUsersPerSec start → peak` | saturation point / scaling behaviour |
| `SpikeSimulation` | baseline → burst + spike → baseline | fault tolerance & recovery time |

### Knobs (`-D…`, all optional)

`browseRate` · `orderRate` · `stressStart` · `stressPeak` ·
`spikeBase` · `spikeRate` · `spikeBurst` · `ramp` · `duration` ·
`settleSecs` · `spikeSecs` · `recoverSecs` · `password`

Every simulation prints its resolved target and knobs on startup.

## Run one

```bash
# start a target first (see repo CLAUDE.md), then:
./mvnw -pl load-tests gatling:test -Dgatling.simulationClass=loadtest.OrderSimulation -DbaseUrl=http://localhost:8080
./mvnw -pl load-tests gatling:test -Dgatling.simulationClass=loadtest.OrderSimulation -DorderRate=15 -Dduration=180
```

Report: `load-tests/target/gatling/<sim>-<timestamp>/index.html`.

## Run a full experiment (Faza 3)

`scripts/run-experiments.sh` brings a variant up with Docker Compose, waits for
health, samples `docker stats` every 5 s, runs the suite, then tears down.

```bash
scripts/run-experiments.sh monolith
scripts/run-experiments.sh microservices
SIMS="OrderSimulation SpikeSimulation" ORDER_RATE=12 scripts/run-experiments.sh microservices
```

Output lands in `results/<variant>-<timestamp>/` (git-ignored): per-sim Gatling
reports under `gatling/`, `docker-stats.csv`, `run.log`. Run both variants, then
compare `OrderSimulation` p95/p99 and the CSV's CPU/RAM columns.
