package app.ruzi.service.mappers;

import app.ruzi.configuration.utils.CommonMapperUtils;
import app.ruzi.entity.app.PurchaseOrder;
import app.ruzi.service.payload.app.PurchaseOrderUpdateDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        uses = {CommonMapperUtils.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PurchaseOrderMapper {
    PurchaseOrderMapper INSTANCE = Mappers.getMapper(PurchaseOrderMapper.class);

    @Mapping(target = "supplier", source = "supplierId", qualifiedByName = "stringToSupplier")
    @Mapping(target = "warehouse", source = "warehouseId", qualifiedByName = "stringToWarehouse")
    @Mapping(target = "dueDate", source = "dueDate", qualifiedByName = "stringToDate")
    @Mapping(target = "paidAmount", source = "paidAmount", qualifiedByName = "stringToBigDecimal")
    PurchaseOrder toEntity(PurchaseOrderUpdateDto purchaseOrderDto);

    PurchaseOrderUpdateDto toDto(PurchaseOrder purchaseOrder);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(PurchaseOrder source, @MappingTarget PurchaseOrder target);
}