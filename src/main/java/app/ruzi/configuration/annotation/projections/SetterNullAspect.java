package app.ruzi.configuration.annotation.projections;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
public class SetterNullAspect {

    @Pointcut("@annotation(setterNull)")
    public void applySetterNull(SetterNull setterNull) {
    }

    @AfterReturning(value = "applySetterNull(setterNull)", returning = "returnValue")
    public void processFields(ResponseEntity<?> returnValue, SetterNull setterNull) {
        Class<?> clazz = setterNull.className();
        String[] fieldsToNull = setterNull.listForSetNull();

        if (returnValue.getBody() instanceof DataTablesOutput<?>) {
            DataTablesOutput<?> dataTable = (DataTablesOutput<?>) returnValue.getBody();
            for (Object obj : dataTable.getData()) {
                if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
                    for (String fieldName : fieldsToNull) {
                        try {
                            Field field = obj.getClass().getDeclaredField(fieldName);
                            field.setAccessible(true);
                            field.set(obj, null);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace(); // Обработка исключения
                        }
                    }
                }
            }
        }
    }

}
