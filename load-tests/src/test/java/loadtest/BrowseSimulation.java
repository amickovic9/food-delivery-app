package loadtest;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import loadtest.support.Feeders;
import loadtest.support.Journeys;
import loadtest.support.Params;
import loadtest.support.Targets;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class BrowseSimulation extends Simulation {

    private final double rate = Params.doubleProp("browseRate", 30);
    private final Duration ramp = Params.seconds("ramp", 15);
    private final Duration hold = Params.seconds("duration", 60);

    private final HttpProtocolBuilder httpProtocol = http
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .shareConnections();

    private final ScenarioBuilder scn = scenario("Browse catalogue")
            .feed(Feeders.customers())
            .exec(Journeys.browse());

    {
        System.out.printf("[BrowseSimulation] target: %s | rate=%.1f/s ramp=%ds hold=%ds%n",
                Targets.describe(), rate, ramp.toSeconds(), hold.toSeconds());
        setUp(scn.injectOpen(
                rampUsersPerSec(1).to(rate).during(ramp),
                constantUsersPerSec(rate).during(hold)
        )).protocols(httpProtocol)
                .assertions(
                        global().failedRequests().percent().lt(1.0),
                        global().responseTime().percentile(99.0).lt(1000));
    }
}
