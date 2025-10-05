package app.ruzi.service.app.warehouse;

import app.ruzi.entity.app.Warehouse;
import app.ruzi.repository.app.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WarehouseService implements WarehouseServiceImplement {
    private final WarehouseRepository warehouseRepository;

    @Override
    public DataTablesOutput<Warehouse> getWarehouse(DataTablesInput dataTablesInput) {
        return warehouseRepository.findAll(dataTablesInput);
    }

}
