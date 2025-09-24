package app.ruzi.controller;

import app.ruzi.configuration.messaging.HandlerService;
//import app.ruzi.entity.PurchaseOrder;
//import app.ruzi.service.app.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final HandlerService handlerService;
//    private final PurchaseOrderService purchaseOrderService;

//    @PostMapping("/read-table-data")
//    public ResponseEntity<Object> read_table_data(@RequestBody @Valid DataTablesInput dataTablesInput) {
//        DataTablesOutput<PurchaseOrder> privilegeDataTablesOutput = purchaseOrderService.read_table_data(dataTablesInput);
//        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
//    }



//    @GetMapping
//    public ResponseEntity<List<PurchaseOrderDto>> getAll() { return ResponseEntity.ok().build(); }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<PurchaseOrderDto> getById(@PathVariable Long id) { return ResponseEntity.ok().build(); }
//
//    @PostMapping
//    public ResponseEntity<PurchaseOrderDto> create(@RequestBody PurchaseOrderDto dto) { return ResponseEntity.ok().build(); }
//
//    @PostMapping("/{orderId}/items")
//    public ResponseEntity<PurchaseOrderItemDto> addItem(@PathVariable Long orderId, @RequestBody PurchaseOrderItemDto dto) { return ResponseEntity.ok().build(); }
//
//    @PostMapping("/{orderId}/approve")
//    public ResponseEntity<Void> approve(@PathVariable Long orderId) { return ResponseEntity.ok().build(); }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> cancel(@PathVariable Long id) { return ResponseEntity.noContent().build(); }
}

