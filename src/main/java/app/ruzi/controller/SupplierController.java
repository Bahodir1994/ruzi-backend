package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.entity.app.Supplier;
import app.ruzi.service.app.supplier.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route-supplier")
@RequiredArgsConstructor
public class SupplierController {
    private final HandlerService handlerService;

    private final SupplierService supplierService;

    @GetMapping("/get-suppliers")
    @PreAuthorize("hasAuthority('ROLE_SUP_READ')")
    public ResponseEntity<Object> getSuppliers(
            @RequestHeader(value = "Accept-Language", required = false) String langType) {
        MessageResponse messageResponse = handlerService.handleRequest(
                supplierService::readList,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PostMapping("/data-table-main")
    @PreAuthorize("hasAuthority('ROLE_SUP_READ')")
    public ResponseEntity<Object> read_table_data(@RequestBody @Valid DataTablesInput dataTablesInput) {
        DataTablesOutput<Supplier> privilegeDataTablesOutput = supplierService.getSupplier(dataTablesInput);
        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
    }
}
