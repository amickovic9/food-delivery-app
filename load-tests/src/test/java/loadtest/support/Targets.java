package loadtest.support;

public final class Targets {

    private static final String BASE = System.getProperty("baseUrl");

    public static final String AUTH = resolve("authUrl", "http://localhost:8081");
    public static final String RESTAURANT = resolve("restaurantUrl", "http://localhost:8082");
    public static final String ORDER = resolve("orderUrl", "http://localhost:8086");

    private Targets() {
    }

    private static String resolve(String prop, String microDefault) {
        String explicit = System.getProperty(prop);
        if (explicit != null && !explicit.isBlank()) {
            return explicit;
        }
        return BASE != null && !BASE.isBlank() ? BASE : microDefault;
    }

    public static String describe() {
        return BASE != null && !BASE.isBlank()
                ? "monolith @ " + BASE
                : "microservices @ auth=" + AUTH + " restaurant=" + RESTAURANT + " order=" + ORDER;
    }
}
