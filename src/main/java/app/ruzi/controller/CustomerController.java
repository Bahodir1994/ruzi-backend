package app.ruzi.controller;

import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.entity.app.Customer;
import app.ruzi.entity.app.Item;
import app.ruzi.service.app.customer.CustomerService;
import app.ruzi.service.payload.app.CustomerDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/get-customer/{id}")
    @PreAuthorize("hasAuthority('ROLE_CUS_READ')")
    public ResponseEntity<?> getCustomer(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("id") String id
            ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> customerService.getCustomerByCart(id),
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

    @PostMapping("/data-table-main")
    @PreAuthorize("hasAuthority('ROLE_CUS_READ')")
    public ResponseEntity<Object> read_table_data(@RequestBody @Valid DataTablesInput dataTablesInput) {
        DataTablesOutput<Customer> privilegeDataTablesOutput = customerService.dataTableMain(dataTablesInput);
        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
    }
}
