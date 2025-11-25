package app.ruzi.service.app.purchase;

import app.ruzi.entity.app.PurchaseOrder;
import app.ruzi.entity.app.PurchaseOrderItem;
import app.ruzi.service.payload.app.CreatePurchaseOrderItemDto;
import app.ruzi.service.payload.app.PurchaseOrderCreatReadDto;
import app.ruzi.service.payload.app.PurchaseOrderUpdateDto;
import app.ruzi.service.payload.app.UpdateFieldDto;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface PurchaseOrderServiceImplement {

    /* crud */
    PurchaseOrder create(PurchaseOrderCreatReadDto requestDTO);

    PurchaseOrder read(String id);

    void update(PurchaseOrderUpdateDto updateDto);

    void updateItem(String id, UpdateFieldDto dto);

    void deleteOrder(String orderId);

    void deleteItemFromOrder(String orderId, String itemId);

    void addItemToOrder(CreatePurchaseOrderItemDto dto);

    void recalculateOrderTotals(PurchaseOrder purchaseOrder);

    /* datatables */
    DataTablesOutput<PurchaseOrder> readTablePurchaseOrder(DataTablesInput dataTablesInput);

    DataTablesOutput<PurchaseOrderItem> readTablePurchaseOrderItem(DataTablesInput dataTablesInput);
}
