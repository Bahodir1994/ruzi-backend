package app.ruzi.service.app.purchase;

import app.ruzi.entity.app.PurchaseOrder;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

public interface PurchaseOrderServiceImplement {

    DataTablesOutput<PurchaseOrder> readTablePurchaseOrder(DataTablesInput dataTablesInput);

    void dasdasdasd();

}
