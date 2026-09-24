package loadtest;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import loadtest.support.Feeders;
import loadtest.support.Journeys;
import loadtest.support.Params;
import loadtest.support.Targets;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class StressSimulation extends Simulation {

    private final double start = Params.doubleProp("stressStart", 2);
    private final double peak = Params.doubleProp("stressPeak", 60);
    private final Duration over = Params.seconds("duration", 240);

    private final HttpProtocolBuilder httpProtocol = http
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .shareConnections();

    private final ScenarioBuilder scn = scenario("Stress ramp")
            .feed(Feeders.customers())
            .exec(Journeys.fullJourney());

    {
        System.out.printf("[StressSimulation] target: %s | %.1f→%.1f users/s over %ds%n",
                Targets.describe(), start, peak, over.toSeconds());
        setUp(scn.injectOpen(
                rampUsersPerSec(start).to(peak).during(over)
        )).protocols(httpProtocol)
                .assertions(global().successfulRequests().percent().gt(50.0));
    }
}
