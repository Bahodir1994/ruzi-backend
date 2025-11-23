package app.ruzi.controller;

import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.entity.app.PurchaseOrder;
import app.ruzi.entity.app.PurchaseOrderItem;
import app.ruzi.service.app.purchase.PurchaseOrderService;
import app.ruzi.service.payload.app.CreatePurchaseOrderItemDto;
import app.ruzi.service.payload.app.ItemDto;
import app.ruzi.service.payload.app.PurchaseOrderCreatReadDto;
import app.ruzi.service.payload.app.UpdateFieldDto;
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

    @GetMapping("/read-order/{id}")
    @PreAuthorize("hasAuthority('ROLE_PUR_READ')")
    public ResponseEntity<Object> read(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("id") String id
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> purchaseOrderService.read(id),
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

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

    @DeleteMapping("/delete-order/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_PUR_DELETE')")
    public ResponseEntity<Object> deleteOrder(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("orderId") String orderId
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> purchaseOrderService.deleteOrder(orderId),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PostMapping("/add-item")
    @PreAuthorize("hasAuthority('ROLE_PUR_CREATE')")
    public ResponseEntity<Object> addItemToOrder(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @RequestBody CreatePurchaseOrderItemDto dto
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> purchaseOrderService.addItemToOrder(dto),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PatchMapping("/update-order-item/{id}")
    @PreAuthorize("hasAuthority('ROLE_PUR_UPDATE')")
    public ResponseEntity<Object> updateOrderItem(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable String id,
            @Valid @RequestBody UpdateFieldDto creatReadDto,
            BindingResult bindingResult
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> purchaseOrderService.updateItem(id, creatReadDto),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @DeleteMapping("/delete-order-item/{orderId}/{itemId}")
    @PreAuthorize("hasAuthority('ROLE_PUR_DELETE')")
    public ResponseEntity<Object> deleteItemFromOrder(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("orderId") String orderId,
            @PathVariable("itemId") String itemId
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> purchaseOrderService.deleteItemFromOrder(orderId, itemId),
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

    @PostMapping("/data-table-pur-order")
    @PreAuthorize("hasAuthority('ROLE_PUR_READ')")
    public ResponseEntity<Object> read_table_pur_order(@RequestBody @Valid DataTablesInput dataTablesInput) {
        DataTablesOutput<PurchaseOrderItem> privilegeDataTablesOutput = purchaseOrderService.readTablePurchaseOrderItem(dataTablesInput);
        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
    }
}

