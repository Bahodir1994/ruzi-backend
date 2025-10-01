package app.ruzi.controller;

import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.entity.app.PurchaseOrder;
import app.ruzi.service.app.purchase.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/route-purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final HandlerService handlerService;
    private final PurchaseOrderService purchaseOrderService;

    @PostMapping("/data-table-main")
    public ResponseEntity<Object> read_table_data(@RequestBody @Valid DataTablesInput dataTablesInput) {
        DataTablesOutput<PurchaseOrder> privilegeDataTablesOutput = purchaseOrderService.readTablePurchaseOrder(dataTablesInput);
        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
    }
}

