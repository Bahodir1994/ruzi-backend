package app.ruzi.service.app.warehouse;

import app.ruzi.entity.app.Warehouse;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

public interface WarehouseServiceImplement {

    DataTablesOutput<Warehouse> getWarehouse(DataTablesInput dataTablesInput);

}
