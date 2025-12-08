package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.CustomAuthRole;
import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.service.app.referrer.ReferrerService;
import app.ruzi.service.payload.app.CustomerDto;
import app.ruzi.service.payload.app.ReferrerDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route-referrer")
@RequiredArgsConstructor
public class ReferrerController {

    private final ReferrerService referrerService;
    private final HandlerService handlerService;

    @GetMapping("/get-referrers")
    @CustomAuthRole(roles = {"ROLE_REF_READ"})
    public ResponseEntity<?> getReferrers(
            @RequestHeader(value = "Accept-Language", required = false) String langType) {
        MessageResponse messageResponse = handlerService.handleRequest(
                referrerService::getAllReferrers,
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @GetMapping("/get-referrer/{id}")
    @PreAuthorize("hasAuthority('ROLE_REF_READ')")
    public ResponseEntity<?> getReferrer(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("id") String id
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> referrerService.getReferrerByCart(id),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PostMapping("/create-referrer")
    @PreAuthorize("hasAuthority('ROLE_REF_CREATE')")
    public ResponseEntity<Object> save(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @RequestBody ReferrerDto referrerDto
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> referrerService.create(referrerDto),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

}
