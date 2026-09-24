package loadtest.support;

import io.gatling.javaapi.core.ChainBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;

public final class Journeys {

    private Journeys() {
    }

    public static ChainBuilder browse() {
        return exec(http("list restaurants")
                .get(Targets.RESTAURANT + "/restaurants")
                .check(status().is(200))
                .check(jsonPath("$[0].id").saveAs("restaurantId")))
                .pause(Duration.ofMillis(200), Duration.ofMillis(600))
                .exec(http("get menu")
                        .get(Targets.RESTAURANT + "/restaurants/#{restaurantId}")
                        .check(status().is(200))
                        .check(jsonPath("$.menu[0].id").saveAs("itemA"))
                        .check(jsonPath("$.menu[1].id").saveAs("itemB")));
    }

    public static ChainBuilder login() {
        return exec(http("login")
                .post(Targets.AUTH + "/auth/login")
                .body(StringBody("{\"email\":\"#{email}\",\"password\":\"" + Feeders.PASSWORD + "\"}"))
                .check(status().is(200))
                .check(jsonPath("$.token").saveAs("token")));
    }

    public static ChainBuilder placeOrder() {
        return exec(http("place order")
                .post(Targets.ORDER + "/orders")
                .body(StringBody("""
                        {"userId":#{userId},"restaurantId":#{restaurantId},
                         "address":"Test Street 1","note":"ring the bell","paymentMethod":"CARD",
                         "items":[{"menuItemId":#{itemA},"quantity":1},
                                  {"menuItemId":#{itemB},"quantity":2}]}"""))
                .check(status().is(200))
                .check(jsonPath("$.id").saveAs("orderId"))
                .check(jsonPath("$.status").exists()));
    }

    public static ChainBuilder readOrderBack() {
        return exec(http("get order")
                .get(Targets.ORDER + "/orders/#{orderId}")
                .check(status().is(200)));
    }

    public static ChainBuilder fullJourney() {
        return browse()
                .pause(Duration.ofMillis(300), Duration.ofMillis(800))
                .exec(login())
                .pause(Duration.ofMillis(200), Duration.ofMillis(500))
                .exec(placeOrder())
                .pause(Duration.ofMillis(200), Duration.ofMillis(600))
                .exec(readOrderBack());
    }
}
