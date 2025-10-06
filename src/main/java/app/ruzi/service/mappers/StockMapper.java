package app.ruzi.service.mappers;

import app.ruzi.configuration.utils.CommonMapperUtils;
import app.ruzi.entity.app.Stock;
import app.ruzi.service.payload.app.StockViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        uses = {CommonMapperUtils.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StockMapper {
    StockMapper INSTANCE = Mappers.getMapper(StockMapper.class);

    @Mapping(target = "stockId", source = "id")
    @Mapping(target = "warehouseId", source = "warehouse.id")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    @Mapping(target = "warehouseCode", source = "warehouse.id")
    @Mapping(target = "purchaseOrderItemId", source = "purchaseOrderItem.id")
    @Mapping(target = "itemId", source = "purchaseOrderItem.item.id")
    @Mapping(target = "itemCode", source = "purchaseOrderItem.item.code")
    @Mapping(target = "itemName", source = "purchaseOrderItem.item.name")
    @Mapping(target = "barcode", source = "purchaseOrderItem.item.barcode")
    @Mapping(target = "unitName", source = "purchaseOrderItem.unitCode")
    @Mapping(target = "categoryName", source = "purchaseOrderItem.item.category.code")
    @Mapping(target = "salePrice", source = "purchaseOrderItem.salePrice")
    @Mapping(target = "minimalSum", source = "purchaseOrderItem.minimalSum")
    @Mapping(target = "purchasePrice", source = "purchaseOrderItem.purchasePrice")
    @Mapping(target = "batchNumber", source = "purchaseOrderItem.batchNumber")
    @Mapping(target = "expiryDate", source = "purchaseOrderItem.expiryDate")
    @Mapping(target = "discount", source = "purchaseOrderItem.discount")
    @Mapping(target = "imageUrl", source = "purchaseOrderItem.item.primaryImageUrl")
    @Mapping(target = "clientId", source = "client.id")
    StockViewDto toDto(Stock entity);

    List<StockViewDto> toDtoList(List<Stock> stockList);
}