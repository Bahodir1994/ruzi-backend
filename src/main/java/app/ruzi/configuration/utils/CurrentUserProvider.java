package app.ruzi.configuration.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Request kontekstida hozirgi foydalanuvchi haqida ma'lumotni saqlaydi.
 * (Masalan: username va roles).
 *
 * Eslatma:
 *  - Bu klass har bir HTTP request uchun ishlaydi (ThreadLocal orqali)
 *  - So‘rov tugaganda .clear() chaqirish shart (masalan, filter yoki interceptor ichida)
 */
public class CurrentUserProvider {

    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> currentRoles = new ThreadLocal<>();

    // --- USER ---

    public static void setCurrentUser(String user) {
        currentUser.set(user);
    }

    public static String getCurrentUser() {
        return currentUser.get();
    }

    // --- ROLES ---

    public static void setCurrentRoles(List<String> roles) {
        currentRoles.set(roles != null ? new ArrayList<>(roles) : new ArrayList<>());
    }

    public static List<String> getCurrentRoles() {
        List<String> roles = currentRoles.get();
        return roles != null ? Collections.unmodifiableList(roles) : Collections.emptyList();
    }

    public static boolean hasRole(String role) {
        List<String> roles = currentRoles.get();
        if (roles == null) return false;
        return roles.stream().anyMatch(r -> r.equalsIgnoreCase(role));
    }

    public static boolean isSuperAdmin() {
        return hasRole("super_admin") || hasRole("ROLE_SUPER_ADMIN");
    }

    // --- CLEAR ---

    public static void clear() {
        currentUser.remove();
        currentRoles.remove();
    }
}
