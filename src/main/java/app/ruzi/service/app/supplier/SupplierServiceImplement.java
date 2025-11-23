package app.ruzi.service.app.supplier;

import app.ruzi.entity.app.Supplier;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface SupplierServiceImplement {

    List<Supplier> readList();

    DataTablesOutput<Supplier> getSupplier(DataTablesInput dataTablesInput);

}
