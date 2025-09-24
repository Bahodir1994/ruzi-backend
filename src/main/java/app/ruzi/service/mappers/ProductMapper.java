package app.ruzi.service.mappers;

import app.ruzi.configuration.utils.CommonMapperUtils;
import app.ruzi.entity.app.Product;
import app.ruzi.service.payload.app.ProductDto;
import app.ruzi.service.payload.app.ProductRequestDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        uses = {CommonMapperUtils.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toEntity(ProductDto productDto);
    Product toEntity(ProductRequestDto productRequestDto);

    /*****************************************************/

    ProductDto toDto(Product product);
    ProductRequestDto toDto1(Product product);

    /*****************************************************/

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product partialUpdate(ProductDto productDto, @MappingTarget Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(ProductRequestDto productRequestDto, @MappingTarget Product product);
}