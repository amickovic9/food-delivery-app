package loadtest.support;

import java.time.Duration;

public final class Params {

    private Params() {
    }

    public static int intProp(String name, int def) {
        String v = System.getProperty(name);
        return v == null || v.isBlank() ? def : Integer.parseInt(v.trim());
    }

    public static double doubleProp(String name, double def) {
        String v = System.getProperty(name);
        return v == null || v.isBlank() ? def : Double.parseDouble(v.trim());
    }

    public static Duration seconds(String name, int defSeconds) {
        return Duration.ofSeconds(intProp(name, defSeconds));
    }
}
