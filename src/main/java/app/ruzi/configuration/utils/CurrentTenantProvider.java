package app.ruzi.configuration.utils;

public class CurrentTenantProvider {
    private static final ThreadLocal<String> currentClient = new ThreadLocal<>();

    public static void setCurrentClient(String clientId) {
        currentClient.set(clientId);
    }

    public static String getCurrentClient() {
        return currentClient.get();
    }

    public static void clear() {
        currentClient.remove();
    }
}

