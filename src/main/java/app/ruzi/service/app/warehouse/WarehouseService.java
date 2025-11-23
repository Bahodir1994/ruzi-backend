package app.ruzi.service.app.warehouse;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.entity.app.Supplier;
import app.ruzi.entity.app.Warehouse;
import app.ruzi.repository.app.SupplierRepository;
import app.ruzi.repository.app.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService implements WarehouseServiceImplement {
    private final JwtUtils jwtUtils;
    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> readList() {
        UserJwt userJwt = jwtUtils.extractUserFromToken();

        return warehouseRepository.findAllByClient_Id(userJwt.getClientId());
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<Warehouse> getWarehouse(DataTablesInput dataTablesInput) {
        UserJwt userJwt = jwtUtils.extractUserFromToken();

        return warehouseRepository.findAll(
                dataTablesInput,
                (root, query, cb) ->
                        cb.equal(root.get("client").get("id"), userJwt.getClientId())
        );
    }


}
