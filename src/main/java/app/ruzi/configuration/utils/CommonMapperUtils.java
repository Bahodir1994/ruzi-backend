package app.ruzi.configuration.utils;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommonMapperUtils {

    // ----------------- Helpers -----------------

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
            // format: "2025-08-12 14:30:00"
            return Timestamp.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null; // noto‘g‘ri format bo‘lsa null qaytaradi
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

    @Named("stringToBigDecimalSafe")
    public static BigDecimal stringToBigDecimalSafe(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Named("defaultZero")
    public static Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    @Named("stringToBoolean")
    public static Boolean stringToBoolean(String value) {
        if (value == null) return null; // null kelganda null qaytaradi
        return Boolean.parseBoolean(value.trim().toLowerCase());
    }


    /**
     * Generic List filter: isDeleted = 0 bo‘lganlarni qoldiradi
     */
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

    /**
     * Generic Set filter: isDeleted = 0 bo‘lganlarni qoldiradi
     */
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
