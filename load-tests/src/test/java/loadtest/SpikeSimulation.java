package loadtest;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import loadtest.support.Feeders;
import loadtest.support.Journeys;
import loadtest.support.Params;
import loadtest.support.Targets;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SpikeSimulation extends Simulation {

    private final double base = Params.doubleProp("spikeBase", 5);
    private final double spike = Params.doubleProp("spikeRate", 60);
    private final int burst = Params.intProp("spikeBurst", 200);
    private final Duration settle = Params.seconds("settleSecs", 30);
    private final Duration spikeFor = Params.seconds("spikeSecs", 20);
    private final Duration recover = Params.seconds("recoverSecs", 90);

    private final HttpProtocolBuilder httpProtocol = http
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .shareConnections();

    private final ScenarioBuilder scn = scenario("Spike & recover")
            .feed(Feeders.customers())
            .exec(Journeys.fullJourney());

    {
        System.out.printf("[SpikeSimulation] target: %s | base=%.1f/s spike=%.1f/s burst=%d%n",
                Targets.describe(), base, spike, burst);
        setUp(scn.injectOpen(
                constantUsersPerSec(base).during(settle),
                atOnceUsers(burst),
                constantUsersPerSec(spike).during(spikeFor),
                constantUsersPerSec(base).during(recover)
        )).protocols(httpProtocol)
                .assertions(global().successfulRequests().percent().gt(80.0));
    }
}
