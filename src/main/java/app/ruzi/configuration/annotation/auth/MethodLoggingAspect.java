package app.ruzi.configuration.annotation.auth;

import app.ruzi.configuration.permission.ControllerMappingService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class MethodLoggingAspect {
    private final ControllerMappingService controllerMappingService;

    @Before("@annotation(methodInfo)")
    public void handleSqlChecks(JoinPoint joinPoint, MethodInfo methodInfo) throws ParseException {
        String methodName = methodInfo.methodName();

        Object dto = Arrays.stream(joinPoint.getArgs())
                .filter(this::isCustomClass)
                .findFirst()
                .orElse(null);

        controllerMappingService.handleRequestWithSql(methodName, () -> dto);
    }

    private boolean isCustomClass(Object arg) {
        if (arg == null) {
            return false;
        }
        Class<?> clazz = arg.getClass();
        return !clazz.isPrimitive() && !clazz.getName().startsWith("java.");
    }
}
