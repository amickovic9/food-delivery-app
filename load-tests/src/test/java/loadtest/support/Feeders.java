package loadtest.support;

import io.gatling.javaapi.core.FeederBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.gatling.javaapi.core.CoreDsl.listFeeder;

public final class Feeders {

    private Feeders() {
    }

    public static final String PASSWORD = System.getProperty("password", "password");

    private static final List<Map<String, Object>> CUSTOMERS = IntStream.rangeClosed(7, 26)
            .mapToObj(id -> Map.<String, Object>of(
                    "userId", id,
                    "email", "user" + (id - 6) + "@fink.dev"))
            .collect(Collectors.toList());

    private static final List<Map<String, Object>> COURIERS = IntStream.rangeClosed(2, 6)
            .mapToObj(id -> Map.<String, Object>of(
                    "courierId", id,
                    "email", "courier" + (id - 1) + "@fink.dev"))
            .collect(Collectors.toList());

    public static FeederBuilder<Object> customers() {
        return listFeeder(CUSTOMERS).circular();
    }

    public static FeederBuilder<Object> couriers() {
        return listFeeder(COURIERS).circular();
    }
}
