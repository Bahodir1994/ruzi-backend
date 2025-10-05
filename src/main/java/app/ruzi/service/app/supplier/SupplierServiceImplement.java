package app.ruzi.service.app.supplier;

import app.ruzi.entity.app.Supplier;
import app.ruzi.entity.app.Warehouse;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

public interface SupplierServiceImplement {

    DataTablesOutput<Supplier> getSupplier(DataTablesInput dataTablesInput);

}
