//package app.ruzi.service.mappers;
//
//import app.ruzi.configuration.utils.CommonMapperUtils;
//import app.ruzi.entity.app.Item;
//import app.ruzi.service.payload.app.ProductDto;
//import app.ruzi.service.payload.app.ProductRequestDto;
//import org.mapstruct.*;
//import org.mapstruct.factory.Mappers;
//
//@Mapper(
//        uses = {CommonMapperUtils.class},
//        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
//        unmappedTargetPolicy = ReportingPolicy.IGNORE
//)
//public interface ProductMapper {
//    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
//
//    Item toEntity(ProductDto productDto);
//    Item toEntity(ProductRequestDto productRequestDto);
//
//    /*****************************************************/
//
//    ProductDto toDto(Item product);
//    ProductRequestDto toDto1(Item product);
//
//    /*****************************************************/
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    Item partialUpdate(ProductDto productDto, @MappingTarget Item product);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void partialUpdate(ProductRequestDto productRequestDto, @MappingTarget Item product);
//}