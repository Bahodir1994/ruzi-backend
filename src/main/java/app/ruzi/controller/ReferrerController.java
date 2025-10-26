package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.CustomAuthRole;
import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.service.app.referrer.ReferrerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/route-referrer")
@RequiredArgsConstructor
public class ReferrerController {

    private final ReferrerService referrerService;
    private final HandlerService handlerService;

    @GetMapping("/get-referrers")
    @CustomAuthRole(roles = {"ROLE_CART_CREATE"})
    @MethodInfo(methodName = "get-item-quantity")
    public ResponseEntity<?> getReferrers(
            @RequestHeader(value = "Accept-Language", required = false) String langType) {
        MessageResponse messageResponse = handlerService.handleRequest(
                referrerService::getAllReferrers,
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

}
