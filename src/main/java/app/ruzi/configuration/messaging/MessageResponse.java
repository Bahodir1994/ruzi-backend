package app.ruzi.configuration.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String message;
    private Boolean success;
    private Object data;
    private HttpStatus status;
}
