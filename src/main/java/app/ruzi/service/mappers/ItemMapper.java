package app.ruzi.service.mappers;

import app.ruzi.configuration.utils.CommonMapperUtils;
import app.ruzi.entity.app.Item;
import app.ruzi.service.payload.app.ItemRequestDto;
import app.ruzi.service.payload.app.ItemDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        uses = {CommonMapperUtils.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    /*****************************************************
     * 🧩 DTO → ENTITY (yaratish uchun)
     *****************************************************/
    @Mapping(target = "price", source = "price", qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "isActive", source = "isActive", qualifiedByName = "stringToBoolean")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "stringToCategory")
    @Mapping(target = "code", source = "code", qualifiedByName = "stringOrBlankToString")
    @Mapping(target = "name", source = "name", qualifiedByName = "stringOrBlankToString")
    @Mapping(target = "primaryImageUrl", source = "primaryImageUrl", qualifiedByName = "stringOrBlankToString")
    @Mapping(target = "skuCode", source = "skuCode", qualifiedByName = "stringOrBlankToString")
    @Mapping(target = "barcode", source = "barcode", qualifiedByName = "stringOrBlankToString")
    @Mapping(target = "brand", source = "brand", qualifiedByName = "stringOrBlankToString")
    @Mapping(target = "unit", source = "unit", qualifiedByName = "stringOrBlankToString")
    @Mapping(target = "description", source = "description", qualifiedByName = "stringOrBlankToString")
    @Mapping(target = "client", ignore = true)
    Item toEntity(ItemRequestDto dto);

    /*****************************************************
     * 🧩 ENTITY → DTO (javob uchun)
     *****************************************************/
    ItemDto toDto(Item entity);

    /*****************************************************
     * 🧩 PATCH (qisman yangilash uchun)
     *****************************************************/
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item partialUpdate(ItemDto dto, @MappingTarget Item entity);

}
