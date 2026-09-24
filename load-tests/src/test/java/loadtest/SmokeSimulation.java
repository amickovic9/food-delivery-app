package loadtest;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import loadtest.support.Feeders;
import loadtest.support.Journeys;
import loadtest.support.Targets;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SmokeSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .shareConnections();

    private final ScenarioBuilder journey = scenario("Customer journey (smoke)")
            .feed(Feeders.customers())
            .exec(Journeys.fullJourney());

    {
        System.out.println("[SmokeSimulation] target: " + Targets.describe());
        setUp(journey.injectOpen(rampUsers(20).during(Duration.ofSeconds(10))))
                .protocols(httpProtocol)
                .assertions(
                        global().failedRequests().percent().lt(1.0),
                        global().responseTime().percentile(95.0).lt(1500));
    }
}
