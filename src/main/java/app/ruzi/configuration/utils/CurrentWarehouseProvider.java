package app.ruzi.configuration.utils;

public class CurrentWarehouseProvider {
    private static final ThreadLocal<String> currentWarehouse = new ThreadLocal<>();

    public static void setCurrentWarehouse(String warehouseId) {
        currentWarehouse.set(warehouseId);
    }

    public static String getCurrentWarehouse() {
        return currentWarehouse.get();
    }

    public static void clear() {
        currentWarehouse.remove();
    }
}

