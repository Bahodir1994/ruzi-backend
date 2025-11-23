package app.ruzi.service.app.supplier;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.entity.app.Supplier;
import app.ruzi.repository.app.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService implements SupplierServiceImplement {
    private final JwtUtils jwtUtils;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> readList() {
        UserJwt userJwt = jwtUtils.extractUserFromToken();

        return supplierRepository.findAllByClient_Id(userJwt.getClientId());
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<Supplier> getSupplier(DataTablesInput input) {
        UserJwt user = jwtUtils.extractUserFromToken();

        return supplierRepository.findAll(
                input,
                (root, query, cb) -> cb.equal(root.get("client").get("id"), user.getClientId())
        );
    }

}
