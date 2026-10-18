package vn.dafl.commons;

public class GlobalConstants {
    public static final long SHORT_TIMEOUT = 5;
    public static final long LONG_TIMEOUT = 15;
    public static final String DEFAULT_APP_URL = "http://localhost:3000";

    public static String getAppUrl() {
        String propUrl = System.getProperty("app.url");
        if (propUrl != null && !propUrl.trim().isEmpty()) {
            return propUrl.trim();
        }
        String envUrl = System.getenv("APP_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            return envUrl.trim();
        }
        return DEFAULT_APP_URL;
    }
}
