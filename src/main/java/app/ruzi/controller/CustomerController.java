package app.ruzi.controller;

import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.service.app.customer.CustomerService;
import app.ruzi.service.payload.app.CustomerDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route-customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final HandlerService handlerService;

    @GetMapping("/get-customers")
    @PreAuthorize("hasAuthority('ROLE_CUS_READ')")
    public ResponseEntity<?> getCustomers(
            @RequestHeader(value = "Accept-Language", required = false) String langType) {
        MessageResponse messageResponse = handlerService.handleRequest(
                customerService::getAllCustomers,
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PostMapping("/create-customer")
    @PreAuthorize("hasAuthority('ROLE_CUS_CREATE')")
    public ResponseEntity<Object> save(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @RequestBody CustomerDto customerDto
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> customerService.save(customerDto),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }
}
