package app.ruzi.configuration.permission;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.entity.tasks.SqlLogics;
import app.ruzi.repository.tasks.SqlLogicsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ControllerMappingService implements ControllerMappingServiceImplement {

    private final JwtUtils jwtUtils;
    private final SqlLogicsRepository sqlLogicsRepository;
    private static final Pattern PARAM_PATTERN = Pattern.compile(":(\\w+)");

    @PersistenceContext
    private EntityManager entityManager;

    public ControllerMappingService(JwtUtils jwtUtils, SqlLogicsRepository sqlLogicsRepository) {
        this.jwtUtils = jwtUtils;
        this.sqlLogicsRepository = sqlLogicsRepository;
    }

    @Override
    public void handleRequestWithSql(String methodName, Supplier<Object> supplier) throws ParseException {
        List<SqlLogics> byGroupName = sqlLogicsRepository.findByGroupName(methodName);

        Object body = supplier.get();
        UserJwt userJwt = jwtUtils.extractUserFromToken();
        String userShortName = userJwt != null ? userJwt.getUsername() : null;

        for (SqlLogics logic : byGroupName) {
            String sqlQuery = logic.getQueryText();
            Map<String, Object> queryParams = parseParamsFromJson(logic.getQueryParams());
            Set<String> sqlParams = extractParameters(sqlQuery);
            Map<String, Object> extractedParameters = bindParameters(body, sqlParams);

            for (String param : queryParams.keySet()) {
                if (!extractedParameters.containsKey(param)) {
                    extractedParameters.put(param, queryParams.get(param));
                }
            }

//        if (userJwt != null) {
//            extractedParameters.put("unicode", userShortName);
//        }

//        for (String param : sqlParams) {
//            PrivilegeStatusEnum statusEnum = getStatusEnumFromParam(param);
//            if (statusEnum != null) {
//                extractedParameters.put(param, statusEnum.getCode());
//            }
//        }

            for (Map.Entry<String, Object> entry : extractedParameters.entrySet()) {
                String paramName = ":" + entry.getKey();
                Object value = entry.getValue();
                sqlQuery = sqlQuery.replace(paramName, formatValueForSql(value));
            }

            Query query = entityManager.createNativeQuery(sqlQuery);
            Object result = query.getSingleResult();

            if (!(result instanceof Number) || ((Number) result).intValue() != 1) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, logic.getMessageCode());
            }
        }
    }

    //private PrivilegeStatusEnum getStatusEnumFromParam(String param) {
    //    try {
    //        return PrivilegeStatusEnum.valueOf(param);
    //    } catch (IllegalArgumentException e) {
    //        return null;
    //    }
    //}

    public Set<String> extractParameters(String sql) {
        Set<String> parameters = new HashSet<>();
        Matcher matcher = PARAM_PATTERN.matcher(sql);
        while (matcher.find()) {
            parameters.add(matcher.group(1));
        }
        return parameters;
    }

    public Map<String, Object> parseParamsFromJson(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> paramMap = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(json);
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                paramMap.put(field.getKey(), field.getValue().asText());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON parameters: " + json, e);
        }

        return paramMap;
    }

    public Map<String, Object> bindParameters(Object body, Set<String> sqlParams) {
        Map<String, Object> boundParams = new HashMap<>();

        if (body == null) {
            return boundParams;
        }

        for (String param : sqlParams) {
            try {
                Field field = body.getClass().getDeclaredField(param);
                field.setAccessible(true);
                Object value = field.get(body);
                boundParams.put(param, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                boundParams.put(param, null);
            }
        }

        return boundParams;
    }

    private String formatValueForSql(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String) {
            return "'" + value.toString().replace("'", "''") + "'";
        }
        return value.toString();
    }
}
