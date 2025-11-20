package app.ruzi.service.app.purchase;

import app.ruzi.entity.app.PurchaseOrder;
import app.ruzi.service.payload.app.PurchaseOrderCreatReadDto;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface PurchaseOrderServiceImplement {

    /* crud */
    PurchaseOrder create(PurchaseOrderCreatReadDto requestDTO);

    PurchaseOrder read(String id);

    void update(PurchaseOrderCreatReadDto requestDTO);

    void delete(List<String> idList);


    /* datatables */
    DataTablesOutput<PurchaseOrder> readTablePurchaseOrder(DataTablesInput dataTablesInput);
}
