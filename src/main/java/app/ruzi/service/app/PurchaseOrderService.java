package app.ruzi.service.app;

import app.ruzi.entity.PurchaseOrder;
import app.ruzi.repository.PurchaseOrderRepository;
import app.ruzi.service.mappers.PurchaseOrderMapper;
import app.ruzi.service.payload.PurchaseOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PurchaseOrderService implements PurchaseOrderServiceImplement{

    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<PurchaseOrder> read_table_data(DataTablesInput dataTablesInput) {
        return purchaseOrderRepository.findAll(dataTablesInput);
    }

    @Override
    @Transactional
    public PurchaseOrderDto create_purchase_data() {

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        return PurchaseOrderMapper.INSTANCE.toDto(purchaseOrder);
    }
}
