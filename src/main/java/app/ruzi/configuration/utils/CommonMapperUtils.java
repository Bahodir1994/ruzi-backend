package app.ruzi.configuration.utils;

import app.ruzi.entity.app.Category;
import app.ruzi.entity.app.Warehouse;
import app.ruzi.entity.app.Client;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommonMapperUtils {

    // ==============================================================
    // 🔹 1. Basic converters (String → Number / Date / Boolean / etc.)
    // ==============================================================

    @Named("stringToDouble")
    public static Double stringToDouble(String doubles) {
        if (doubles == null || doubles.isBlank()) return null;
        try {
            return Double.parseDouble(doubles.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Named("stringToBigDecimal")
    public static BigDecimal stringToBigDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Named("stringToTimestamp")
    public static Timestamp stringToTimestamp(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Timestamp.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("stringToDate")
    public static Date stringToDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            // format: yyyy-MM-dd
            return new SimpleDateFormat("yyyy-MM-dd").parse(value.trim());
        } catch (ParseException e1) {
            try {
                // alternativ format: dd.MM.yyyy
                return new SimpleDateFormat("dd.MM.yyyy").parse(value.trim());
            } catch (ParseException e2) {
                return null;
            }
        }
    }

    @Named("stringToInteger")
    public static Integer stringToInteger(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Named("stringOrBlankToString")
    public static String stringOrBlankToString(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim();
    }

    @Named("stringToBoolean")
    public static Boolean stringToBoolean(String value) {
        if (value == null) return null;
        return Boolean.parseBoolean(value.trim().toLowerCase());
    }

    @Named("defaultZero")
    public static Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    // ==============================================================
    // 🔹 2. Entity reference converters (String ID → Entity Object)
    // ==============================================================

    @Named("stringToCategory")
    public static Category stringToCategory(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) return null;
        return Category.builder().id(categoryId.trim()).build();
    }

    @Named("stringToWarehouse")
    public static Warehouse stringToWarehouse(String id) {
        if (id == null || id.isBlank()) return null;
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id.trim());
        return warehouse;
    }

    @Named("stringToClient")
    public static Client stringToClient(String id) {
        if (id == null || id.isBlank()) return null;
        Client client = new Client();
        client.setId(id.trim());
        return client;
    }

    // ==============================================================
    // 🔹 3. Generic filters for List/Set (isDeleted = 0)
    // ==============================================================

    @Named("filterDeletedList")
    public static <E, D> List<D> filterDeletedList(
            List<E> entities,
            Function<E, Integer> getDeleted,
            Function<E, D> mapper
    ) {
        if (entities == null) return null;
        return entities.stream()
                .filter(e -> getDeleted.apply(e) != null && getDeleted.apply(e) == 0)
                .map(mapper)
                .toList();
    }

    @Named("filterDeletedSet")
    public static <E, D> Set<D> filterDeletedSet(
            Set<E> entities,
            Function<E, Integer> getDeleted,
            Function<E, D> mapper
    ) {
        if (entities == null) return null;
        return entities.stream()
                .filter(e -> getDeleted.apply(e) != null && getDeleted.apply(e) == 0)
                .map(mapper)
                .collect(Collectors.toSet());
    }
}
