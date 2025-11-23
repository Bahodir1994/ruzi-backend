package app.ruzi.service.app.warehouse;

import app.ruzi.entity.app.Warehouse;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface WarehouseServiceImplement {

    List<Warehouse> readList();

    DataTablesOutput<Warehouse> getWarehouse(DataTablesInput dataTablesInput);

}
