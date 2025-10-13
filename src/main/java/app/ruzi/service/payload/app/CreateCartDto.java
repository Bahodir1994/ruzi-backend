package app.ruzi.service.payload.app;

public record CreateCartDto(
        String activeSessionId,
        Boolean  forceNew
) {}

