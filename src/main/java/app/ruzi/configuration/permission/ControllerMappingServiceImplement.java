package app.ruzi.configuration.permission;

import java.text.ParseException;
import java.util.function.Supplier;

public interface ControllerMappingServiceImplement {

    void handleRequestWithSql(String methodName, Supplier<Object> supplier) throws ParseException;

}
