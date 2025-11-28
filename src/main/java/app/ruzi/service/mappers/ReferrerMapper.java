package app.ruzi.service.mappers;

import app.ruzi.configuration.utils.CommonMapperUtils;
import app.ruzi.entity.app.Referrer;
import app.ruzi.service.payload.app.ReferrerDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        uses = {CommonMapperUtils.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)public interface ReferrerMapper {
    ReferrerMapper INSTANCE = Mappers.getMapper(ReferrerMapper.class);

    Referrer toEntity(ReferrerDto referrerDto);

    ReferrerDto toDto(Referrer referrer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Referrer partialUpdate(ReferrerDto referrerDto, @MappingTarget Referrer referrer);
}