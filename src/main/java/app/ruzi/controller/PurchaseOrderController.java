package app.ruzi.controller;

import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.entity.app.PurchaseOrder;
import app.ruzi.service.app.purchase.PurchaseOrderService;
import app.ruzi.service.payload.app.ItemDto;
import app.ruzi.service.payload.app.PurchaseOrderCreatReadDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route-purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final HandlerService handlerService;
    private final PurchaseOrderService purchaseOrderService;

    @PostMapping("/create-order")
    @PreAuthorize("hasAuthority('ROLE_PUR_CREATE')")
    public ResponseEntity<Object> create(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @RequestBody PurchaseOrderCreatReadDto requestDTO,
            BindingResult bindingResult
    ) {

        MessageResponse messageResponse = handlerService.handleRequest(
                () -> purchaseOrderService.create(requestDTO),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PutMapping("/update-order/{id}")
    @PreAuthorize("hasAuthority('ROLE_PUR_UPDATE')")
    public ResponseEntity<Object> update(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable String id,
            @Valid @RequestBody PurchaseOrderCreatReadDto creatReadDto,
            BindingResult bindingResult
    ) {
        creatReadDto.setId(id);
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> purchaseOrderService.update(creatReadDto),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }


    @PostMapping("/data-table-main")
    @PreAuthorize("hasAuthority('ROLE_PUR_READ')")
    public ResponseEntity<Object> read_table_data(@RequestBody @Valid DataTablesInput dataTablesInput) {
        DataTablesOutput<PurchaseOrder> privilegeDataTablesOutput = purchaseOrderService.readTablePurchaseOrder(dataTablesInput);
        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
    }
}

