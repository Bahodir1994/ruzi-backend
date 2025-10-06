package app.ruzi.service.app.supplier;

import app.ruzi.entity.app.Supplier;
import app.ruzi.repository.app.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierService implements SupplierServiceImplement {
    private final SupplierRepository supplierRepository;

    @Override
    public DataTablesOutput<Supplier> getSupplier(DataTablesInput dataTablesInput) {
        return supplierRepository.findAll(dataTablesInput);
    }

}
