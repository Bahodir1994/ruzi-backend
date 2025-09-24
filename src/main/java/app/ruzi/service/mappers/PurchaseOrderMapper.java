//package app.ruzi.service.mappers;
//
//import app.ruzi.configuration.utils.CommonMapperUtils;
//import app.ruzi.entity.PurchaseOrder;
//import app.ruzi.service.payload.PurchaseOrderDto;
//import org.mapstruct.*;
//import org.mapstruct.factory.Mappers;
//
//@Mapper(
//        uses = {CommonMapperUtils.class},
//        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
//        unmappedTargetPolicy = ReportingPolicy.IGNORE
//)
//public interface PurchaseOrderMapper {
//    PurchaseOrderMapper INSTANCE = Mappers.getMapper(PurchaseOrderMapper.class);
//
//    PurchaseOrder toEntity(PurchaseOrderDto purchaseOrderDto);
//
//    PurchaseOrderDto toDto(PurchaseOrder purchaseOrder);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    PurchaseOrder partialUpdate(PurchaseOrderDto purchaseOrderDto, @MappingTarget PurchaseOrder purchaseOrder);
//}